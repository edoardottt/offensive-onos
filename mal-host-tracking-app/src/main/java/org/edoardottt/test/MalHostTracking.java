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
package org.edoardottt.malhosttracking;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onosproject.core.CoreService;
import org.onosproject.net.host.HostService;
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
import java.util.TimerTask;

/**
 * Malicious Host Tracking Application.
 */
@Component(immediate = true)
public class MalHostTracking {

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
        coreService.registerApplication("org.edoardottt.malhosttracking.app", () -> log.info("Periscope down."));
        startTimer(TIMEOUT);
        log.info("Started malhosttracking App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped malhosttracking App!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {
        HostId hId = pickRandomHost().id();
        DeviceId dId = pickRandomDevice().id();
        List<Port> ports = deviceStore.getPorts(dId);
        PortNumber pNumber = pickRandomPort(ports).number();
        HostLocation hl = new HostLocation(dId, pNumber, 0);
        hostStore.appendLocation(hId, hl);
        log.info("Malicious Host Tracking App: Host {} connected to device {}", hId.toString(), dId.toString());
    }

    // startTimer starts a timer that timeouts every X seconds.
    private void startTimer(long timeout) {
        timer.scheduleAtFixedRate(timerTask, 0, timeout);
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

        return randomPort;
    }
}
