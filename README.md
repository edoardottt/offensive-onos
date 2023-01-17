# Offensive ONOS Apps

My experiments in weaponizing [ONOS](https://github.com/opennetworkinglab/onos) applications.  
Useful Papers to get context: [Cross-App Poisoning in Software-Defined Networking](https://dl.acm.org/doi/10.1145/3243734.3243759), [Classifying Poisoning Attacks in Software Defined Networking](https://ieeexplore.ieee.org/abstract/document/8920310).

Requirements
-----

- Maven (https://maven.apache.org/)

Get Started
-----

Compile and check for errors:
```console
make compile
```

Compile each app into an .oar (ONOS app archive) file which can be installed into ONOS:
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

SSH into ONOS
```console
ssh -p 8101 onos@172.17.0.1
```

Install the application(s)
```console
./tools/package/runtime/bin/onos-app localhost install! ~/github/offensive-onos-apps/apps/APPNAME/target/APPNAME-*.oar
```

Links
-----
- [Thomas Vachuska - Creating and deploying ONOS app](https://www.youtube.com/watch?v=mzQubYhJhro&ab_channel=ThomasVachuska)

Changelog
-----
Detailed changes for each release are documented in the [release notes](https://github.com/edoardottt/offensive-onos-apps/releases).
