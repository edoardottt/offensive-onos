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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.onosproject.core.CoreService;
import org.onosproject.net.Host;
import org.onosproject.net.host.HostService;
import org.onosproject.net.host.HostStore;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.Device;
import org.onosproject.net.packet.PacketStore;
import org.onosproject.net.statistic.StatisticStore;
import org.onosproject.net.resource.ResourceStore;
import org.onosproject.net.region.RegionStore;
import org.onosproject.net.meter.MeterStore;
import org.onosproject.net.link.LinkStore;
import org.onosproject.net.key.DeviceKeyStore;
import org.onosproject.net.intent.IntentStore;
import org.onosproject.net.group.GroupStore;
import org.onosproject.net.flowobjective.FlowObjectiveStore;
import org.onosproject.net.flow.FlowRuleStore;
import org.onosproject.net.config.NetworkConfigStore;
import org.onosproject.cluster.ClusterStore;
import org.onosproject.app.ApplicationIdStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.nio.file.Files;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    // --------------------------------------------------------
    // CHANGE THIS PARAMETER TO WRITE IN A DIFFERENT FILE.
    // --------------------------------------------------------
    private static final String FILENAME = "/home/edoardottt/cybersecurity/todo/Thesis/onos-dump.txt";

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
        startTimer(TIMEOUT);
        log.info("Started storedumper App!");
    }

    @Deactivate
    protected void deactivate() {
        timer.cancel();
        timer.purge();
        log.info("Stopped storedumper App!");
    }

    // startTimer starts a timer that timeouts every X seconds.
    private void startTimer(long timeout) {
        timer.scheduleAtFixedRate(timerTask, 0, timeout);
    }

    private void dump() {
        createFile();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        writeToFile("=========================== " + timeStamp + " ===========================");
        dumpHosts();
        dumpDevices();
    }

    // dumpHosts
    private void dumpHosts() {
        writeToFile("------------ HOSTS DUMP ------------");
        Iterable<Host> hosts = hostService.getHosts();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        for (int i=0; i<hostList.size(); i++) {
            writeToFile(hostList.get(i).toString());
        }
    }

    // dumpDevices
    private void dumpDevices() {
        writeToFile("------------ DEVICES DUMP ------------");
        Iterable<Device> devices = deviceService.getDevices();
        List<Device> deviceList = new ArrayList<Device>();
        devices.forEach(deviceList::add);
        for (int i=0; i<deviceList.size(); i++) {
            writeToFile(deviceList.get(i).toString());
        }
    }

    private File createFile() {
        try {
            File myObj = new File(FILENAME);
            return myObj;
        } catch (Exception e) {
            log.info("Error while creating file {}", FILENAME);
            e.printStackTrace();
        }

        return null;
    }

    private void writeToFile(String content) {
        try {
            FileWriter fw = new FileWriter(FILENAME, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(content + "\n");
            out.close();
            log.info("Successfully wrote to the file!");
        } catch (IOException e) {
            log.info("Error while writing to file {}", FILENAME);
            e.printStackTrace();
        }
    }
}
