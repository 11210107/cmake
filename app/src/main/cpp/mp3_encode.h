#include <stdio.h>
#include "lame-3.100_libmp3lame/lame.h"
class Mp3Encoder {

private:
    FILE *pcmFile;
    FILE *mp3File;

    lame_t lameClient;
    bool singleChannel;

public:

    Mp3Encoder();

    ~Mp3Encoder();

    int Init(const char *pcmFile, const char *mp3File,int sampleRate,int channels,int bitRate);

    void Encode();
    void encodeDoubleChannelPcm();
    void encodeSingleChannelPcm();

    void Release();

};