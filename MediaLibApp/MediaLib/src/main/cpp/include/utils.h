#ifndef __UTILS_H__
#define __UTILS_H__

#ifdef WIN32
typedef unsigned __int64 uint64;
#else
typedef unsigned long long int uint64;
#endif

float getMSecOfTime();

#endif