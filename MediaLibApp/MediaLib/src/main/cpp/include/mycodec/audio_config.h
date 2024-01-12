#ifndef __AUDIO_CONFIG_H
#define __AUDIO_CONFIG_H


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

typedef struct AudioParam
{
	AudioFmt _audio_fmt;	
	AudioCodecType _codec_type;	
	int _bitrate;
	int _sample_rate;
	int _channel_no;

	AudioParam() {
		memset(this, 0, sizeof(AudioParam));
	}
	AudioParam(AudioFmt fmt, AudioCodecType type, int bitrate, int frate, int channelno) {
		_audio_fmt = fmt;
		_codec_type = type;
		_bitrate = bitrate;
		_sample_rate = frate;
		_channel_no = channelno;
	}
	AudioParam(const AudioParam& obj) {
		_audio_fmt = obj._audio_fmt;
		_codec_type = obj._codec_type;
		_bitrate = obj._bitrate;
		_sample_rate = obj._sample_rate;
		_channel_no = obj._channel_no;
	}
	AudioParam& operator = (const AudioParam& obj) {
		if(this != &obj) {
			_audio_fmt = obj._audio_fmt;
			_codec_type = obj._codec_type;
			_bitrate = obj._bitrate;
			_sample_rate = obj._sample_rate;
			_channel_no = obj._channel_no;
		}
		return *this;
	}
}AudioParam;


#endif
