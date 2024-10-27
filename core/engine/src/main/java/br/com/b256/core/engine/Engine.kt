package br.com.b256.core.engine

import android.graphics.Bitmap

class Engine {
    //TODO: olhar este repositorio https://github.com/cubukcum/lane-detection-kotlin-opencv/blob/main/app/build.gradle

    /**
     * A native method that is implemented by the 'engine' native library,
     * which is packaged with this application.
     */
    external fun grayscale(bitmap: Bitmap)

    companion object {
        // Used to load the 'engine' library on application startup.
        init {
            System.loadLibrary("engine")
        }
    }
}
