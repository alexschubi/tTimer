package xyz.alexschubi.ttimer.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.alexschubi.ttimer.data.Category
import xyz.alexschubi.ttimer.data.Tag
import xyz.alexschubi.ttimer.data.kNote

@Composable
fun ItemsScreen() {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(testData()){ index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor =  MaterialTheme.colorScheme.surfaceVariant)
            ){
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = item.uid.toString())
                    Column {
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            text = item.category.toString()
                        )
                    }
                    Column{
                        Row() {
                            item.tags?.forEach { tag ->
                                Button(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    modifier = Modifier
                                        .height(20.dp)
                                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                                    contentPadding = PaddingValues(2.dp, 0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    onClick = { /*TODO*/ }
                                ) {
                                    Text(
                                        text = tag.toString(),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item.text)
                }

            }

        }
    }
}


//TEST
fun testData(): List<kNote>{
    return listOf(
        kNote(1, null, "test1", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(2, null, "test2", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(3, null, "test3", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(4, null, "test4", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(5, null, "test5", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(6, null, "test6", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(7, null, "test7", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null),
        kNote(8, null, "test8", 1673894238666, Category.purple, listOf(Tag.alpha, Tag.beta), null)
    )
}