/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-02-03 23:48:09
 * Description: 
********************************************************************************/
#ifndef __AUDIO_DEFINE_H
#define __AUDIO_DEFINE_H

#include "libconfig.h"

typedef enum AudioFmt
{ 
	AUDIO_NULL_CODEC = 0,
	AUDIO_G726_CODEC,
	AUDIO_MP2_CODEC,
	AUDIO_MP3_CODEC,
}AudioFmt;

typedef enum AudioCodecType
{
	AUDIO_ENCODE_TYPE = 1,
	AUDIO_DECODE_TYPE,
}AudioCodecType;

typedef void (*AudioDecodeCallbackFunc)(vbyte8_ptr dest, vint32_t destlen, void*);
typedef void (*AudioEncodeCallbackFunc)(vbyte8_ptr dest, vint32_t destlen, void*);
typedef struct AudioParam
{
	AudioFmt _audio_fmt;	
	AudioCodecType _codec_type;	
	int _bitrate;
	int _sample_rate;
	int _channel_no;
	void * _user_data;
	AudioDecodeCallbackFunc _decode_callback_func;
	AudioEncodeCallbackFunc _encode_callback_func;

	AudioParam() {
		memset(this, 0, sizeof(AudioParam));
	}
	AudioParam(AudioFmt fmt, AudioCodecType type, int bitrate, int frate, int channelno) {
		_audio_fmt = fmt;
		_codec_type = type;
		_bitrate = bitrate;
		_sample_rate = frate;
		_channel_no = channelno;
		_decode_callback_func = NULL;
		_encode_callback_func = NULL;
		_user_data = NULL;
	}
	AudioParam(const AudioParam& obj) {
		_audio_fmt = obj._audio_fmt;
		_codec_type = obj._codec_type;
		_bitrate = obj._bitrate;
		_sample_rate = obj._sample_rate;
		_channel_no = obj._channel_no;
		_decode_callback_func = obj._decode_callback_func;
		_encode_callback_func = obj._encode_callback_func;
		_user_data = obj._user_data;
	}
	AudioParam& operator = (const AudioParam& obj) {
		if(this != &obj) {
			_audio_fmt = obj._audio_fmt;
			_codec_type = obj._codec_type;
			_bitrate = obj._bitrate;
			_sample_rate = obj._sample_rate;
			_channel_no = obj._channel_no;
			_decode_callback_func = obj._decode_callback_func;
			_encode_callback_func = obj._encode_callback_func;
			_user_data = obj._user_data;
		}
		return *this;
	}
}AudioParam;


#endif
