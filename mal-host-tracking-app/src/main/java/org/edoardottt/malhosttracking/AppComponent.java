/*
 * Malicious Host Tracking Application
 * 
 * https://github.com/edoardottt/offensive-onos-apps/
 * 
 * edoardottt, https://www.edoardoottavianelli.it/
 */
package org.edoardottt.malhosttracking;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Malicious Host Tracking App Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Malicious Host Tracking App Stopped");
    }

}
