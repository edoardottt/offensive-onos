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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store Dumper.
 */
@Component(immediate = true)
public class StoreDumper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.storedumper.app", () -> log.info("Periscope down."));
        log.info("Started storedumper App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped storedumper App!");
    }

}
