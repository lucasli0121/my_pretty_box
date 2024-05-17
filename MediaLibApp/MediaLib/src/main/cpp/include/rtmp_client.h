/******************************************************************************
 * Author: liguoqiang
 * Date: 2024-01-16 10:04:05
 * LastEditors: liguoqiang
 * LastEditTime: 2024-05-17 20:16:09
 * Description: 
********************************************************************************/
#ifndef __RTMP_CLIENT_H__
#define __RTMP_CLIENT_H__

#include "librtmp/rtmp_sys.h"
#include "librtmp/log.h"
#include "packet_fifo.h"
#include "thread.h"



typedef struct SpsData
{
    char* sps;
    int sps_size;
    char* pps;
    int pps_size;
}SpsData;
typedef struct RTMP_CLIENT
{
    char rtmpUrl[255];
    char* metaDataBuf;
    int metaDataSize;
    SpsData * spsData;
    RTMP * rtmp;
    FifoAgent * dataFifo;
    int runThread;
    THANDLE clientThreadId;
    int hasSendSps;
    int hasSendMeta;
} RTMP_CLIENT;

RTMP_CLIENT* startRtmpClient(char *url, char *logFileName);
void stopRtmpClient(RTMP_CLIENT *rtmp);
void beforeSendData(RTMP_CLIENT *rtmp);
int sendX264VideoData(RTMP_CLIENT *rtmp, const char *data, int size, unsigned int timestamp, int absTimestamp, int keyframe);
int sendRawVideoData(RTMP_CLIENT *rtmp, const char *data, int size, unsigned int timestamp, int absTimestamp);
int sendAudioData(RTMP_CLIENT *rtmp, const char *data, int size, unsigned int timestamp, int absTimestamp);
void setMetaData(RTMP_CLIENT *rtmp, char* data, int size);
void setDataFrame(RTMP_CLIENT *rtmp, int duration, int width, int height, int fps, char* encoder, char* audioid, int audiovolume);
void changeChunkSize(RTMP_CLIENT *client, int size);
#endif