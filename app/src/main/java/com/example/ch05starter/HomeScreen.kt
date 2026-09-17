package com.example.ch05starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Data dummy — nantinya bisa diganti sumber data asli (API, database)
data class Article(val id: Int, val title: String, val excerpt: String)

val dummyArticles = (1..10).map { i ->
    Article(
        id      = i,
        title   = "Artikel ke-$i: Belajar Compose",
        excerpt = "Ini adalah ringkasan artikel ke-$i yang membahas " +
                  "topik menarik seputar Android development modern."
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Beranda") }) }
    ) { padding ->
        LazyColumn(
            modifier             = Modifier.padding(padding),
            contentPadding       = PaddingValues(16.dp),
            verticalArrangement  = Arrangement.spacedBy(8.dp)
        ) {
            items(dummyArticles, key = { it.id }) { article ->
                ArticleCard(
                    article = article,
                    onClick = {
                        navController.navigate(
                            Routes.Detail.createRoute(article.id)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article, onClick: () -> Unit) {
    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = article.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = article.excerpt,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
