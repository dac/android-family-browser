/*
 * @(#) android/jni/ca_chaves_android.hpp
 */

#ifndef CA_CHAVES_ANDROID_HPP_INCLUDED
#define CA_CHAVES_ANDROID_HPP_INCLUDED

#include <assert.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#include <jni.h>
#include <android/log.h>

#define LOG_TAG     "ca.chaves.android"
#define LOGE(...)   { __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__); }

#ifndef NDEBUG      /* release build */
# define LOGV(...)  { ; }
#else               /* debug build */
# define LOGV(...)  { __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__); }
#endif

#define ARRAY_SIZE(array)   ( sizeof((array)) / sizeof((array)[0]) )

#endif /* CA_CHAVES_ANDROID_HPP_INCLUDED */

/*------------------------------------- The End */
