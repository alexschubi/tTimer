package com.example.ttimer

import java.time.LocalDateTime

data class Item(val Index: Int,
                val Text: String,
                val Date: LocalDateTime,
                var Span: String,
                var Notified: Boolean,
                var Deleted: Boolean
                 )

