/******************************************************************************
 * Author: liguoqiang
 * Date: 2024-02-16 10:56:46
 * LastEditors: liguoqiang
 * LastEditTime: 2024-02-17 21:28:18
 * Description: 
********************************************************************************/
#ifndef __PACKET_FIFO_H
#define __PACKET_FIFO_H

#include "librtmp/rtmp_sys.h"
#include "librtmp/log.h"
#include "thread.h"

typedef struct PacketFifo
{
    RTMPPacket *packet;
    PacketFifo *next;
} PacketFifo;

typedef struct FifoAgent {
    PacketFifo *packetFifoHeader;
    PacketFifo *packetFifoTail;
    RtmpMutex *fifoMutex;
    uint32_t size;
    void(*initFifo)(FifoAgent*);
    void(*uninitFifo)(FifoAgent*);
    void(*pushFifo)(FifoAgent*, RTMPPacket*);
    RTMPPacket*(*frontFifo)(FifoAgent*);
    void(*popFifo)(FifoAgent*);
    void(*clearFifo)(FifoAgent*);
} FifoAgent;

FifoAgent* createFifoAgent();
void releaseFifoAgent(FifoAgent *fifoAgent);

#endif