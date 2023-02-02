### Useful Mininet commands 

- `sh ovs-ofctl dump-flows sX --protocols=Openflow13` (get a flow dump of a switch, substitute `sX`)
- `sudo mn --mac --topo linear,4 --controller remote,ip=192.168.1.8 --switch ovs,protocols=Openflow13`
