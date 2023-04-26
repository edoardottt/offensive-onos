from mininet import net

def connTest():
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
    
