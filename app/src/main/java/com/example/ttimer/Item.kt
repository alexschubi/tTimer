package com.example.ttimer

import java.sql.Date
import java.sql.Time
import java.time.LocalDateTime

data class Item(val Index: Int,
                val Text: String,
                val Date: LocalDateTime,
                var Span: String
                 )
data class ItemTest( val Text: String,
                     val  Date: String,
                     val Time: String
                     )

