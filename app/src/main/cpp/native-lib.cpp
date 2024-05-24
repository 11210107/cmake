#include <jni.h>
#include <string>
#include <android/log.h>
#include <fmod.hpp>
using namespace FMOD;

static JNINativeMethod methods[] = {
//        {"doAction","(Ljava/lang/String;)I",(jint *)(doAction)}
};
/*JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    // 获取JNI env变量
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        // 失败返回-1
        return result;
    }

    // 获取native方法所在类
    const char* className = "com/wz/cmake/MainActivity";
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return result;
    }

    // 动态注册native方法
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0) {
        return result;
    }

    // 返回成功
    result = JNI_VERSION_1_6;
    return result;
}*/

extern "C" JNIEXPORT jstring JNICALL
Java_com_wz_cmake_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    System *system;
    System_Create(&system);
    unsigned int version;
    system->getVersion(&version);
    __android_log_print(ANDROID_LOG_ERROR, "CPP TEST","FMOD version：%08x",version);

    std::string hello = "Hello from C Plus Plus";
    return env->NewStringUTF(hello.c_str());
}
