package xyz.alexschubi.ttimer.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class test() {
    lateinit var testdata: List<kNote>
    init {
        testToJson(testData())
        testFromJson()
        json()
    }
    //TEST
    fun testData(): List<kNote>{
        return listOf(
            kNote(1, null, "test1\n qweqwe", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(2, null, "test2\n qawqweqw", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(3, null, "test3\n qweqwe\n qweqweqwe", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(4, null, "test4", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(5, null, "test5", 1673894238666, Category.blue, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(6, null, "test6", 1673894238666, Category.blue, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(7, null, "test7", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(8, null, "test8", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(9, null, "test3", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(10, null, "test4", 1673894238666, Category.yellow, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(11, null, "test5", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(12, null, "test6", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(13, null, "test1\n qweqwe", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(14, null, "test2\n qawqweqw", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(15, null, "test3\n qweqwe\n qweqweqwe", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(16, null, "test4", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(17, null, "test5", 1673894238666, Category.blue, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(18, null, "test6", 1673894238666, Category.blue, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(19, null, "test7", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(20, null, "test8", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(21, null, "test3", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(22, null, "test4", 1673894238666, Category.yellow, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            kNote(23, null, "test5", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
            )
    }


    fun testToJson(data: List<kNote>){

        val gson = Gson()
        val string: String = gson.toJson(data)
        val file = File("data/data/xyz.alexschubi.ttimer/files/test.json")
        file.createNewFile()
        file.writeText(string)

        val gsonpretty = GsonBuilder().setPrettyPrinting().create()
        val stringpretty: String = gsonpretty.toJson(data)
        val filepretty = File("data/data/xyz.alexschubi.ttimer/files/testpretty.json")
        filepretty.createNewFile()
        filepretty.writeText(stringpretty)
    }
    fun testFromJson(){
        val file = File("data/data/xyz.alexschubi.ttimer/files/test.json")
        val jsonstring = file.readText()
        val gson = Gson()
        val listTutorialType = object : TypeToken<List<kNote>>() {}.type
        val returnList: List<kNote> = gson.fromJson(jsonstring, listTutorialType)
        testdata = returnList
    }
}