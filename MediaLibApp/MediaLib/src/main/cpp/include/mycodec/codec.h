/*
 *
 *
 *
 */
#ifndef __CODEC_H
#define __CODEC_H

#ifdef __cplusplus
extern "C" {
#endif

#include "video_def.h"
#include "audio_def.h"

#ifndef FF_INPUT_BUFFER_PADDING_SIZE
#define FF_INPUT_BUFFER_PADDING_SIZE 8
#endif

vint32_t codec_init(const_char_ptr logfile);
void codec_unini();

/*
 */
void test_hw_decode(vint64_t codec, const_char_ptr filename, const_char_ptr outfilename);
void test_muxing_decode(const_char_ptr codename, const_char_ptr filename, const_char_ptr outfilename);
vint64_t create_video_codec(VideoFmt , CodecType,  vint32_t, vint32_t, vint32_t, vint32_t );
void release_video_codec(vint64_t);
void set_jni_env(void* env);
void set_video_size(vint64_t codec, vint32_t w, vint32_t h);
VideoParam* get_video_param(vint64_t codec);
void set_video_param(vint64_t codec, const VideoParam* param);

vint32_t encoder_from_rgba(vint64_t codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);

/*encoder video source format is nv21*/
vint32_t encoder_from_nv21(vint64_t codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/*encoder video source format is nv12*/
vint32_t encoder_from_nv12(vint64_t codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/*encoder video source format is yuv420p*/
vint32_t encoder_from_yuv420p(vint64_t codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/**/
vint32_t encoder_from_uyvy(vint64_t codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/**/
vint32_t encoder_from_uyyvyy411(vint64_t codec, vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
void set_video_encode_callback(vint64_t codec, EncodeCallbackFunc func, void* user_data);
/*
 */
vint32_t decode_to_rgb8888(vint64_t codec, vbyte8_ptr srcdata, vint32_t srclen);
vint32_t decode_to_yuv420p(vint64_t codec, vbyte8_ptr srcdata, vint32_t srclen);
vint32_t decode_to_rgba(vint64_t codec, vbyte8_ptr srcdata, vint32_t srclen);
void set_video_decode_callback(vint64_t codec, DecodeCallbackFunc func, void* user_data);

vint32_t yuv420p_to_rgba(vint64_t codec,vbyte8_ptr src,vint32_t srclen,vint32_t srcw,vint32_t srch,vint32_t width,vint32_t height,vbyte8_ptr *dest);
vint32_t rgba_to_yuv420p(vint64_t codec, vbyte8_ptr src, vint32_t srclen,	vint32_t srcw,vint32_t srch,vint32_t destw,vint32_t desth,vbyte8_ptr *dest);
void rgb24_rotate90(
	vint64_t codec,
	vbyte8_ptr src,
	vint32_t width,
	vint32_t height,
	vbyte8_ptr dest,
	bool anti,
	vint32_t rotate_count);
/*
 *
 *
 */
vint64_t create_audio_codec(AudioFmt, AudioCodecType,  vint32_t, vint32_t, vint32_t );
void release_audio_codec(vint64_t); 
AudioParam* get_audio_param(vint64_t codec);
void set_audio_param(vint64_t codec, const AudioParam* param);
vint32_t encode_audio( vint64_t codec, vbyte8_ptr src,	vint32_t srclen);
vint32_t decode_audio(vint64_t codec, vbyte8_ptr src, vint32_t srclen);

#ifdef __cplusplus
}
#endif

#endif
