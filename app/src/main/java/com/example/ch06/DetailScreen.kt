package com.example.ch06

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ch06.data.Article

// Route: mengambil artikel dari cache repository lewat AppContainer
@Composable
fun DetailRoute(itemId: Int, onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as MainApplication
    val article = app.container.articleRepository
        .getCachedArticles()
        .firstOrNull { it.id == itemId }

    DetailScreen(article = article, onBack = onBack)
}

// Screen murni presentasional: hanya menerima data, tidak tahu soal repository
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(article: Article?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text("Detail Artikel") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (article == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Artikel tidak ditemukan")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text  = article.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = article.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Text(
                    text  = article.excerpt.repeat(10),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
