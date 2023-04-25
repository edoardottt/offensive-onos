# Add an host in mininet while the network is up and running.

py net.addHost('h5')
py net.addLink(s4, net.get('h5'))
py s4.attach('s4-eth3')
py net.get('h5').cmd('ifconfig h5-eth0 10.0.0.5')

# delete host
# py net.delHost(net.get('h5'))
