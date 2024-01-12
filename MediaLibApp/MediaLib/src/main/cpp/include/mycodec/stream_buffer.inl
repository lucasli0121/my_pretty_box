inline vint32_t StreamBuffer::remain_size() const
{
	return (total_size() - length());
}

/*
 * advance a length to new position
 *
 *
 */
inline void StreamBuffer::advance(vint32_t skiplen)
{
	vint32_t len = skiplen > _length ? _length : skiplen;
	_head += len;
	_head %= _buf_size;
	_length -= len;
}
/*
 *
 *
 *
 */
inline int StreamBuffer::read(vbyte8_ptr buf, vint32_t len)
{
	vint32_t i = 0;
	vint32_t size = 0;
	vint32_t old_head = _head;
	if(length() < len) {
		return -1;
	}
	for(i = 0; i < len; i += size) {
		size = (old_head >= _tail ? _buf_size - old_head : _tail - old_head);
		if(size > len) {
			size = len;
		}
		memcpy(buf + i, _buffer + old_head, size);
		old_head = (old_head + size) % _buf_size;
	}
	
	return 0;

}

inline int StreamBuffer::remove(vbyte8_ptr buf, vint32_t len)
{
	vint32_t i = 0;
	vint32_t size = 0;
	vint32_t needlen = 0;
	
	if(length() < len) {
		return -1;
	}
	needlen = len;
	for(i = 0; i < len; i += size) {
		size = (_head >= _tail ? _buf_size - _head : _tail - _head);
		if(size > needlen) {
			size = needlen;
		}
		memcpy(buf + i, _buffer + _head, size);
		_head = (_head + size) % _buf_size;
		_length -= size;
		needlen -= size;
	}
	return 0;
}

inline int StreamBuffer::append(vbyte8_ptr buf, vint32_t len)
{
	vint32_t i = 0;
	vint32_t size = 0;
	vint32_t needlen = 0;
	if(remain_size() < len || full()) {
		return -1;
	}
	needlen = len;
	for(i = 0; i < len && !full(); i += size) {
		size = (_tail >= _head ? _buf_size - _tail : _head - _tail);
		if(size > needlen) {
			size = needlen;
		}
		memcpy(_buffer + _tail, buf + i, size);
		_tail = (_tail + size) % _buf_size;
		_length += size;
		needlen -= size;
	}
	return 0;
}

inline StreamBuffer & StreamBuffer::operator++ (void)
{
	_head = ++_head % _buf_size;
	_length--;
	return *this;
}

inline StreamBuffer StreamBuffer::operator++ (int)
{
	StreamBuffer temp = *this;
	_head = ++_head % _buf_size;
	_length--;
	return temp;
}

inline vbyte8_t StreamBuffer::operator*(void)
{
	return *(_buffer + _head % _buf_size);
}

