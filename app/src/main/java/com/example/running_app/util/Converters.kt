package com.example.running_app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun toBitmap(bytes: ByteArray) = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    @TypeConverter
    fun fromBitMap(btmp: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        btmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}