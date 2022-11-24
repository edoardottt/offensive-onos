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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onosproject.core.CoreService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.host.HostStore;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.DeviceStore;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.core.ApplicationId;
import java.util.Timer;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Malicious Host Tracking
 */
@Component(immediate = true)
public class MalHostTracking {

    private final Logger log = LoggerFactory.getLogger(getClass());

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

    private ApplicationId appId;

    Timer timer = new Timer();

    // --------------------------------------------------------
    // CHANGE THIS PARAMETER TO TRIGGER THE APP EVERY X SECONDS.
    // --------------------------------------------------------
    private static final long TIMEOUT = 10;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.edoardottt.malhosttracking");
        startTimer(TIMEOUT);
        log.info("Malicious Host Tracking App Started! App ID: {}", appId.toString());
    }

    @Deactivate
    protected void deactivate() {
        log.info("Malicious Host Tracking App Stopped!");
    }

    // editHostStore mess up with the Host Data Store.
    private void editHostStore() {
        Host h = pickRandomHost();
        Device d = pickRandomDevice();

        HostId hId = h.id();
        DeviceId dId = d.id();
        List<Port> ports = deviceStore.getPorts(dId);
        PortNumber pNumber = pickRandomPort(ports).number();
        HostLocation hl = new HostLocation(dId, pNumber, 0);
        hostStore.appendLocation(hId, hl);
        log.info("Malicious Host Tracking App: Host {} connected to device {}", hId.toString(), dId.toString());
    }

    // startTimer starts a timer that timeouts every X seconds.
    private void startTimer(long timeout) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("Time up, running Task!");
                editHostStore();
            }
        };
        timer.schedule(timerTask, 0, timeout);
    }

    // pickRandomHost picks a random host
    private Host pickRandomHost() {
        Iterable<Host> hosts = hostService.getHosts();
        Random rand = new Random();
        List<Host> hostList = new ArrayList<Host>();
        hosts.forEach(hostList::add);
        Host randomHost = hostList.get(rand.nextInt(hostList.size()));

        return randomHost;
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

    // pickRandomPort picks a random port of a device
    private Port pickRandomPort(List<Port> ports) {
        Random rand = new Random();
        Port randomPort = ports.get(rand.nextInt(ports.size()));

        return randomPort;
    }
}
