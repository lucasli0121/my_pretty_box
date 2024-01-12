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
void test_hw_decode(const_char_ptr codename, const_char_ptr filename, const_char_ptr outfilename);
void test_muxing_decode(const_char_ptr codename, const_char_ptr filename, const_char_ptr outfilename);
int64 create_video_codec(VideoFmt , CodecType,  vint32_t, vint32_t, vint32_t, vint32_t );
void release_video_codec(int64);
void set_jni_env(void* env);
void set_video_size(int64 codec, vint32_t w, vint32_t h);

vint32_t encoder_from_RGBA(int64 codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);

/*encoder video source format is nv21*/
vint32_t encoder_from_nv21(int64 codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/*encoder video source format is nv12*/
vint32_t encoder_from_nv12(int64 codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/*encoder video source format is yuv420p*/
vint32_t encoder_from_yuv420p(int64 codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/**/
vint32_t encoder_from_uyvy(int64 codec, vbyte8_ptr srcdata,	vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
/**/
vint32_t encoder_from_uyyvyy411(int64 codec, vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
void set_video_encode_callback(int64 codec, EncodeCallbackFunc func, void* user_data);
/*
 */
vint32_t decode_to_rgb8888(int64 codec, vbyte8_ptr srcdata, vint32_t srclen);
vint32_t decode_to_yuv420p(int64 codec, vbyte8_ptr srcdata, vint32_t srclen);
vint32_t decode_to_rgba(int64 codec, vbyte8_ptr srcdata, vint32_t srclen);
void set_video_decode_callback(int64 codec, DecodeCallbackFunc func, void* user_data);

vint32_t yuv422_to_rgb24(
	int64 codec,
	vbyte8_ptr src,
	vint32_t srclen,
	vint32_t srcw,
	vint32_t srch,
	vint32_t width,
	vint32_t height,
	vbyte8_ptr *dest);
vint32_t rgb24_to_rgb24(
	int64 codec,
	vbyte8_ptr src,
	vint32_t srclen,
	vint32_t srcw,
	vint32_t srch,
	vint32_t destw,
	vint32_t desth,
	vbyte8_ptr *dest);
vint32_t nv21_to_nv21(
	int64 codec,
	vbyte8_ptr src,
	vint32_t srclen,
	vint32_t srcw,
	vint32_t srch,
	vint32_t destw,
	vint32_t desth,
	vbyte8_ptr *dest);
vint32_t rgba_to_yuv420p(
	int64 codec,
	vbyte8_ptr src,
	vint32_t srclen,
	vint32_t srcw,
	vint32_t srch,
	vint32_t destw,
	vint32_t desth,
	vbyte8_ptr *dest);
void rgb24_rotate90(
	int64 codec,
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
int64 create_audio_codec(AudioFmt, AudioCodecType,  vint32_t, vint32_t, vint32_t );
void release_audio_codec(int64); 

vint32_t encode_audio(
	int64 codec,
	vbyte8_ptr src,
	vint32_t srclen,
	vbyte8_ptr* dest,
	vint32_t *destlen);
vint32_t decode_audio(
	int64 codec,
	vbyte8_ptr src,
	vint32_t srclen,
	void (*decode_callback)(vbyte8_ptr dest, vint32_t destlen, void*),
	void* ctx);

#ifdef __cplusplus
}
#endif

#endif
