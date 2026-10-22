package com.example.ch10.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

// Gambar artikel dengan Coil. Tanpa URL -> placeholder ikon koran.
// Selama memuat / gagal memuat -> kotak berwarna polos.
@Composable
fun ArticleImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant

    if (imageUrl == null) {
        Box(
            modifier = modifier.background(placeholderColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Newspaper,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            placeholder = ColorPainter(placeholderColor),
            error = ColorPainter(placeholderColor),
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
