#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#define LOG_TAG "NDK_Camera"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_br_com_b256_core_ndk_NativeLib_grayscale(JNIEnv *env, jobject thiz, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        LOGD("Failed to get bitmap info");
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGD("Unsupported format");
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        LOGD("Failed to lock pixels");
        return;
    }

    // Process the image to grayscale
    uint32_t *line = (uint32_t *) pixels;
    for (int y = 0; y < info.height; y++) {
        for (int x = 0; x < info.width; x++) {
            uint32_t pixel = line[x];

            uint8_t r = (pixel >> 16) & 0xFF;
            uint8_t g = (pixel >> 8) & 0xFF;
            uint8_t b = pixel & 0xFF;

            // Grayscale conversion
            uint8_t gray = (r + g + b) / 3;
            line[x] = (0xFF << 24) | (gray << 16) | (gray << 8) | gray;
        }
        line = (uint32_t *) ((char *) line + info.stride);
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
