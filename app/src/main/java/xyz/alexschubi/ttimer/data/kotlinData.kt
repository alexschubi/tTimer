package xyz.alexschubi.ttimer.data

data class kNote(
    val uid: Int,
    val source: Source?,
    val text: String = "",
    val lastEdited: Long,
    val category: Category,
    val tags: List<Tag>?,
    val notifications: List<kNotification>?
) {
    fun toJson(): String {
        var finalstring = "bblblbllblb"
        return finalstring
    }
}

data class kNotification(
    val uid: Int = 0,
    val status: NotificationStatus,
    val timestamp: Long
)
