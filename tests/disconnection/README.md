# Disconnection attack test

<p align="center">
  <img src="https://github.com/edoardottt/offensive-onos-apps/blob/main/tests/disconnection/Disconnection_CAP_attack.png">
</p>

How the attack works
----
First of all, the network is composed as the figure shows: every host can reach other hosts and the test pingall in Mininet passes with 100% success rate.
```
mininet> pingall
*** Ping: testing ping reachbility
h1 -> h2 h3 h4
h2 -> h1 h3 h4
h3 -> h2 h3 h4
h4 -> h1 h2 h3
*** Results: 0% dropped (12/12 received)
```
Then, the malicious application [mal-host-tracking-app](https://github.com/edoardottt/offensive-onos-apps/tree/main/apps/mal-host-tracking-app) is installed and activated in ONOS. When activated, it starts immediately poisoning the Host Data Store:

- It finds the location of host H1 and H3
- Empty the locations of H1 and H3
- Insert the location of H1 in the array of locations of H3 and viceversa

At the end of the attack, hosts H1 and H3 will have locations inverted (H1 connected to S2 and H3 connected to S1).

Now if we perform a connection test we can observe this result:
```
mininet> pingall
*** Ping: testing ping reachbility
h1 -> h2 X h4
h2 -> h1 X h4
h3 -> h2 X h4
h4 -> h1 h2 h3
*** Results: 25% dropped (9/12 received)
```
