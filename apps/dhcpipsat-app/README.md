# DHCP IP Saturation Application

DHCP Server vulnerable to IP Saturation (via CAP attack).

How to use
-----

Set up data plane using Mininet (use your ONOS IP address):

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
bazelisk run onos-local --host_force_python=PY3
```

Install the application

```console
./tools/package/runtime/bin/onos-app localhost install! ~/github/offensive-onos-apps/apps/dhcpipsat-app/target/onos-dhcpipsat-2.0.0-SNAPSHOT.oar
```
