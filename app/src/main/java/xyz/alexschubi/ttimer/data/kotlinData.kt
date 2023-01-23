package xyz.alexschubi.ttimer.data

data class kNote(
    val uid: Int = 0,
    val source: Source? = null,
    var text: String = "",
    val lastEdited: Long = 0,
    val category: Category = Category.purple,
    val tags: List<Tag>? = null,
    val notifications: List<kNotification>? = null
) {
    fun toJson(): String {
        val finalstring = "bblblbllblb"
        return finalstring
    }
}

data class kNotification(
    val uid: Int = 0,
    val status: NotificationStatus,
    val timestamp: Long
)
