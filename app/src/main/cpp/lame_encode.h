#include <jni.h>
/**
* Header for class com_wz_cmake_audio_Mp3Encoder
*/
#ifndef INCLUDE_LAME_ENCODE
#define INCLUDE_LAME_ENCODE
#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL encodeMp3(JNIEnv *env, jobject,jstring,jstring,jint,jint,jint);
JNIEXPORT void JNICALL destroy(JNIEnv *env, jobject);
extern "C"
jint registerNativeMethod(JNIEnv *env);
#ifdef __cplusplus

}
#endif
#endif