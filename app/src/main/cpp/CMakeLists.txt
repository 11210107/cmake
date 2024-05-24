# 指定cmake最低支持版本
cmake_minimum_required(VERSION 3.22.1)

#设置头文件目录
include_directories(${CMAKE_SOURCE_DIR}/inc)
#查找当前目录所有源文件保存到DIR_SRCS中
#aux_source_directory(. DIR_SRCS)
file(GLOB allCPP *.c *.h *.cpp)
function(func a b c)
        message("call function func")
        message("a:${a}")
        message("b:${b}")
        message("c:${c}")
        message("ARGC:${ARGC}")
        message("ARGV:${ARGV}")
endfunction(func)

func(1 3 5)

project("cmake")

set(LAME_LIBMP3_DIR ${CMAKE_SOURCE_DIR}/lame-3.100_libmp3lame)
# 导入lame头文件
include_directories(${CMAKE_SOURCE_DIR}/lame-3.100_libmp3lame)
# 添加一个lame的库（动态库SHARED，静态库STATIC）
add_library(
        mp3lame
        SHARED
        ${LAME_LIBMP3_DIR}/bitstream.c
        ${LAME_LIBMP3_DIR}/fft.c
        ${LAME_LIBMP3_DIR}/id3tag.c
        ${LAME_LIBMP3_DIR}/mpglib_interface.c
        ${LAME_LIBMP3_DIR}/presets.c
        ${LAME_LIBMP3_DIR}/quantize.c
        ${LAME_LIBMP3_DIR}/reservoir.c
        ${LAME_LIBMP3_DIR}/tables.c
        ${LAME_LIBMP3_DIR}/util.c
        ${LAME_LIBMP3_DIR}/VbrTag.c
        ${LAME_LIBMP3_DIR}/encoder.c
        ${LAME_LIBMP3_DIR}/gain_analysis.c
        ${LAME_LIBMP3_DIR}/lame.c
        ${LAME_LIBMP3_DIR}/newmdct.c
        ${LAME_LIBMP3_DIR}/psymodel.c
        ${LAME_LIBMP3_DIR}/quantize_pvt.c
        ${LAME_LIBMP3_DIR}/set_get.c
        ${LAME_LIBMP3_DIR}/takehiro.c
        ${LAME_LIBMP3_DIR}/vbrquantize.c
        ${LAME_LIBMP3_DIR}/version.c
        ${LAME_LIBMP3_DIR}/Mp3Encoder.c
)

# 添加一个库，根据native-lib.cpp源文件编译一个native-lib的动态库
add_library( 
        cmake
        SHARED
        ${allCPP})
# 设置第三方so库的路径，导入库文件（方式一）
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}")
#Android6.0之前配置添加预编译库
#[[set(FMOD_LIB_DIR ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
add_library(fmod
        SHARED
        IMPORTED)
set_target_properties(fmod
        PROPERTIES IMPORTED_LOCATION
        ${FMOD_LIB_DIR}/libfmod.so)
add_library(fmodL
        SHARED
        IMPORTED)
set_target_properties(fmodL
        PROPERTIES IMPORTED_LOCATION
        ${FMOD_LIB_DIR}/libfmodL.so)]]
# 查找系统库，这里找的是系统日志库，并赋值给变量log-lib
#[[find_library(
        log-lib
        log)]]
# 链接到总库中
target_link_libraries( 
        cmake #被链接的总库
        fmod
        fmodL
        mp3lame # 具体的库 链接到libcmake.so里面去
        log #自动寻找
        )