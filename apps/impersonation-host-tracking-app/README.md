# Impersonation Host Tracking Application

Impersonate legitimate hosts poisoning the host store with fake locations.

How to use
-----

Set up data plane using Mininet (use [mitmhosttracking-topo.py](https://github.com/edoardottt/offensive-onos-apps/blob/main/mininet/mitmhosttracking-topo.py) and your ONOS IP address):

```console
sudo mn --mac --custom impersonation.py --topo impersonation --controller remote,ip=192.168.1.8 --switch ovs,protocols=OpenFlow13
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
./tools/package/runtime/bin/onos-app localhost install! ~/github/offensive-onos-apps/apps/impersonation-host-tracking-app/target/onos-impersonationhosttracking-2.0.0-SNAPSHOT.oar
```
