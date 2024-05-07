/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-03-09 11:46:58
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

#ifdef __cplusplus
extern "C" {
#endif

#include "video_def.h"
#include "libavutil/pixfmt.h"
#include "libavutil/hwcontext.h"

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
	void test_hw_decode(const_char_ptr filename, const_char_ptr outfilename);

	void set_video_size(int w, int h);
	
	vint32_t encoder_from_rgba(vbyte8_ptr srcdata, vint32_t srclen, vint32_t width, vint32_t height, vint32_t keyframe);
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
	vint32_t yuv420p_to_rgba(vbyte8_ptr src, int srclen, int srcw, int srch, int width, int height,	vbyte8_ptr *dest);
	vint32_t rgba_to_yuv420p(vbyte8_ptr src, int srclen, int srcw, int srch, int destw, int desth, vbyte8_ptr *dest);
private:
	int open_codec();
	void close_codec();
	int create_encoder();
	int create_decoder();
	int rebuild_264_decoder();
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
	vint32_t convert_diff_fmt(vbyte8_ptr srcdata, int srclen, AVPixelFormat srcFmt, int srcw, int srch, AVPixelFormat destFmt, int dstw, int dsth, vbyte8_ptr *dest);
	vint32_t convert_diff_fmt(AVFrame* srcframe, AVPixelFormat srcFmt, int srcw, int srch, AVPixelFormat destFmt, int dstw, int dsth, vbyte8_ptr *dest);
	int convert_fmt_with_ff(AVFrame* src, int srcw, int srch, AVPixelFormat srcfmt, int dstw, int dsth, AVPixelFormat dstfmt, vbyte8_ptr*dest);
	int convert_fmt_with_libyuv(AVFrame* src, int srcw, int srch, AVPixelFormat srcfmt, int dstw, int dsth, AVPixelFormat dstfmt, vbyte8_ptr*dest);
	int scale_video_common_func(AVFrame* srcframe, AVPixelFormat fmt, int destw, int desth, AVFrame** destframe);
	int scale_yuv420p(AVFrame* srcframe, int destw, int desth, AVFrame** destframe);
	int scale_nv12(AVFrame* srcframe, int destw, int desth, AVFrame** destframe);
	vbyte8_ptr get_av_buffer(int size);
	vbyte8_ptr get_scale_buffer(int size);
	vbyte8_ptr get_scale_buffer2(int size);
	int hw_coder_init(AVCodecContext *ctx, const enum AVHWDeviceType type);
	void find_hw_pix_fmt();
	int decode_write_file(FILE* output_file, AVCodecContext* avctx, AVPacket* packet);
private:
	VideoParam _video_param;
	struct AVFormatContext* _av_fmt_ctx;
	struct AVCodecContext* _av_codec_ctx;
	AVCodecParserContext* _av_ctx_parser;
	AVPacket *_av_pkt;
	AVFrame *_av_frame;
	const AVCodec * _av_codec;
	vbyte8_ptr _av_buffer;
	vint32_t _av_buffer_size;
	vbyte8_ptr _scale_buffer;
	vint32_t _scale_size;
	vbyte8_ptr _scale_buffer2;
	vint32_t _scale_size2;
	bool _has_open;
	vint32_t _frame_interval;
	AVBufferRef * _hw_device_ctx;
	enum AVHWDeviceType _hw_device_type;
	vbyte8_ptr _sps;
	vint32_t _spslen;
	vbyte8_ptr _pps;
	vint32_t _ppslen;
	bool _use_libyuv;
};

#ifdef __cplusplus
}
#endif
#endif
