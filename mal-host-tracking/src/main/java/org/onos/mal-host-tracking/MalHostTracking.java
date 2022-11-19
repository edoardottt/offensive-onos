/**
* https://github.com/edoardottt/offensive-onos-apps.
*/
package org.onos.malhosttracking;


import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample application that modifies the Host Data Store
 * (Host-Switch association) just for fun.
 */
@Component(immediate = true)
public class MalHostTracking {

    private static Logger log = LoggerFactory.getLogger(MalHostTracking.class);

    private static final String STORE_ADD = "[ > ] Host Data Store Poisoned. Added pair {} - {}";
    private static final String STORE_REMOVED = "[ > ] Host Data Store Poisoned. Removed pair {} - {}";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    private static final int DELAY = 30; // seconds

    private ApplicationId appId;

    @Activate
    public void activate() {
        appId = coreService.registerApplication("org.onosproject.malhosttracking");
        log.info("[ > ] MalHostTracking Started!");
    }

    @Deactivate
    public void deactivate() {
        log.info("[ > ] MalHostTracking Stopped!");
    }
}