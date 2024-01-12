#ifndef __LOG_H
#define __LOG_H

int log_init(const char*);
void log_unini();

int put_log(const char* fmt,...);

#ifndef TRACE_LOG
#define TRACE_LOG put_log
#endif

char * av_err2str2(int errnum);

#endif
