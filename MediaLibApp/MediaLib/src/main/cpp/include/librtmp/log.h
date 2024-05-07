/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-12-22 15:14:00
 * LastEditors: liguoqiang
 * LastEditTime: 2024-05-04 19:41:25
 * Description: 
********************************************************************************/
/*
 *  Copyright (C) 2008-2009 Andrej Stepanchuk
 *  Copyright (C) 2009-2010 Howard Chu
 *
 *  This file is part of librtmp.
 *
 *  librtmp is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1,
 *  or (at your option) any later version.
 *
 *  librtmp is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with librtmp see the file COPYING.  If not, write to
 *  the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *  Boston, MA  02110-1301, USA.
 *  http://www.gnu.org/copyleft/lgpl.html
 */

#ifndef __RTMP_LOG_H__
#define __RTMP_LOG_H__

#include <stdio.h>
#include <stdarg.h>
#include <stdint.h>
#include "../thread.h"

#ifdef __cplusplus
extern "C" {
#endif
/* Enable this to get full debugging output */
/* #define _DEBUG */

#ifdef _DEBUG
#undef NODEBUG
#endif

typedef enum
{ RTMP_LOGCRIT=0, RTMP_LOGERROR, RTMP_LOGWARNING, RTMP_LOGINFO,
  RTMP_LOGDEBUG, RTMP_LOGDEBUG2, RTMP_LOGALL
} RTMP_LogLevel;

struct RTMP_LogContext;
typedef void (RTMP_LogCallback)(RTMP_LogContext *ctx, int level, const char *fmt, va_list);

RTMP_LogContext* RTMP_LogInit(void);
void RTMP_LogUninit(RTMP_LogContext* context);
void RTMP_LogSetCallback(RTMP_LogContext* r, RTMP_LogCallback *cb);
void RTMP_LogSetOutput(RTMP_LogContext* r, FILE *file);
void RTMP_LogPrintf(const char *format, ...);
void RTMP_Log(RTMP_LogContext* r, int level, const char *format, ...);
void RTMP_LogHex(RTMP_LogContext* r, int level, const uint8_t *data, unsigned long len);
void RTMP_LogHexString(RTMP_LogContext* r,int level, const uint8_t *data, unsigned long len);
void RTMP_LogSetLevel(RTMP_LogContext* r,RTMP_LogLevel lvl);
RTMP_LogLevel RTMP_LogGetLevel(RTMP_LogContext* r);

typedef struct RTMP_LogContext
{
    RTMP_LogLevel level;
    RTMP_LogCallback *cb;
    FILE *file;
    RtmpMutex *mutex;

} RTMP_LogContext;

#ifdef __cplusplus
}
#endif

#endif
