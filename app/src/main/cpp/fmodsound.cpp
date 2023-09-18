#include <jni.h>
#include <string>
#include "fmodsound.h"
#include <fmod.hpp>
#include <android/log.h>
#include <unistd.h>
#include <cstring>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"fmodSound",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"fmodSound",FORMAT,##__VA_ARGS__);
#define TYPE_NORMAL  0
#define TYPE_LOLITA  1
#define TYPE_UNCLE   2
#define TYPE_THRILLER  3
#define TYPE_FUNNY  4
#define TYPE_ETHEREAL  5
#define TYPE_CHORUS  6
#define TYPE_TREMOLO  7

using namespace FMOD;
Channel *channel;
extern "C"
JNIEXPORT void JNICALL
Java_com_wz_cmake_MainActivity_voiceChangeNative
(JNIEnv *env,jobject thiz,jint type, jstring path_jstr) {
    LOGE("%s", "--> start");

    System *system;
    Sound *sound;
    DSP *dsp;
//    Channel *channel = 0;
    float frequency;
    bool isPlaying = true;

    System_Create(&system);
    //初始化
    void *extradriverdata;
    system->init(32, FMOD_INIT_NORMAL, extradriverdata);
    const char *path_cstr = env->GetStringUTFChars(path_jstr, NULL);

    try {
        system->createSound(path_cstr, FMOD_DEFAULT, 0, &sound);
        LOGE("%s", path_cstr)
        LOGE("%d", type)
        switch (type) {
            case TYPE_NORMAL:  // 普通
                system->playSound(sound,0, false, &channel);
                LOGE("%s", "fix normal");
                break;
            case TYPE_LOLITA:  // 萝莉
                LOGE("%s","start play lolita sound")
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);    // 可改变音调
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,8.0f);     // 8.0 为一个八度
                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                break;

            case TYPE_UNCLE:  // 大叔
                LOGE("%s","start play uncle sound")
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                LOGE("%s","createDSPByType")
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,0.8f);
                LOGE("%s","setParameterFloat")
                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                break;

            case TYPE_THRILLER:   // 惊悚
                LOGE("%s","start play thriller sound")
                system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp);       //可改变颤音
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW,5);           // 时间偏移低频振荡周期
                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                break;
            case TYPE_FUNNY:  // 搞怪
                LOGE("%s","start play funny sound")
                system->createDSPByType(FMOD_DSP_TYPE_NORMALIZE, &dsp);    //放大声音
                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                channel->getFrequency(&frequency);
                frequency = frequency * 2;                                  //频率*2
                channel->setFrequency(frequency);
                break;
            case TYPE_ETHEREAL: // 空灵
                LOGE("%s","start play ethereal sound")
                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);          // 控制回声
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY,300);           // 延时
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK,20);         // 回波衰减的延迟

                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                break;
            case TYPE_CHORUS:
                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY,100);
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK,50);
                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                break;
            case TYPE_TREMOLO:
                system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW,0.8);
                system->playSound(sound,0, false, &channel);
                channel->addDSP(0, dsp);
                break;
            }
    } catch (...) {
        LOGE("%s", "catch exception...")
        goto end;
    }

    system->update();

    // 每隔一秒检测是否播放结束
    LOGE("%d",   isPlaying)
    while (isPlaying) {
        channel->isPlaying(&isPlaying);
        LOGE("%d", isPlaying)
        usleep(1000 * 1000);
    }

    goto end;

    //释放资源
    end:
    env->ReleaseStringUTFChars(path_jstr, path_cstr);
    sound->release();
    system->close();
    system->release();

    jclass mainCls = env->GetObjectClass(thiz);
    jmethodID endMethod = env->GetMethodID(mainCls, "playerEnd", "(Ljava/lang/String;)V");
    jstring value = env->NewStringUTF("播放完毕");
    env->CallVoidMethod(thiz,endMethod,value);
}


