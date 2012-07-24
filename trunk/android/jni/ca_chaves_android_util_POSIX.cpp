/*
 * @(#) android/jni/ca_chaves_android_util_POSIX.cpp
 */

#include "ca_chaves_android.hpp"
#include "ca_chaves_android_util_POSIX.hpp"

#include <sys/stat.h>

/*-------------------------------------- native_chmod */

static inline jint
native_chmod( JNIEnv *, jstring, jint) //
    __attribute__((__always_inline__));

jint
native_chmod( //
        JNIEnv * const env, //
        jstring const path, jint const mode) {

    if (0 == path) {
        LOGE("chmod failed: path is null: EFAULT\n");
        return EFAULT;
    }

    char const * const path_p = env->GetStringUTFChars(path, 0);
    if (0 == path_p) {
        LOGE("chmod failed: GetStringUTFChars: ENOMEM\n");
        return ENOMEM;
    }

    int result = 0;
    if (0 != chmod(path_p, mode)) {
        result = errno;
        LOGE("chmod 0%o %s failed: errno %d: %s\n", //
                mode, path_p, result, strerror(result));
    } else {
        LOGV("chmod 0%o %s\n", mode, path_p);
    }

    env->ReleaseStringUTFChars(path, path_p);

    return result;
}

/*-------------------------------------- jni call */

jint
Java_ca_chaves_android_util_POSIX_native_1chmod( //
        JNIEnv * const env, //
        jclass const /* cls */, //
        jstring const path, //
        jint const mode) {

    return native_chmod(env, path, mode);
}

/*------------------------------------- The End */
