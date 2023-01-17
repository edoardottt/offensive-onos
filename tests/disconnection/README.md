# Disconnection attack test

<p align="center">
  <img src="https://github.com/edoardottt/offensive-onos-apps/blob/main/tests/disconnection/Disconnection_CAP_attack.png">
</p>

How the attack works
----

```
mininet> pingall
*** Ping: testing ping reachbility
h1 -> h2 h3 h4
h2 -> h1 h3 h4
h3 -> h2 h3 h4
h4 -> h1 h2 h3
*** Results: 0% dropped (12/12 received)
```

```
mininet> pingall
*** Ping: testing ping reachbility
h1 -> h2 X h4
h2 -> h1 X h4
h3 -> h2 X h4
h4 -> h1 h2 h3
*** Results: 25% dropped (9/12 received)
```
