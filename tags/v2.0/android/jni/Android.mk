#
# @(#) android/jni/Android.mk
#

LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/../../share/jni/Android.mk

#--------------------------------------- ca.chaves.android

include $(CLEAR_VARS)

LOCAL_MODULE    := ca.chaves.android

LOCAL_CFLAGS    := $(_DROID_CFLAGS)
LOCAL_CPPFLAGS  := $(_DROID_CPPFLAGS)
LOCAL_LDFLAGS   := $(_DROID_LDFLAGS)

# All source file names to be included in lib, separated by a whitespace
LOCAL_SRC_FILES := \
        ca_chaves_android.cpp   \
        ca_chaves_android_util_POSIX.cpp \

# Additional libraries, maybe more than actually needed
LOCAL_SHARED_LIBRARIES :=

# JNI headers
LOCAL_C_INCLUDES := $(JNI_H_INCLUDE)

# JNI libraries
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)

#-------------------------------------- The End
