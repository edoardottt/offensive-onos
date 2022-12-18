# Send this IP packet with `python add-fake-host.py`.
# If Host Location Provider is activated a new host with IP 10.0.0.5 will be added
# to the host store.

from scapy.all import *

frame = Ether(src="00:00:00:00:00:05", dst="00:00:00:00:00:01")/IP(src="10.0.0.5", dst="10.0.0.4")

sendp(frame, iface="h1-eth0") 
