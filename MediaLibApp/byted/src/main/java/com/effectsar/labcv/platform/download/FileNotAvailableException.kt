package com.effectsar.labcv.platform.download

import java.io.IOException

class FileNotAvailableException(msg:String) : IOException(msg)
class UnzipErrorException(msg:String) : IOException(msg)