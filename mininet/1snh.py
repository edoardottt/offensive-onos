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
    def build(self, n=2):
        switch = self.addSwitch('s1')
        # Python's range(N) generates 0..N-1
        for h in range(n):
            host = self.addHost('h%s' % (h + 1))
            self.addLink(host, switch)

def read_input():
    if len(sys.argv) != 2:
        print("usage: python {} n; where n is the number of hosts.".format(sys.argv[0]))
        sys.exit(1)
    return int(sys.argv[1])

def start(input):
    topo = SingleSwitchTopo(n=input)
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
    n = read_input()
    net = start(n)
    print("Sleeping for five seconds...")
    time.sleep(5)
    simpleTest(net)
    net.run(CLI, net)
