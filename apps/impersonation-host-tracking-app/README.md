### Impersonation Host Tracking

Impersonate legitimate hosts poisoning the host store with fake locations.

Test with [mitmhosttracking-topo.py](https://github.com/edoardottt/offensive-onos-apps/blob/main/mininet/mitmhosttracking-topo.py).

How to use
-----

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
