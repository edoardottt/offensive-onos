# Impersonation attack test

<p align="center">
  <img src="https://github.com/edoardottt/offensive-onos-apps/blob/main/tests/impersonation/Impersonation_CAP_attack.png">
</p>

How the attack works
----

First of all, the network is composed as the figure shows: every host can reach other hosts and the test `pingall` in Mininet passes with 100% success rate.  

```
mininet> pingall
*** Ping: testing ping reachbility
h1 -> h2 h3 h4
h2 -> h1 h3 h4
h3 -> h2 h3 h4
h4 -> h1 h2 h3
*** Results: 0% dropped (12/12 received)
```

Then, the malicious application [impersonation-host-tracking-app](https://github.com/edoardottt/offensive-onos-apps/tree/main/apps/impersonation-host-tracking-app) is installed and activated in ONOS. When activated, it starts immediately poisoning the Host Data Store:

- It finds the location of host H2
- Put the location of H2 in the array of locations of H3 and H4
- Delete old locations of H3 and H4

At the end of the attack, hosts H2, H3 and H4 will have the exact same location (the one highlighted in red, the starting one of H2).  
Now we just need to execute:

- `h2 tcpdump > impersonation-h2.pcap &`
- `h4 tcpdump > impersonation-h4.pcap &`
- `h1 ping h4`

We can observe that in the file impersonation-h2.pcap H2 will receive ICMP echo requests from H1, meaning that attack succeeded. H2 then can impersonate H4 sending ICMP echo replies back to H1.  
Instead, if we observe impersonation-h4.pcap, we can notice that H4 never receives ICMP echo requests from H1.

> **Note**
> This attack is successful because there are no flow rules installed in the switches (due to cache timeout) ruling the flows between H1 and H4. So, when a switch gets a flow rule table miss it will ask directly to the ONOS controller where the packer should go and the controller will reply installing flow rules based on H4 location (but now it is the fake one!).
