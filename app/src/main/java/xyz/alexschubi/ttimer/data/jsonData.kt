package xyz.alexschubi.ttimer.data

data class jNote(
    val uid: Long,
    val source: String,
    val text: String,
    val lastEdited: Long,
    val category: String,
    val tags: List<String>,
    val notifications: List<jNotification>,
) {
    fun toKotlin(): kNote {
        return kNote(0, null, "test1", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null)
    }
}

data class jNotification(
    val nid: Long,
    val status: String,
    val timestamp: Long,
)