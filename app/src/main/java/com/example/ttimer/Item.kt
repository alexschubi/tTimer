package com.example.ttimer

import java.sql.Date
import java.sql.Time

data class Item( val index: Int,
                 //Text
                 val Text: String,
                 //Date
                 val Day: Int,
                 val Month: Int,
                 val Year: Int,
                 //Time
                 val Hour: Int,
                 val Minute: Int
                 )
data class ItemTest( val Text: String, val  Date: String, val Time: String)
