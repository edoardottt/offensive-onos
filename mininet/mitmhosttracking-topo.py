#
# sudo mn --mac --custom mitmhosttracking-topo.py --topo mytopo --controller remote,ip=192.168.1.8 --switch ovs,protocols=OpenFlow13
#

from mininet.topo import Topo

class MyTopo( Topo ):
    "Simple topology example."

    def build( self ):
        "Single switch connected to n hosts."
        switch1 = self.addSwitch('s1')
        h1 = self.addHost('h1')
        self.addLink(switch1, h1)
        switch2 = self.addSwitch('s2')
        self.addLink(switch1, switch2)
        # Python's range(N) generates 0..N-1
        for h in range(1, 4):
            host = self.addHost('h%s' % (h + 1))
            self.addLink(host, switch2)


topos = { 'mytopo': ( lambda: MyTopo() ) }
