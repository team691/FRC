
CC=${CROSS_COMPILE}gcc-4.4
CPP=${CROSS_COMPILE}g++-4.4
LD=${CROSS_COMPILE}g++-4.4
OBJS=nano_time.o super_sock.o 
LIBS=-lrt -lcv -lhighgui -lcvaux -lml -lcxcore $(OBJS) 
INCLUDES=-I$/usr/include -I$/usr/include/c++/4.4 -I$/usr/src/linux/include -I/usr/include/opencv -I.
CFLAGS=-g -O2 -L/usr/lib/i386-linux-gnu


HEADERS=nano_time.h super_sock.h frc2012.h KPacket.h
BASKET_FINDER=basket_finder_bin
SEND_SOCK=send_sock_bin
READ_SOCK=read_sock_bin

TARGETS= $(BASKET_FINDER) $(SEND_SOCK) $(READ_SOCK)

all: $(TARGETS)

%.o : %.cc $(HEADERS)
	$(CPP) -c $(CFLAGS) $(CPPFLAGS) $(INCLUDES) $< -o $@ 

%_bin : %_bin.o $(OBJS)
	$(CPP) $(CFLAGS) $(CPPFLAGS) $(LIBS) $< -o $@

.PHONY: clean

clean:
	rm -f *.o $(TARGETS)

install: $(TARGETS)
	cp $(TARGETS) ${HOME}/bin
