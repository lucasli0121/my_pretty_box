/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-12-22 20:09:51
 * LastEditors: liguoqiang
 * LastEditTime: 2023-12-31 23:01:40
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
} RTMP_SERVER;

RTMP_SERVER* openRtmpServer(RTMP_REQUEST *rtmpRequest);
RTMP_REQUEST *getDefaultRtmpRequest();
void closeRtmpServer(RTMP_SERVER *rtmpServer);
void setRtmpVideoCallback(VideoCallbackFunc videoFunc, void *user_data);
void setRtmpAudioCallback(AudioCallbackFunc audioFunc, void *user_data);

#ifdef __cplusplus
}
#endif

#endif