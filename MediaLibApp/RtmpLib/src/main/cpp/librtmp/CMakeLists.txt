set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
file(GLOB rtmp_src *.c)
add_library(librtmp STATIC ${rtmp_src})