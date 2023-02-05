package com.k_bootcamp.furry_friends.util.etc

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun bitmapToFile(bitmap: Bitmap, path: String): File{
    val file = File(path)
    if(!file.exists()) file.mkdirs()

    val fileCacheStream = File(path + System.currentTimeMillis() + ".jpg")
    var out: OutputStream? = null
    try{
        fileCacheStream .createNewFile()
        out = FileOutputStream(fileCacheStream)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
    }finally{
        out?.close()
    }
    return file
}