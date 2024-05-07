/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-12-22 20:09:51
 * LastEditors: liguoqiang
 * LastEditTime: 2024-02-14 10:33:01
 * Description: 
********************************************************************************/
#ifndef __RTMP_SRV_H__
#define __RTMP_SRV_H__

#include "thread.h"
#include "rtmp_stream.h"

#ifdef __cplusplus
extern "C" {
#endif


// rtmp request struct define
typedef struct _RTMP_REQUEST
{
  char hostname[32];
  int rtmpport;
  int protocol;
  int chunkSize;
  int bw;
  int timeout;
  int bufferTime;
} RTMP_REQUEST;

// rtmp server struct define
typedef struct
{
  int socket;
  int state;
  char *host;
  int port;
  THANDLE thread;
  int needRawVideo;
} RTMP_SERVER;

typedef struct
{
  char encoder[64];
  int duration;
  int width;
  int height;
  int fps;
  char audioid[32];
  int audioVolume;
} MetaData;

RTMP_SERVER* openRtmpServer(RTMP_REQUEST *rtmpRequest, const char *logFileName);
RTMP_REQUEST *getDefaultRtmpRequest();
void closeRtmpServer(RTMP_SERVER *rtmpServer);
void setRtmpVideoCallback(VideoCallbackFunc videoFunc, void *user_data);
void setRtmpAudioCallback(AudioCallbackFunc audioFunc, void *user_data);
void setRtmpBeginPublishCallback(BeginPublishFunc beginPublishFunc, void *user_data);
void setRtmpSpsPpsCallback(SpsPpsCallbackFunc spsPpsFunc, void *user_data);
int getRtmpMetaData(char** metaData);
void getRtmpDataFrame(MetaData *metaData);

#ifdef __cplusplus
}
#endif

#endif