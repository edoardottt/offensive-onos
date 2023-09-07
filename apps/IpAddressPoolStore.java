/*
 * Copyright 2015-present Open Networking Foundation
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

package org.onosproject.security;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AllPermission;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;
import java.io.File;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manages the inventory of application keys.
 */
@Component(immediate = true, service = IpAddressPoolStore.class)
public class IpAddressPoolStore {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String LOGFILENAME = "/home/edoardottt/cybersecurity/thesis/onos-ipsat.log";

    private ConcurrentHashMap<Integer, Boolean> ipStore;

    @Activate
    public void activate() {
        ipStore = new ConcurrentHashMap<Integer, Boolean>();

        for (int i = 5; i < 100; i++) {
            ipStore.put(i, false);
        }

        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        ipStore.clear();

        log.info("Stopped");
    }

    public void ipTaken(int ip) {
        ipStore.put(ip, true);

        log.info("Ip Address {} Taken!", "10.0.0." + String.valueOf(ip));

        ipStoreSaturated();
    }

    private boolean ipStoreSaturated() {
        for (int i : ipStore.keySet()) {
            if (!ipStore.get(i)) {
                return false;
            }
        }

        log.info("IP STORE SATURATED!");

        return true;
    }

    private void log(String appName, String appKey, String content) {
        long timeMilli = new Date().getTime();

        try {
            createFile();
            FileWriter fw = new FileWriter(LOGFILENAME, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(timeMilli + " " + appName + " " + content + "\n");
            out.close();
            log.info("ONOS-LOG | Successfully wrote to file!");
        } catch (IOException e) {
            log.info("ONOS-LOG | Error while writing to file {}", LOGFILENAME);
            e.printStackTrace();
        }
    }

    private File createFile() {
        try {
            File myObj = new File(LOGFILENAME);
            if (!myObj.exists()) {
                myObj.createNewFile();
            }
            return myObj;
        } catch (Exception e) {
            log.info("ONOS-LOG | Error while creating file {}", LOGFILENAME);
            e.printStackTrace();
        }

        return null;
    }
}