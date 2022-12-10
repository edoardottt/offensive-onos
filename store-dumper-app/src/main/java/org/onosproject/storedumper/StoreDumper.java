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
package org.onosproject.storedumper;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.onosproject.core.CoreService;
import org.onosproject.net.host.HostStore;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.packet.PacketStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store Dumper.
 */
@Component(immediate = true)
public class StoreDumper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // --------------------------------------------------------
    // CHANGE THIS PARAMETER TO TRIGGER THE APP EVERY X MILLISECONDS.
    // --------------------------------------------------------
    private static final long TIMEOUT = 60000;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostStore hostStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceStore deviceStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketStore packetStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected StatisticStore statisticStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ResourceStore resourceStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected RegionStore regionStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected MeterStore meterStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkStore linkStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceKeyStore deviceKeyStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected IntentStore intentStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected GroupStore groupStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowObjectiveStore flowObjectiveStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleStore flowRuleStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected NetworkConfigStore networkConfigStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ClusterStore clusterStore;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ApplicationIdStore applicationIdStore;

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            log.info("Time up, running Task!");
            dump();
        }
    };
    
    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.storedumper.app", () -> log.info("Periscope down."));
        log.info("Started storedumper App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped storedumper App!");
    }

    // startTimer starts a timer that timeouts every X seconds.
    private void startTimer(long timeout) {
        timer.scheduleAtFixedRate(timerTask, 0, timeout);
    }

    private void dump() {
        getHosts();
    }

    // getHosts
    private void getHosts() {
        Iterable<Host> hosts = hostService.getHosts();
        Random rand = new Random();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        for (int i=0; i<hostList.size(); i++) {
            log.info(hostList.get(i).toString());
        }
    }
}
