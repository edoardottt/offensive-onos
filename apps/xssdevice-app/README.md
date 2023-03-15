# XSS Device Application

This application exploits [CVE-2017-1000078](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2017-1000078) in ONOS 1.9.0 ([docker image](https://hub.docker.com/layers/onosproject/onos/1.9.0/images/sha256-15736a6740918e9dd7df2dd1287ee52e22be5ad45ba8b6c4400afeca9a66ff51)).  
This is an importat Proof of Concept because it's the first Cross App Poisoning attack targeting Web resources; before it was only about network related attacks.  
This application overwrites some information of an existing device with HTML/JS payload resulting in a stored XSS attack.

How to use
-----

Setup data plane with Mininet (use your ONOS IP address):
```console
sudo mn --mac --topo linear,4 --controller remote,ip=192.168.1.8 --switch ovs,protocols=OpenFlow13
```

Compile and check for errors:
```console
make compile
```

Compile the app into an .oar (ONOS app archive) file which can be installed into ONOS:
```console
make install
```

Search for .oar files
```console
make oar
```

Start ONOS locally
```console
bazel run onos-local
```

Install the application
```console
./tools/package/runtime/bin/onos-app localhost install! ~/github/offensive-onos-apps/apps/xssdevice-app/target/onos-xssdevice-2.0.0-SNAPSHOT.oar
```
