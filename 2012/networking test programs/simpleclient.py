# TCP client example
import socket


def main():
    print "Welcome to simple client!"
    print "Who would you like connect to?"
    host_str  = raw_input(" >>> ")

    print "What port on the host do you want to use?"
    host_port = int(raw_input(" >>> "))
    
    print "trying to connect..."
    
    
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((host_str, host_port))
    while 1:
        data = client_socket.recv(2048)
        if ( data == 'q' or data == 'Q'):
            break;
        else:
            print "RECIEVED:    " , data
            i = raw_input ( "SEND( q to quit, e to echo):    " )
            if(i <> 'e' and i <> 'E'):
                data = i
            
            if (data <> 'Q' and data <> 'q'):
                client_socket.send(unicode(data))
            else:
                client_socket.send(unicode(data))
                break;
        
    #run when the loop is ended    
    client_socket.close()

try:
    main()
except Exception as e:
    print "ERROR!"
    print e

exit = raw_input("done!")