/*
 * @Author: liguoqiang
 * @Date: 2017-07-24 11:00:42
 * @LastEditors: liguoqiang
 * @LastEditTime: 2021-06-14 17:02:57
 * @Description: 
 */
#ifndef __VIDEO_CONFIG_H
#define __VIDEO_CONFIG_H

#ifndef MIN_WIDTH
#define MIN_WIDTH 320 //176
#endif

#ifndef MIN_HEIGHT
#define MIN_HEIGHT 240  //144
#endif

typedef enum VideoFmt
{ 
	VIDEO_NULL_CODEC = 0,
	VIDEO_MPEG4_CODEC,
	VIDEO_H264_CODEC
}VideoFmt;

typedef enum CodecType
{
	VIDEO_ENCODE_TYPE = 1,
	VIDEO_DECODE_TYPE,
}CodecType;

typedef struct VideoParam
{
	VideoFmt _video_fmt;
	CodecType _codec_type;
	int _width;                //����
	int _height;               //�߶�
	int _bitrate;              //����
	int _frame_rate;           //֡��/��

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
	}
	VideoParam& operator = (const VideoParam& obj) {
		if(this != &obj) {
			_video_fmt = obj._video_fmt;
			_codec_type = obj._codec_type;
			_width = obj._width;
			_height = obj._height;
			_bitrate = obj._bitrate;
			_frame_rate = obj._frame_rate;
		}
		return *this;
	}
}VideoParam;


#endif
