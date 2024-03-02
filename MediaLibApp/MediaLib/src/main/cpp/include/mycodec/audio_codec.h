/*
 * @Author: liguoqiang
 * @Date: 2017-07-24 11:00:42
 * @LastEditors: liguoqiang
 * @LastEditTime: 2024-02-25 12:00:34
 * @Description: 
 */
/*
 * Audio utils that can encode and decode audio data.
 * Currently support convert from each other that include pcm
 *
 * by. liguoqiang
 */

#ifndef __AUDIO_CODEC_H
#define __AUDIO_CODEC_H

#ifdef __cplusplus
extern "C" {
#endif

#include "stream_buffer.h"
#include "audio_def.h"

struct AVCodecContext;
struct AVFrame;
struct AVCodec;

class LibExport AudioCodec
{
public:
	AudioCodec(AudioParam param);
	~AudioCodec();
	int init();
	void unini();
	void set_audio_param(const AudioParam& param);
	AudioParam* get_audio_param();
	int encode_audio(vbyte8_ptr src, int srclen);
	int decode_audio(vbyte8_ptr src, int srclen); 
private:
	int alloc_avcodec();
	int open_codec();
	void close_codec();
	int create_encoder();
	int create_decoder();
private:
	AudioParam _audio_param;
	struct AVCodecContext* _av_codec_ctx;
	AVCodecParserContext *_av_parser;
	const AVCodec * _av_codec;
	bool _has_open;
	uint16_t *  _sample_buf;
	uint32_t _sample_size;
	AVPacket* _outPk;
	
};

#ifdef __cplusplus
}
#endif

#endif
