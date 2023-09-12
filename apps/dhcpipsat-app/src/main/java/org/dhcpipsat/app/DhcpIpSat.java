/*
 * Copyright 2022-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author edoardottt, https://www.edoardoottavianelli.it/
 */

package org.onosproject.dhcpipsat;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onosproject.core.CoreService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.host.HostStore;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.Device;
import org.onosproject.net.Port;
import java.util.Timer;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import org.onlab.packet.ARP;
import org.onlab.packet.DHCP;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.Ip4Address;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.TpPort;
import org.onlab.packet.UDP;
import org.onlab.packet.VlanId;
import org.onlab.packet.dhcp.DhcpOption;
import org.onlab.util.SharedScheduledExecutors;
import org.onlab.util.Tools;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.host.DefaultHostDescription;
import org.onosproject.net.host.HostProvider;
import org.onosproject.net.host.HostProviderRegistry;
import org.onosproject.net.host.HostProviderService;
import org.onosproject.net.packet.DefaultOutboundPacket;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import java.util.concurrent.ExecutorService;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketPriority;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.onlab.util.Tools.groupedThreads;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/*
 * ONOS-IPSAT
 */
import org.onosproject.security.IpAddressPoolStore;

/**
 * DHCP Server IP Saturation.
 */
@Component(immediate = true)
public class DhcpIpSat {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostStore hostStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceStore deviceStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected IpAddressPoolStore ipAddressPoolStore;

    protected ExecutorService eventExecutor;

    private final InternalHostListener hostListener = new InternalHostListener();

    // private DhcpPacketProcessor processor = new DhcpPacketProcessor();

    private ApplicationId appId;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.onosproject.dhcpipsat.app", () -> log.info("Periscope down."));
        // packetService.addProcessor(processor, PacketProcessor.director(1));
        // requestPackets();
        eventExecutor = newSingleThreadScheduledExecutor(groupedThreads("onos/dhcpipsat", "events-%d", log));
        hostService.addListener(hostListener);
        log.info("Started dhcpipsat App!");
    }

    @Deactivate
    protected void deactivate() {
        // packetService.removeProcessor(processor);
        // cancelPackets();
        hostService.removeListener(hostListener);
        eventExecutor.shutdownNow();
        eventExecutor = null;
        log.info("Stopped dhcpipsat App!");
    }

    private class InternalHostListener implements HostListener {
        @Override
        public void event(HostEvent event) {
            eventExecutor.execute(() -> {
                // read from host data store which IP addresses have been taken
                // ArrayList<String> ips = getIpAddresses();
                log.info("received EVENT!");
                Host h = event.subject();
                ArrayList<String> ipList = new ArrayList<String>();
                for (IpAddress ipa : h.ipAddresses()) {
                    ipList.add(ipa.toString());
                }

                // update the IP address pool store
                for (String ip : ipList) {
                    int lastDigits = Integer.valueOf(ip.split("\\.")[3]);
                    ipAddressPoolStore.ipTaken(lastDigits);
                }
            });
        }

    }

    /*
     * private void requestPackets() {
     * 
     * TrafficSelector.Builder selectorServer = DefaultTrafficSelector.builder()
     * .matchEthType(Ethernet.TYPE_IPV4)
     * .matchIPProtocol(IPv4.PROTOCOL_UDP)
     * .matchUdpDst(TpPort.tpPort(UDP.DHCP_SERVER_PORT))
     * .matchUdpSrc(TpPort.tpPort(UDP.DHCP_CLIENT_PORT));
     * packetService.requestPackets(selectorServer.build(), PacketPriority.CONTROL,
     * appId);
     * 
     * selectorServer = DefaultTrafficSelector.builder()
     * .matchEthType(Ethernet.TYPE_ARP);
     * packetService.requestPackets(selectorServer.build(), PacketPriority.CONTROL,
     * appId);
     * }
     * 
     * private void cancelPackets() {
     * TrafficSelector.Builder selectorServer = DefaultTrafficSelector.builder()
     * .matchEthType(Ethernet.TYPE_IPV4)
     * .matchIPProtocol(IPv4.PROTOCOL_UDP)
     * .matchUdpDst(TpPort.tpPort(UDP.DHCP_SERVER_PORT))
     * .matchUdpSrc(TpPort.tpPort(UDP.DHCP_CLIENT_PORT));
     * packetService.cancelPackets(selectorServer.build(), PacketPriority.CONTROL,
     * appId);
     * 
     * selectorServer = DefaultTrafficSelector.builder()
     * .matchEthType(Ethernet.TYPE_ARP);
     * packetService.cancelPackets(selectorServer.build(), PacketPriority.CONTROL,
     * appId);
     * }
     * 
     * private class DhcpPacketProcessor implements PacketProcessor {
     * 
     * @Override
     * public void process(PacketContext context) {
     * Ethernet packet = context.inPacket().parsed();
     * if (packet.getEtherType() == Ethernet.TYPE_IPV4) {
     * IPv4 ipv4Packet = (IPv4) packet.getPayload();
     * 
     * if (ipv4Packet.getProtocol() == IPv4.PROTOCOL_UDP) {
     * UDP udpPacket = (UDP) ipv4Packet.getPayload();
     * 
     * if (udpPacket.getDestinationPort() == UDP.DHCP_SERVER_PORT ||
     * udpPacket.getSourcePort() == UDP.DHCP_CLIENT_PORT) {
     * // This is meant for the dhcp server so process the packet here.
     * log.info("received DHCP packet!");
     * 
     * // read from host data store which IP addresses have been taken
     * ArrayList<String> ips = getIpAddresses();
     * 
     * // update the IP address pool store
     * for (String ip : ips) {
     * int lastDigits = Integer.valueOf(ip.split("\\.")[3]);
     * ipAddressPoolStore.ipTaken(lastDigits);
     * }
     * }
     * }
     * }
     * }
     * }
     */

    // getHosts
    private ArrayList<String> getIpAddresses() {
        Iterable<Host> hosts = hostService.getHosts();
        ArrayList<Host> hostList = new ArrayList<Host>();
        ArrayList<String> ipList = new ArrayList<String>();
        hosts.forEach(hostList::add);
        for (int i = 0; i < hostList.size(); i++) {
            for (IpAddress ipa : hostList.get(i).ipAddresses()) {
                ipList.add(ipa.toString());
            }
        }
        return ipList;
    }
}
