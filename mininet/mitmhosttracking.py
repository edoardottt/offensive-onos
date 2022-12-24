#!/usr/bin/python                                                                            
                                                                                             
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel
from mininet.cli import CLI
import time
import sys

class SingleSwitchTopo(Topo):
    "Single switch connected to n hosts."
    def build(self, n=4):
        switch1 = self.addSwitch('s1')
        h1 = self.addHost('h1')
        self.addLink(switch1, h1)
        switch2 = self.addSwitch('s2')
        # Python's range(N) generates 0..N-1
       	for h in range(1, n):
            host = self.addHost('h%s' % (h + 1))
            self.addLink(host, switch2)

def read_input():
    if len(sys.argv) != 3:
        print("usage: python {} n remote-ip; where n is the number of hosts and remote-ip the IP of ONOS".format(sys.argv[0]))
        sys.exit(1)
    return int(sys.argv[1])

def start(input):
    topo = SingleSwitchTopo(n=input)
    net = Mininet(topo)
    c0 = RemoteController('c0', ip=sys.argv[2], port=6633 )
    net.addController(c0)
    net.start()
    return net

def simpleTest(net):
    print( "Dumping host connections" )
    dumpNodeConnections(net.hosts)
    print( "Testing network connectivity" )
    net.pingAll()

def stop(net):
    net.stop()

if __name__ == '__main__':
    # Tell mininet to print useful information
    setLogLevel('info')
    n = read_input()
    net = start(n)
    print("Sleeping for five seconds...")
    time.sleep(5)
    simpleTest(net)
    net.run(CLI, net)
