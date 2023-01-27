package xyz.alexschubi.ttimer.data

import xyz.alexschubi.ttimer.mcontext
import java.nio.file.Files
import java.nio.file.Paths

class json() {
    lateinit var dataFolder: String
    init {
        val path = "/data/data/xyz.alexschubi.ttimer/files/data"
        val getPath = mcontext.getFilesDir().getPath() + "/data"
        Files.createDirectory(Paths.get(getPath))
    }

    fun saveToJson(){}
   // fun getFromJson(): List<kNote> {  }
}