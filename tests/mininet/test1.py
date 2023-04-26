#!/usr/bin/python                                                                            
                                                                                             
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import RemoteController
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
        h2 = self.addHost('h2')
        self.addLink(switch2, h2)
        switch3 = self.addSwitch('s3')
        h3 = self.addHost('h3')
        self.addLink(switch3, h3)
        switch4 = self.addSwitch('s4')
        h4 = self.addHost('h4')
        self.addLink(switch4, h4)

def read_input():
    if len(sys.argv) != 3:
        print("usage: python {} n remote-ip; where n is the number of hosts and remote-ip the IP of ONOS".format(sys.argv[0]))
        sys.exit(1)
    return int(sys.argv[1])

def start(input):
    topo = SingleSwitchTopo(n=input)
    net = Mininet(topo)
    c0 = RemoteController('c0', ip=sys.argv[2], port=6653 )
    net.addController(c0)
    net.start()
    return net

def simpleTest(net):
    print( "Dumping host connections" )
    dumpNodeConnections(net.hosts)
    print( "Testing network connectivity" )
    net.pingAll()

def connTest(net):
    print( "Testing host connections" )
    while True:
        net.addHost('h5')
        s4 = net.get('s4')
        net.addLink(s4, net.get('h5'))
        s4.attach('s4-eth3')
        net.get('h5').cmd('ifconfig h5-eth0 10.0.0.5')

        # pingall
        net.get('h5').cmd('ping -c 1 10.0.0.1')
        net.get('h5').cmd('ping -c 1 10.0.0.2')
        net.get('h5').cmd('ping -c 1 10.0.0.3')
        net.get('h5').cmd('ping -c 1 10.0.0.4')
        
        # delete host
        net.delHost(net.get('h5'))
    

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
    connTest(net)
    net.run(CLI, net)
