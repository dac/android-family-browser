/*
 * @(#) android/jni/ca_chaves_android.cpp
 */

#include "ca_chaves_android.hpp"
#include "ca_chaves_android_util_POSIX.hpp"

/*-------------------------------------- public symbols */

/**
 * Declarations for JNI_OnLoad and JNI_OnUnload from <jni.h>
 *
 * These functions might be defined in libraries which we load;
 * the JNI implementation calls them at the appropriate times.
 */
extern "C" {
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *);
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *);
}

/*-------------------------------------- register_native_methods */

static char const class_name[] = "ca/chaves/android/util/POSIX";

static JNINativeMethod const methods_table[] = { //
    { //
        "native_chmod", //
        "(Ljava/lang/String;I)I", //
        (void *) Java_ca_chaves_android_util_POSIX_native_1chmod, //
    }, //
};

/**
 * Explicitly register all methods for our class
 *
 * @return 0 on success, -1 on error
 */
static inline int
register_native_methods(JNIEnv* const env) {

    /* look up the class */
    jclass const cls = env->FindClass(class_name);
    if (0 == cls) {
        LOGE("FindClass failed: %s\n", class_name);
        return -1;
    }

    /* register all the methods */
    if (JNI_OK != env->RegisterNatives(cls, methods_table, ARRAY_SIZE(methods_table))) {
        LOGE("RegisterNatives failed: %s\n", class_name);
        return -1;
    }

    return 0;
}

/*-------------------------------------- JNI_OnLoad */

/**
 * This is called by the VM when the shared library is first loaded
 *
 * @return JNI_VERSION_1_6 on success, -1 on failure
 */
jint
JNI_OnLoad(JavaVM * const vm, void *) {

    JNIEnv* env = 0;
    // LOGV("loading native library");

    if (JNI_OK != vm->GetEnv((void**) &env, JNI_VERSION_1_6)) {
        LOGE("GetEnv failed\n");
        return -1;
    };

    assert(0 != env);

    if (0 != register_native_methods(env)) {
        LOGE("register_native_methods failed\n");
        return -1;
    };

    /* success -- return valid version number */
    // LOGV("native library loaded");
    return JNI_VERSION_1_6;
}

/*-------------------------------------- JNI_OnUnload */

/**
 * This is called when the class loader containing the native library is garbage collected.
 * This function can be used to perform cleanup operations.
 * Because this function is called in an unknown context (such as from a finalizer),
 * the programmer should be conservative on using Java VM services, and
 * refrain from arbitrary Java call-backs.
 */
void
JNI_OnUnload(JavaVM * const /* vm */, void *) {
    LOGV("native library unloaded");
}

/*------------------------------------- The End */
