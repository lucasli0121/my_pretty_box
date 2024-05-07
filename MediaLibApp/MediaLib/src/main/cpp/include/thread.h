/******************************************************************************
 * Author: liguoqiang
 * Date: 2023-12-22 15:13:59
 * LastEditors: liguoqiang
 * LastEditTime: 2024-05-03 23:31:56
 * Description: 
********************************************************************************/
/*  Thread compatibility glue
 *  Copyright (C) 2009 Howard Chu
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RTMPDump; see the file COPYING.  If not, write to
 *  the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *  Boston, MA  02110-1301, USA.
 *  http://www.gnu.org/copyleft/gpl.html
 *
 */

#ifndef __THREAD_H__
#define __THREAD_H__ 1

#ifdef WIN32
#include <windows.h>
#include <process.h>
//#include <sys/cpuset.h>
#define TFTYPE	void
#define TFRET()
#define THANDLE	HANDLE
typedef CRITICAL_SECTION   thread_mutex_t;   //win32 mutex
#else
#include <pthread.h>
#include <unistd.h>
#define _GNU_SOURCE
#include <sched.h>
#define TFTYPE	void *
#define TFRET()	0
#define THANDLE pthread_t
typedef pthread_mutex_t    thread_mutex_t;
#endif


typedef TFTYPE (thrfunc)(void *arg);
THANDLE ThreadCreate(thrfunc *routine, void *args);
void ThreadJoin(THANDLE thread);
typedef struct RtmpMutex {
    thread_mutex_t mutex;
    void (*init)(thread_mutex_t *mutex);
    void (*lock)(thread_mutex_t *mutex);
    void (*unlock)(thread_mutex_t *mutex);
    int (*trylock)(thread_mutex_t *mutex);
    void (*destroy)(thread_mutex_t *mutex);
} RtmpMutex;

RtmpMutex *createMutex();
void destroyMutex(RtmpMutex *mutex);

void bindToCpu(int cpu);




#endif /* __THREAD_H__ */

