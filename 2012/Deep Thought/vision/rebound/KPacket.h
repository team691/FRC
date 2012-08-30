/** 
 * @file  KPacket.h
 *
 * $Id: $
 *
 * @author  Gerard 
 * @date    February 18, 2012
 *
 * Packet that we want to send to the C-RIO
 *
 */ 
#ifndef  KPACKET_H
#define  KPACKET_H

typedef int kint;

struct KPacketOut
{
	char head[8];
	kint frame_count;
	
	kint offset_from_top;
	kint offset_from_left;
	kint offset_from_bottom;
	kint offset_from_right;
	
	kint speed_top;
	kint speed_left;
	kint speed_bottom;
	kint speed_right;
	
	kint distance;
	kint angle;
	kint target_number;

    KPacketOut():
        frame_count(0),
	
        offset_from_top(0),
        offset_from_left(0),
        offset_from_bottom(0),
        offset_from_right(0),
	
        speed_top(0),
        speed_left(0),
        speed_bottom(0),
        speed_right(0),
	
        distance(0),
        angle(0),
        target_number(0)
    {
        head[0] = 'H';
        head[1] = 'e';
        head[2] = 'a';
        head[3] = 'd';
        head[4] = 'e';
        head[5] = 'r';
        head[6] = '0';
        head[7] = 0;
    }
        
};


#endif // KPACKET_H
