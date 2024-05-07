/*
 * @Author: liguoqiang
 * @Date: 2024-01-13 07:09:03
 * @LastEditors: liguoqiang
 * @LastEditTime: 2024-02-27 19:50:02
 * @Description: 
 */
/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-12-23 23:32:02
 * LastEditors: liguoqiang
 * LastEditTime: 2024-04-26 08:53:45
 * Description: 
********************************************************************************/
#ifndef __RTMP_SRV_PROTO_H__
#define __RTMP_SRV_PROTO_H__

#include "librtmp/rtmp_sys.h"
#include "thread.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef void (*VideoCallbackFunc)(const char *data, int size, unsigned long timestamp, uint8_t absTimestamp, int key, void* user_data);
typedef void (*AudioCallbackFunc)(const char *data, int size, unsigned long timestamp, uint8_t absTimestamp, void* user_data);
typedef void (*BeginPublishFunc)(void* user_data);
typedef void (*SpsPpsCallbackFunc)(const char *sps, int spsSize, const char *pps, int ppsSize, void* user_data);

enum
{
  STREAMING_LISTENING = 0,
  STREAMING_ACCEPTING,
  STREAMING_IN_PROGRESS,
  STREAMING_STOPPING,
  STREAMING_STOPPED
};

typedef struct
{
  int state;
  int streamID;
  int arglen;
  int argc;
  uint32_t filetime; /* time of last download we started */
  AVal filename;     /* name of last download */
  char *connect;
  int chunkSize;
  uint32_t dStartOffset;
  uint32_t dStopOffset;
  uint32_t nTimeStamp;
  THANDLE thread;
  RTMP * rtmp;
  RtmpMutex *mutex;
  int needRawVideo;
  BeginPublishFunc beginPublishFunc;
  VideoCallbackFunc videoFunc;
  AudioCallbackFunc audioFunc;
  SpsPpsCallbackFunc spsPpsFunc;
  void *user_data;
  char* videoBuffer;
  int videoBufferSize;
  int duration;
  int width;
  int height;
  int framerate;
  char encoder[64];
  char audioid[32];
  int audioVolume;
  char* dataFrameBuf;
  int dataFrameSize;
} RTMP_STREAM;
RTMP_STREAM* initRtmpStream(int chunkSize, int needRawVideo);
int openRtmpStreaming(RTMP_STREAM*, int sockfd );
void closeRtmpStreaming(RTMP_STREAM *rtmpStream);
int streamIsConnect(RTMP_STREAM *rtmpStream);

#ifdef __cplusplus
}
#endif

#endif