//#include <jni.h>
//#include <string>
//#include <android/log.h>
//#include <fmod.hpp>
//#include <pthread.h>
#include "native-lib.h"
using namespace FMOD;
#define LOG_TAG "cmake"
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,FORMAT,##__VA_ARGS__);

JNIEXPORT jint JNICALL
doAction(JNIEnv *env, jobject thiz,jstring name) {
    jsize size = env->GetStringUTFLength( name);
    LOGI("name  length: %d",size);
    const char *str;
    str = env->GetStringUTFChars(name, NULL);
    LOGI("name  str: %s",str);
    LOGI("doAction name：%s",str);
    jstring result = env->NewStringUTF(str);
    return 0;
}
static JNINativeMethod methods[] = {
//        {"doAction","(Ljava/lang/String;)I",(jint *)(doAction)}
        {"doAction","(Ljava/lang/String;)I",(jint *)(doAction)}
};

extern "C"
jint registerActionMethod(JNIEnv *env){
    jclass cl = env->FindClass("com/wz/cmake/MainActivity");
    if ((env->RegisterNatives(cl,methods,sizeof(methods) / sizeof(methods[0]))) < 0){
        return -1;
    }
    return 0;
}
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
//定义辅助类，用于接受一个jobject对象
class MSContext{
public:
    jobject instance = nullptr;
};
//声明全局变量，用于close方法，对全局变量的释放
MSContext *msContext;
extern JavaVM* vm;
//启动一个子线程，需要回调的方法
void *cpp_thread_fun(void * args) {
    LOGI("C++ PThread的异步线程");
    MSContext *msContext = static_cast<MSContext *>(args);
    JNIEnv * env;
    jint r = ::vm->AttachCurrentThread((&env), nullptr);
    if (r!= JNI_OK) {
        LOGI("AttachCurrentThread() failed");
        return nullptr;
    }

    jclass activityClazz = env->GetObjectClass(msContext->instance);
    jmethodID update = env->GetMethodID(activityClazz,"runThread","()V");
    env->CallVoidMethod(msContext->instance,update);
    //释放JNIEnv
    ::vm->DetachCurrentThread();
    return nullptr;
}

extern "C" JNIEXPORT void JNICALL
Java_com_wz_cmake_MainActivity_nativeThread(JNIEnv* env,jobject thiz){
    msContext = new MSContext();
    msContext->instance = env->NewGlobalRef(thiz); //把局部变量提升为全局变量

    pthread_t pid;
    //创建一个线程
    pthread_create(&pid, nullptr,cpp_thread_fun,msContext);
}

