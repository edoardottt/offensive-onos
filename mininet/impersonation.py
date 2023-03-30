#
# sudo mn --mac --custom impersonation.py --topo impersonation --controller remote,ip=192.168.1.8 --switch ovs,protocols=OpenFlow13
#

from mininet.topo import Topo

class ImpersonationTopo( Topo ):

    def build( self ):
        switch1 = self.addSwitch('s1')
        h1 = self.addHost('h1')
        self.addLink(switch1, h1)
        switch2 = self.addSwitch('s2')
        self.addLink(switch1, switch2)
        for h in range(1, 4):
            host = self.addHost('h%s' % (h + 1))
            self.addLink(host, switch2)


topos = { 'impersonation': ( lambda: ImpersonationTopo() ) }
