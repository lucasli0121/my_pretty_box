/******************************************************************************
 * Author: liguoqiang
 * Date: 2024-01-16 10:04:05
 * LastEditors: liguoqiang
 * LastEditTime: 2024-01-18 12:01:43
 * Description: 
********************************************************************************/
#ifndef __RTMP_CLIENT_H__
#define __RTMP_CLIENT_H__

#include "librtmp/rtmp_sys.h"

RTMP* startRtmpClient(char *url);
void stopRtmpClient(RTMP *rtmp);

int sendX264VideoData(RTMP *rtmp, const char *data, int size, unsigned int timestamp);
void setMetaData(RTMP *rtmp, int width, int height, int framerate, int duration, char *encoder);
#endif