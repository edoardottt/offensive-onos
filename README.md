# Offensive ONOS Apps

My experiments in weaponizing [ONOS](https://github.com/opennetworkinglab/onos) applications.  
This is a part of research activity for my Cybersecurity M.Sc. Thesis, focused on detection of Cross App Poisoning Attacks in Software Defined Networks.

Useful Papers to get context: 
  - [Cross-App Poisoning in Software-Defined Networking](https://dl.acm.org/doi/10.1145/3243734.3243759)
  - [Classifying Poisoning Attacks in Software Defined Networking](https://ieeexplore.ieee.org/abstract/document/8920310)
  - [A Survey on Software Defined Networking: Architecture for Next Generation Network](https://arxiv.org/abs/2001.10165)

Requirements
-----

- JVM 11+ (https://www.oracle.com/java/technologies/downloads/)
- Maven (https://maven.apache.org/)
- ONOS 2.7.0 (https://wiki.onosproject.org/display/ONOS/Downloads)

In order to test the applications I've used Mininet, but it's optional (https://github.com/mininet/mininet/releases/).

Get Started
-----

Compile an ONOS application ready to be installed and activated
```console
make -C apps/APP-NAME compile
```

Search for .oar (ONOS archive) files
```console
make oar
```

Links
-----
- [Thomas Vachuska - Creating and deploying ONOS app](https://www.youtube.com/watch?v=mzQubYhJhro&ab_channel=ThomasVachuska)
- [Get Started With Mininet](http://mininet.org/download/)

Changelog
-----
Detailed changes for each release are documented in the [release notes](https://github.com/edoardottt/offensive-onos-apps/releases).

Contributing
-------

Just open an [issue](https://github.com/edoardottt/offensive-onos-apps/issues) / [pull request](https://github.com/edoardottt/offensive-onos-apps/pulls).
