package com.emergent.photosharingapp.Utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.*

import java.nio.ByteBuffer
import java.nio.file.Files

object FileUtils {
    private val BUFFER_SIZE = 8192

    fun copy(context: Context, uri: Uri): File {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        var fileName = "newFile"
        if(returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            fileName = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        val targetFile = File(context.filesDir.absolutePath + File.separator + fileName)
        val fileOutputStream = FileOutputStream(targetFile)
        val fileInputStream = context.contentResolver.openInputStream(uri)
        copy(fileInputStream, fileOutputStream)

        fileOutputStream.close()
        fileInputStream.close()
        return targetFile
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, sink: OutputStream): Long {
        var nread = 0L
        val buf = ByteArray(BUFFER_SIZE)
        var n: Int = source.read(buf)
        while (n > 0) {
            sink.write(buf, 0, n)
            nread += n.toLong()
            n = source.read(buf)
        }
        return nread
    }
}
