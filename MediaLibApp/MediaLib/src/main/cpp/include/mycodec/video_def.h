/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-05-17 20:09:36
 * Description: 
********************************************************************************/
#ifndef __VIDEO_DEFINE_H
#define __VIDEO_DEFINE_H

#include "libconfig.h"

#ifndef MIN_WIDTH
#define MIN_WIDTH 320//176
#endif

#ifndef MIN_HEIGHT
#define MIN_HEIGHT 240//144
#endif

typedef enum VideoFmt
{ 
	VIDEO_NULL_CODEC = 0,
	VIDEO_MPEG4_CODEC,
	VIDEO_H264_CODEC,
	VIDEO_MEDIA_CODEC,
}VideoFmt;

typedef enum CodecType
{
	VIDEO_ENCODE_TYPE = 1,
	VIDEO_DECODE_TYPE,
}CodecType;

typedef void (*DecodeCallbackFunc)(vbyte8_ptr dest, vint32_t destlen, vint32_t w, vint32_t h, vint32_t keyframe, void*);
typedef void (*EncodeCallbackFunc)(vbyte8_ptr dest, vint32_t destlen, vint64_t pts, vint64_t dts, vint32_t keyframe, void*);

typedef struct VideoParam
{
	VideoFmt _video_fmt;
	CodecType _codec_type;
	int _width;
	int _height;
	int _bitrate;
	int _frame_rate;
	int _max_b_frame;
	void* _user_data;
	DecodeCallbackFunc _decode_callback_func;
	EncodeCallbackFunc _encode_callback_func;
	VideoParam() {
		memset(this, 0, sizeof(VideoParam));
	}
	VideoParam(VideoFmt fmt, CodecType type, int w, int h, int bitrate, int frate) {
		_video_fmt = fmt;
		_codec_type = type;
		_width = w;
		_height = h;
		_bitrate = bitrate;
		_frame_rate = frate;
	}
	VideoParam(const VideoParam& obj) {
		_video_fmt = obj._video_fmt;
		_codec_type = obj._codec_type;
		_width = obj._width;
		_height = obj._height;
		_bitrate = obj._bitrate;
		_frame_rate = obj._frame_rate;
		_decode_callback_func = obj._decode_callback_func;
		_encode_callback_func = obj._encode_callback_func;
		_user_data = obj._user_data;
	}
	VideoParam& operator = (const VideoParam& obj) {
		if(this != &obj) {
			_video_fmt = obj._video_fmt;
			_codec_type = obj._codec_type;
			_width = obj._width;
			_height = obj._height;
			_bitrate = obj._bitrate;
			_frame_rate = obj._frame_rate;
			_decode_callback_func = obj._decode_callback_func;
			_encode_callback_func = obj._encode_callback_func;
			_user_data = obj._user_data;
		}
		return *this;
	}
}VideoParam;


#endif
