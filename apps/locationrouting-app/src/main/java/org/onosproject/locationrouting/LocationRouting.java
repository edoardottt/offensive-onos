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

package org.onosproject.locationrouting;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.List;
import org.onlab.packet.MacAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.Criterion.Type;
import org.onosproject.net.flow.criteria.EthCriterion;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.Host;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.onlab.util.Tools.groupedThreads;

import com.google.common.collect.Lists;

/**
 * Location Routing.
 */
@Component(immediate = true)
public class LocationRouting {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    protected ExecutorService eventExecutor;

    private final InternalHostListener hostListener = new InternalHostListener();

    private ApplicationId appId;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.edoardottt.locationrouting.app",
                () -> log.info("Periscope down."));
        eventExecutor = newSingleThreadScheduledExecutor(groupedThreads("onos/locationrouting", "events-%d", log));
        hostService.addListener(hostListener);
        log.info("Started locationrouting App!");
    }

    @Deactivate
    protected void deactivate() {
        flowRuleService.removeFlowRulesById(appId);
        hostService.removeListener(hostListener);
        eventExecutor.shutdownNow();
        eventExecutor = null;
        log.info("Stopped locationrouting App!");
    }

    private class InternalHostListener implements HostListener {
        @Override
        public void event(HostEvent event) {
            eventExecutor.execute(() -> {
                log.info("Host {} has moved; cleaning up.", event.subject());
                cleanup(event.subject());
            });
        }
    }

    /**
     * For a given host, remove any flow rule which references it's addresses.
     * 
     * @param host the host to clean up for
     */
    private void cleanup(Host host) {
        Iterable<Device> devices = deviceService.getDevices();
        List<FlowRule> flowRules = Lists.newLinkedList();
        for (Device device : devices) {
            flowRules.addAll(cleanupDevice(device, host));
        }
        FlowRule[] flows = new FlowRule[flowRules.size()];
        flows = flowRules.toArray(flows);
        flowRuleService.removeFlowRules(flows);
    }

    private Collection<? extends FlowRule> cleanupDevice(Device device, Host host) {
        List<FlowRule> flowRules = Lists.newLinkedList();
        MacAddress mac = host.mac();
        for (FlowRule rule : flowRuleService.getFlowEntries(device.id())) {
            for (Criterion c : rule.selector().criteria()) {
                if (c.type() == Type.ETH_DST || c.type() == Type.ETH_SRC) {
                    EthCriterion eth = (EthCriterion) c;
                    if (eth.mac().equals(mac)) {
                        flowRules.add(rule);
                    }
                }
            }
        }
        return flowRules;
    }
}
