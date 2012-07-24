#
# @(#) templates/share/jni/Application.mk
# THIS FILE MIGHT HAVE BEEN GENERATED FROM A TEMPLATE.
# PLEASE EDIT ONLY THE ORIGINAL TEMPLATE FILE.
#

# build your code for all the available ABIs -
# see http://developer.mips.com/android/download-android-ndk/
APP_ABI       := armeabi armeabi-v7a mips x86
#APP_ABI      := armeabi armeabi-v7a x86
# optimize on release builds
APP_OPTIM     := release

# target platform
APP_PLATFORM  := android-4

# enable c++ exceptions and rtti
# @see http://code.google.com/p/android/issues/detail?id=20176
#APP_STL      := gnustl_static
#APP_CPPFLAGS += -fexceptions
#APP_CPPFLAGS += -frtti

# typedef long unsigned int *_Unwind_Ptr;
#
# /* Stubbed out in libdl and defined in the dynamic linker.
#  * Same semantics as __gnu_Unwind_Find_exidx().
#  */
# extern "C" _Unwind_Ptr dl_unwind_find_exidx(_Unwind_Ptr pc, int *pcount);
# extern "C" _Unwind_Ptr __gnu_Unwind_Find_exidx(_Unwind_Ptr pc, int *pcount)
# {
#     return dl_unwind_find_exidx(pc, pcount);
# }
#
# static void* g_func_ptr;
# jint JNI_OnLoad(JavaVM *vm, void *reserved)
# {
#    // when i throw exception, linker can't find __gnu_Unwind_Find_exidx
#    // so I force to bind this symbol at shared object load time
#    g_func_ptr = (void*)__gnu_Unwind_Find_exidx;
#    return JNI_VERSION_1_6;
# }

#--------------------------------------- The End
