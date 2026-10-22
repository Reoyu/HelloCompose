package com.example.ch10.ui.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ch10.data.model.Article
import com.example.ch10.data.model.NewsCategory
import com.example.ch10.data.model.OfflineInfo
import com.example.ch10.ui.components.ArticleCard
import com.example.ch10.util.formatRelativeTime

// Screen murni presentasional: hanya menerima state dan callback.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    uiState: NewsUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (NewsCategory) -> Unit,
    onArticleClick: (Article) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("KabarKini") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            NewsSearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Kategori hanya berlaku untuk headline, disembunyikan saat mencari
            if (!uiState.isSearching) {
                CategoryRow(
                    categories = uiState.categories,
                    selected = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }

            // Tugas 4: banner saat data berasal dari cache Room
            uiState.offline?.let { OfflineBanner(it) }

            // Tugas 3: tarik ke bawah untuk memuat ulang
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    uiState.errorMessage != null -> ErrorState(
                        message = uiState.errorMessage,
                        onRetry = onRetry
                    )

                    uiState.articles.isEmpty() -> CenteredMessage(
                        if (uiState.isSearching) "Tidak ada berita untuk \"${uiState.query.trim()}\"."
                        else "Tidak ada berita ditemukan."
                    )

                    else -> {
                        // Waktu "sekarang" dihitung ulang setiap daftar berubah
                        val now = remember(uiState.articles) { System.currentTimeMillis() }
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.articles, key = { it.url }) { article ->
                                ArticleCard(
                                    article = article,
                                    now = now,
                                    onClick = { onArticleClick(article) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TextField hanya melaporkan teks; debounce dan pemanggilan API ada di ViewModel.
@Composable
private fun NewsSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Cari berita...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Hapus pencarian")
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun CategoryRow(
    categories: List<NewsCategory>,
    selected: NewsCategory,
    onCategorySelected: (NewsCategory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(categories, key = { it.apiValue }) { category ->
            FilterChip(
                selected = category == selected,
                onClick = { onCategorySelected(category) },
                label = { Text(category.label) }
            )
        }
    }
}

@Composable
private fun OfflineBanner(info: OfflineInfo) {
    val now = remember(info) { System.currentTimeMillis() }
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Menampilkan data offline · diperbarui ${formatRelativeTime(now, info.cachedAt)}",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(text = info.reason, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    ScrollableCenter {
        Icon(
            Icons.Filled.WifiOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(message, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}

@Composable
private fun CenteredMessage(text: String) {
    ScrollableCenter {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Pull-to-refresh hanya bereaksi pada konten yang bisa di-scroll, jadi layar
// error/kosong juga dibuat scrollable. heightIn(min = maxHeight) menjaga
// isinya tetap di tengah layar.
@Composable
private fun ScrollableCenter(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .heightIn(min = maxHeight)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}
