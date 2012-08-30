# TCP server example
frame_num = 0

def convertIntToChrs(i):
    s = chr(     (i)       & 255 )
    s = s + chr( (i >> 8)  & 255 )
    s = s + chr( (i >> 16) & 255 )
    s = s + chr( (i >> 24) & 255)
    return s

def convertChrsToInt(s):
    return (ord(s[3]) << 24) + (ord(s[2]) << 16) + (ord(s[1]) << 8) + ord(s[0])

def printValues(s):
    out = s[8:56]
    print "CRIO got FRAME   = ", convertChrsToInt(out[0:4])
    print "CRIO got otop    = ", convertChrsToInt(out[4:8])
    print "CRIO got oleft   = ", convertChrsToInt(out[8:12])
    print "CRIO got oright  = ", convertChrsToInt(out[12:16])
    print "CRIO got obottom = ", convertChrsToInt(out[16:20])
    print ""
    print "CRIO got stop    = ", convertChrsToInt(out[20:24])
    print "CRIO got sleft   = ", convertChrsToInt(out[24:28])
    print "CRIO got sbottom = ", convertChrsToInt(out[28:32])
    print "CRIO got sright  = ", convertChrsToInt(out[32:46])
    print ""
    print "CRIO got dist    = ", convertChrsToInt(out[36:40])
    print "CRIO got angle   = ", convertChrsToInt(out[40:44])
    print "CRIO got target# = ", convertChrsToInt(out[44:48])
    print ""

def getPacket():
    s = "Header00"
    s += convertIntToChrs(frame_num) #frame number
    
    s += convertIntToChrs(0) #offset_from_top
    s += convertIntToChrs(0) #offset_from_left
    s += convertIntToChrs(0) #offset_from_right
    s += convertIntToChrs(0) #offset_from_bottom
    
    s += convertIntToChrs( int(raw_input("RPM top    >>> ")) ) #speed_top
    s += convertIntToChrs( int(raw_input("RPM left   >>> ")) ) #speed_left
    s += convertIntToChrs( int(raw_input("RPM bottom >>> ")) ) #speed_bottom
    s += convertIntToChrs( int(raw_input("RPM right  >>> ")) ) #speed_right
    
    s += convertIntToChrs(0) #distance
    s += convertIntToChrs(0) #angle
    s += convertIntToChrs(0) #target_number
    return s

def getPIDPacket():
    s = "Header00"
    s += convertIntToChrs(frame_num) #frame number
    
    s += convertIntToChrs( int(raw_input("1/scale (1000000) >>> ")) ) #offset_from_top
    s += convertIntToChrs( int(raw_input("KP      (100)     >>> ")) ) #offset_from_left
    s += convertIntToChrs( int(raw_input("KI      (1)       >>> ")) ) #offset_from_right
    s += convertIntToChrs( int(raw_input("KD      (300)     >>> ")) ) #offset_from_bottom
    
    s += convertIntToChrs( 0 ) #speed_top
    s += convertIntToChrs( 0 ) #speed_left
    s += convertIntToChrs( 0 ) #speed_bottom
    s += convertIntToChrs( 0 ) #speed_right
    
    s += convertIntToChrs(0) #distance
    s += convertIntToChrs(0) #angle
    s += convertIntToChrs(0) #target_number
    return s


import socket
PORT = int(raw_input("What port should I listen to? >>> "))
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(("", PORT))
server_socket.listen(5)

print "TCPServer Waiting for client on port ", PORT

client_socket, address = server_socket.accept()
print "I got a connection from ", address
while 1:
    frame_num += 1
    data = raw_input("S to send packet, P to change PID values, Q to quit >>> ")
    if data == 'q' or data == 'Q':
        client_socket.send(data)
        client_socket.close()
        server_socket.close()
        break
    
    if data == 's' or data == 'S':
        data = getPacket()
        client_socket.send( data )
    elif data == 'p' or data == 'P':
        data = getPIDPacket()
        client_socket.send( data )
    
    data = client_socket.recv(4096)
    print "RECIEVED: "
    if( len(data) < 5 ):
        print data
    else:
        print data[0:8]
        printValues(data)
    
client_socket.shutdown()
client_socket.close()
server_socket.shutdown()
server_socket.close()
q = raw_input("done!")

