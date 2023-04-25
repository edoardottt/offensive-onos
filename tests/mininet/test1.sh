#!/bin/bash

while true
do
    # add host
    py net.addHost('h5')
    py net.addLink(s4, net.get('h5'))
    py s4.attach('s4-eth3')
    py net.get('h5').cmd('ifconfig h5-eth0 10.0.0.5')

    # pingall
    py net.get('h5').cmd('ping -c 1 10.0.0.1')
    py net.get('h5').cmd('ping -c 1 10.0.0.2')
    py net.get('h5').cmd('ping -c 1 10.0.0.3')
    py net.get('h5').cmd('ping -c 1 10.0.0.4')
    
    # delete host
    py net.delHost(net.get('h5'))

    sleep 1
done
