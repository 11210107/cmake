#include <jni.h>
#include <string>
#include <android/log.h>
#include <fmod.hpp>
#include <pthread.h>

#ifndef NATIVE_LIB_H
#define NATIVE_LIB_H


#ifdef __cplusplus
extern "C" {
#endif

extern "C"
jint registerActionMethod(JNIEnv *env);
#ifdef __cplusplus

}
#endif
#endif // NATIVE_LIB_H