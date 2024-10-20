package br.com.b256.core.ndk

import android.graphics.Bitmap

class NativeLib {

    /**
     * A native method that is implemented by the 'ndk' native library,
     * which is packaged with this application.
     */
    external fun grayscale(bitmap: Bitmap)

    companion object {
        // Used to load the 'ndk' library on application startup.
        init {
            System.loadLibrary("ndk")
        }
    }
}
