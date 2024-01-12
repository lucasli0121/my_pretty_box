/******************************************************************************
 * Author: liguoqiang
 * Date: 2021-06-15 17:16:24
 * LastEditors: liguoqiang
 * LastEditTime: 2024-01-02 10:25:27
 * Description: 
********************************************************************************/
/*
 * StreamBuffer make a size block buffer which is a loop buffer
 *
 * by liguoqiang
 *
 *
 */
#ifndef __STREAM_BUFFER_H
#define __STREAM_BUFFER_H

#include "libconfig.h"

class StreamBuffer
{
public:	
	StreamBuffer(vint32_t size = 10*1024)
		: _head(0), _tail(0), _length(0)
	{
		_buf_size = size;
		_buffer = new vbyte8_t[_buf_size];
	}
	~StreamBuffer()
	{
		if(_buffer) {
			delete [] _buffer;
		}
	}

	int read(vbyte8_ptr buf, vint32_t len);
	int remove(vbyte8_ptr buf, vint32_t len);
	int append(vbyte8_ptr buf, vint32_t len);
	bool empty() const
	{
		return (_length == 0);
	}
	bool full() const
	{
		return (_length == _buf_size);
	}
	vint32_t length() const
	{
		return _length;
	}
	
	vint32_t total_size() const {
		return _buf_size;
	}

	vint32_t remain_size() const;
	
	void clear()
	{
		this->_head = this->_tail = 0;
		_length = 0;
	}
	StreamBuffer & operator++ (void);
	StreamBuffer operator++ (int);
	vbyte8_t operator*(void);
	void advance(vint32_t len);
	void distance();
public:
	vint32_t  _buf_size;
	vint32_t  _head;
	vint32_t  _tail;
	vbyte8_ptr _buffer;
	vint32_t  _length;
};

#include "stream_buffer.inl"

#endif
