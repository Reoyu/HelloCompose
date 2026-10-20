package com.example.ch09.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ch09.data.model.Article
import com.example.ch09.util.formatRelativeTime
import com.example.ch09.util.parseIsoInstant

@Composable
fun ArticleCard(
    article: Article,
    now: Long,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            ArticleImage(
                imageUrl = article.imageUrl,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = articleMeta(article, now),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (article.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = article.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// "BBC News · 3 jam lalu" (waktu dihilangkan jika tanggal tidak terbaca)
fun articleMeta(article: Article, now: Long): String {
    val published = parseIsoInstant(article.publishedAt) ?: return article.sourceName
    return "${article.sourceName} · ${formatRelativeTime(now, published)}"
}
