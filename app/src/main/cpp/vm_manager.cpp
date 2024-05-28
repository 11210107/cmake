#include "vm_manager.h"
#include <android/log.h>
#include "lame_encode.h"
#include "native-lib.h"
#define LOG_TAG "Lame"
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,FORMAT,##__VA_ARGS__);
#define LOGD(FORMAT, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, FORMAT, ##args);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,FORMAT,##__VA_ARGS__);

extern jint registerNativeMethod(JNIEnv* env);
extern jint registerActionMethod(JNIEnv* env);

/**
* 加载默认回调
 * @param vm
 * @param reserved
 * @return
*/
JavaVM* vm;
extern "C"
jint JNI_OnLoad(JavaVM *vm,void *reserved){
    ::vm = vm;//赋值全局变量
    LOGE("%s","JNI_OnLoad start");
    JNIEnv *env = NULL;
    if(vm->GetEnv((void **) &env,JNI_VERSION_1_6) != JNI_OK){
        LOGE("%s","JNI_OnLoad GetEnv error");
        return -1;
    }
    //注册方法
    if(registerNativeMethod(env) != JNI_OK){
        LOGE("%s","JNI_OnLoad registerNativeMethod error");
        return -1;
    }
    if(registerActionMethod(env) != JNI_OK){
        LOGE("%s","JNI_OnLoad registerActionMethod error");
        return -1;
    }
    return JNI_VERSION_1_6;
}

jint InitVM(JavaVM* vm) {
//    ::vm = vm;
    return JNI_VERSION_1_6;
}