/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-01-10 17:38:05
 * Description: 
********************************************************************************/
/*
 * Video utils that can encode and decode video data.
 * support mpeg4 and h264
 *
 * by. liguoqiang
 */

#ifndef __VCODEC_H
#define __VCODEC_H 

#include "video_def.h"
#include "libavutil/pixfmt.h"


struct AVCodecContext;
struct AVFrame;
struct AVCodec;

class LibExport VCodec
{
public:
	VCodec(VideoParam param);
	~VCodec();
	int init();
	void unini();
	void set_video_param(const VideoParam& param);
	VideoParam* get_video_param();
	static void test_hw_decode(const_char_ptr codename, const_char_ptr filename, const_char_ptr outfilename);

	void set_video_size(int w, int h);
	
	vint32_t encoder_from_RGBA(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
	/*encoder video source format is yuv420p*/
	vint32_t encoder_from_yuv420p(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
	/*encoder video source format is nv21*/
	vint32_t encoder_from_nv21(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
	/*encoder video source format is nv12*/
	vint32_t encoder_from_nv12(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
	vint32_t encoder_from_uyvy(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
	int encoder_from_uyyvyy411(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
	
	int decoder_to_yuv420p(vbyte8_ptr srcdata, vint32_t srclen);
	/*
	 */
	int decoder_to_rgb8888(vbyte8_ptr srcdata, vint32_t srclen);
	/*
	 */
	int decoder_to_rgba(vbyte8_ptr srcdata, vint32_t srclen);
	void yuv_rotate90(vbyte8_ptr src, vbyte8_ptr dest,int width,int height);
	void nv21_rotate90(vbyte8_ptr src, vbyte8_ptr dest,int width,int height);
	int rgb24_rotate90(vbyte8_ptr src, int width, int height, vbyte8_ptr dest, bool anti = false, int count = 1);
	vint32_t yuv_to_rgb24(vbyte8_ptr src,
			int srclen,
			AVPixelFormat srcFmt,
			int srcw,
			int srch,
			int width,
			int height,
			vbyte8_ptr *dest);
	vint32_t rgb24_to_rgb24( vbyte8_ptr src, int srclen, int srcw, int srch, int destw, int desth, vbyte8_ptr *dest);
	vint32_t nv21_to_nv21(vbyte8_ptr src, int srclen, int srcw, int srch, int destw, int desth, vbyte8_ptr *dest);
	vint32_t rgba_to_yuv420p(vbyte8_ptr src, int srclen, int srcw, int srch, int destw, int desth, vbyte8_ptr *dest);
private:
	int open_codec();
	void close_codec();
	int create_encoder();
	int create_decoder();
	int encode_video_common_func(
		vbyte8_ptr srcdata,
		vint32_t srclen,
		int src_width,
		int src_height,
		AVPixelFormat src_fmt,
		int key_frame);
	
	int do_encode(AVFrame* frame);
	int decode_video_common_func( vbyte8_ptr srcdata, vint32_t srclen, AVPixelFormat dest_fmt);
	int do_decode( AVPacket *avpkt, AVPixelFormat dest_fmt);
	vint32_t translate_diff_fmt(vbyte8_ptr srcdata,
		int srclen,
		AVPixelFormat srcFmt,
		int srcw,
		int srch,
		AVPixelFormat destFmt,
		int dstw,
		int dsth,
		vbyte8_ptr *dest);
	int video_translate(AVFrame* src, int srcw, int srch, AVPixelFormat srcfmt, int dstw, int dsth, AVPixelFormat dstfmt, vbyte8_ptr*dest);		
	vbyte8_ptr get_av_buffer(int size);
private:
	VideoParam _video_param;
	struct AVFormatContext* _av_fmt_ctx;
	struct AVCodecContext* _av_codec_ctx;
	AVCodecParserContext* _av_ctx_parser;
	AVPacket *_av_pkt;
	AVFrame *_av_frame;
	struct AVCodec * _av_codec;
	vbyte8_ptr _av_buffer;
	vbyte8_ptr _scale_buffer;
	vint32_t _scale_size;
	bool _has_open;
	vint32_t _frame_interval;
};

#endif
