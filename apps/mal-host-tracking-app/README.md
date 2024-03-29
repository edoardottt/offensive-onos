# Malicious Host Tracking Application

Disconnect hosts poisoning the host store with fake locations.

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
./tools/package/runtime/bin/onos-app localhost install! ~/github/offensive-onos-apps/apps/mal-host-tracking-app/target/onos-malhosttracking-2.0.0-SNAPSHOT.oar
```
