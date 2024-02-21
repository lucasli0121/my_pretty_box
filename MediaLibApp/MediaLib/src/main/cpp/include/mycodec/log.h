/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-02-06 06:55:23
 * Description: 
********************************************************************************/
#ifndef __LOG_H
#define __LOG_H

#ifdef __cplusplus
extern "C" {
#endif

int log_init(const char*);
void log_unini();

int put_log(int level, const char* fmt,...);

#ifndef TRACE_LOG
#define TRACE_LOG put_log
#endif
void log_set_level(int level);
char * av_err2str2(int errnum);

#ifdef __cplusplus
}
#endif

#endif
