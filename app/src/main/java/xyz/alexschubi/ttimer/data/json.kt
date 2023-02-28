package xyz.alexschubi.ttimer.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xyz.alexschubi.ttimer.mcontext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class jsonItems() {
    private var dataPath: String = mcontext.filesDir.path + "/data/" //TODO dont use mcontext
    private val gson = Gson()
    init {
        Files.createDirectories(Paths.get(dataPath))
    }

    fun saveToJson(item: kNote){
        val jsonString: String = gson.toJson(item)
        val file = File(dataPath + item.uid + ".json")
        Log.d("Data", "write Item to File $file")
        file.writeText(jsonString)
    }
    fun getFromJsonPerId(uid: Int): kNote {
        val jsonstring = File("$dataPath$uid.json").readText()
        val kNoteType = object : TypeToken<kNote>() {}.type
        return gson.fromJson(jsonstring, kNoteType)
    }
    fun getFromJsonPerPath(path: String): kNote {
        val jsonstring = File(path).readText()
        val kNoteType = object : TypeToken<kNote>() {}.type
        return gson.fromJson(jsonstring, kNoteType)
    }
    fun getAllFromJson(): List<kNote> {
        val returnList = mutableListOf<kNote>()
        Log.d("Data", "data-path=$dataPath")
        File(dataPath).walk().filter { it -> it.isFile }.forEach {
            Log.d("Data", "data Path of item="+ it.path)
            returnList.add(getFromJsonPerPath(it.toString()))

        }
        return returnList.toList() //TODO use MutableList for life-data
    }
}

class jsonSettings() {
    private var dataPath: String = mcontext.filesDir.path + "/settings.json" //TODO dont use mcontext
    private val gson = Gson()
    init {
        if (!File(dataPath).exists()){
            saveToJson(kSettings())
        }
    }
    fun saveToJson(settings: kSettings){
        val jsonString: String = gson.toJson(settings)
        val file = File(dataPath)
        Log.d("Data", "write Item to File $file")
        file.writeText(jsonString)
    }
    fun getFromJson(): kSettings {
        Log.d("Data", "data-path=$dataPath")
        val jsonstring = File(dataPath).readText()
        val kSettingsType = object : TypeToken<kSettings>() {}.type
        return gson.fromJson(jsonstring, kSettingsType)
    }
    fun registerNewItem(){
        Log.d("Data", "data-path=$dataPath")
        val jsonstring = File(dataPath).readText()
        val kSettingsType = object : TypeToken<kSettings>() {}.type
        var settings: kSettings = gson.fromJson(jsonstring, kSettingsType)
        settings.itemAmount = settings.itemAmount + 1
        val jsonString: String = gson.toJson(settings)
        File(dataPath).writeText(jsonString)
    }
}