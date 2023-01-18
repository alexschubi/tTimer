package xyz.alexschubi.ttimer.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.alexschubi.ttimer.data.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen() {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(testData()){ index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.secondaryContainer)
            ){
                //row with Text
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item.text)
                }
                //row with Notifications
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 0.dp, 2.dp, 2.dp)) {
                    //add TAGS
                    item.notifications?.forEach { notification ->
                        AssistChip(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(2.dp).height(18.dp),
                            label = { Text(text = notification.timestamp.toString(), fontSize = 12.sp)},
                            onClick = { /*TODO*/ }
                        )
                    }
                }
                //row with Tags
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp, 0.dp, 2.dp, 2.dp)) {
                    //add TAGS
                    item.tags?.forEach { tag ->
                        AssistChip(
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(2.dp).height(18.dp),
                            label = { Text(text = tag.toString(), fontSize = 12.sp)},
                            onClick = { /*TODO*/ }
                        )
                    }
                    Text(text = item.uid.toString())
                }
            }
        }
    }
}


//TEST
fun testData(): List<kNote>{
    return listOf(
        kNote(1, null, "test1", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(2, null, "test2", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(3, null, "test3", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(4, null, "test4", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(5, null, "test5", 1673894238666, Category.blue, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(6, null, "test6", 1673894238666, Category.blue, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(7, null, "test7", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(8, null, "test8", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(9, null, "test3", 1673894238666, Category.green, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(10, null, "test4", 1673894238666, Category.yellow, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(11, null, "test5", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),
        kNote(12, null, "test6", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), listOf( kNotification(1, NotificationStatus.new, 2673894238666), kNotification(2, NotificationStatus.pending, 2673894238666))),

        )
}