# Offensive ONOS

My experiments in weaponizing [ONOS](https://github.com/opennetworkinglab/onos) applications.  
This is a part of research activity for my Cybersecurity M.Sc. Thesis ([link](https://github.com/edoardottt/master-degree-thesis/)), focused on detection of Cross App Poisoning Attacks in Software Defined Networks.  

**This research also led to discovery of [CVE-2023-24279](https://nvd.nist.gov/vuln/detail/CVE-2023-24279) and [CVE-2023-30093](https://nvd.nist.gov/vuln/detail/CVE-2023-30093)**.

Useful papers to get context:

<!-- - "Cross App Poisoning Attacks Detection in Software Defined Networks" by Edoardo Ottavianelli and Marco Polverini -->
- [Cross-App Poisoning in Software-Defined Networking](https://dl.acm.org/doi/10.1145/3243734.3243759)
- [Protecting Virtual Programmable Switches from Cross-App Poisoning (CAP) Attacks](https://ieeexplore.ieee.org/document/9789775)
- [Classifying Poisoning Attacks in Software Defined Networking](https://ieeexplore.ieee.org/abstract/document/8920310)
- [A Survey on Software Defined Networking: Architecture for Next Generation Network](https://arxiv.org/abs/2001.10165)
- [My Master's Degree Thesis](https://www.researchgate.net/publication/371491370_Proposal_and_Investigation_of_a_framework_for_Cross_App_Poisoning_attacks_detection_in_Software_Defined_Networks)

## Requirements

- JVM 11+ (<https://www.oracle.com/java/technologies/downloads/>)
- Maven (<https://maven.apache.org/>)
- ONOS 2.7.0 (<https://wiki.onosproject.org/display/ONOS/Downloads>)

In order to test the applications I've used Mininet to virtualize the data-plane, but it's optional (<https://github.com/mininet/mininet/releases/>).

## Get Started

Compile an ONOS application ready to be installed and activated

```console
make -C apps/APP-NAME compile
```

Search for .oar (ONOS archive) files

```console
make oar
```

## Links

- [ONOS Wiki](https://wiki.onosproject.org/display/ONOS/ONOS)
- [ONOS 2.7.0 API Documentation](https://api.onosproject.org/2.7.0/apidocs/)
- [Thomas Vachuska - Creating and deploying ONOS app](https://www.youtube.com/watch?v=mzQubYhJhro&ab_channel=ThomasVachuska)
- [Introduction to Mininet](https://github.com/mininet/mininet/wiki/Introduction-to-Mininet)

## Changelog

Detailed changes for each release are documented in the [release notes](https://github.com/edoardottt/offensive-onos-apps/releases).

## Contributing

Just open an [issue](https://github.com/edoardottt/offensive-onos-apps/issues) / [pull request](https://github.com/edoardottt/offensive-onos-apps/pulls).

-------

[edoardoottavianelli.it](https://www.edoardoottavianelli.it/) to contact me.
