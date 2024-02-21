/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-02-08 00:18:02
 * Description: 
********************************************************************************/

#ifndef __LIB_CONFIG_H
#define __LIB_CONFIG_H

/*
#ifdef WIN32
#	ifdef MEDIA_LIB_EXPORT
#		define LibExport __declspec (dllexport)
#		pragma warning(disable:4275)
#    	pragma warning(disable:4251)
#	else
#		define LibExport __declspec (dllimport)
#	endif
#else
#	define LibExport
#endif
*/

#ifdef _WIN32_WINNT
#undef _WIN32_WINNT
#define WIN32_LEAN_AND_MEAN
#define _WIN32_WINNT 0x0400
typedef unsigned __int64 vuint64_t;
typedef __int64 vint64_t;

#include <cstring>

#else
typedef unsigned long long vuint64_t;
typedef long long vint64_t;
#include <string.h>
#endif

#define LibExport

typedef int                   vint32_t;
typedef unsigned int          vuint32_t;
typedef long                  vlong_t;
typedef unsigned long         vulong_t;
typedef char                  vchar8_t;
typedef unsigned char         vbyte8_t;
typedef unsigned char *       vbyte8_ptr;
typedef const unsigned char * const_vbyte8_ptr;
typedef const char*           const_char_ptr;
typedef char*                 char_ptr;

#ifndef TRUE
#define TRUE  (1)
#endif
#ifndef FALSE
#define FALSE (0)
#endif

#define video_sn_id   u_long
#define INVALID_UID   (-1)

#endif

