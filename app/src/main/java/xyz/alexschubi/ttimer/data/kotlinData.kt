package xyz.alexschubi.ttimer.data

data class kNote(
    val uid: Int = 0,
    val source: Source? = null,
    var text: String = "",
    val lastEdited: Long = 0,
    val category: Category = Category.purple,
    val tags: List<Tag>? = null,
    val notifications: List<kNotification>? = null
)

data class kNotification(
    val uid: Int = 0,
    var status: NotificationStatus = NotificationStatus.new,
    var timestamp: Long = 0
)

data class kSettings(
    var itemAmount: Int = 0,
    val sortMode: Int = 0,
    val tags: List<Tag> = listOf(),
)