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
    def build(self, ns=1, nh=2):
        hcounter = 0
        switches = []
        for s in range(ns):
            switch = self.addSwitch('s%s' % (s + 1))
            switches.append(switch)
            hcounter+=1
            for h in range(nh):
                host = self.addHost('h%s' % (hcounter + h))
                self.addLink(host, switch)
            hcounter+=1
        for i in range(len(switches)-1):
            self.addLink(switches[i],switches[i+1])

def read_input():
    if len(sys.argv) != 3:
        print("usage: python {} ns nh;\n\twhere ns is the number of switches\n\tand nh the number of hosts.".format(sys.argv[0]))
        sys.exit(1)
    return (int(sys.argv[1]),int(sys.argv[2]))

def start(ns,nh):
    topo = SingleSwitchTopo(ns=ns,nh=nh)
    net = Mininet(topo)
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
    ns,nh = read_input()
    net = start(ns,nh)
    print("Sleeping for five seconds...")
    time.sleep(5)
    simpleTest(net)
    net.run(CLI, net)
