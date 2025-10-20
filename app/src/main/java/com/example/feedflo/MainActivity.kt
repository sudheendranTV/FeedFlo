package com.example.feedflo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.feedflo.ui.theme.FeedFloTheme
import com.example.feedflow.remote.response.Article
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedFloTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PaginatedNewsScreen(paddingValues = innerPadding)
                }
            }
        }
    }
}

@Composable
fun NewsScreen(paddingValues: PaddingValues, viewModel: HomeViewmodel = hiltViewModel()) {
    val newsResponse = viewModel._newsResponse

    // Trigger the API call once when screen loads
    LaunchedEffect(Unit) {
        viewModel.getNews()
    }

    when {
        newsResponse == null -> {
            // Show loading indicator
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        newsResponse.value?.articles.isNullOrEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No articles found")
            }
        }

        else -> {
            val articles = newsResponse.value?.articles.orEmpty()      // List<Article>

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {

                items(articles) { article ->
                    NewsItem(article)
                }
            }
        }
    }
}

@Composable
fun PaginatedNewsScreen(paddingValues: PaddingValues, viewModel: HomeViewmodel = hiltViewModel()) {
    val articles = viewModel.articles.collectAsLazyPagingItems()

    LazyColumn {
        items(articles.itemCount) { index ->
            articles[index]?.let { article ->
                NewsItem(article)
            }
        }

        // Loading more indicator
        articles.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingItem("Loading users...") }
                }

                loadState.append is LoadState.Loading -> {
                    item { LoadingItem("Loading more...") }
                }

                loadState.append is LoadState.Error -> {
                    item {
                        RetryItem {
                            retry() // Retry if load failed
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun LoadingItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Composable
fun RetryItem(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun NewsItem(article: Article) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {

        ImageWithShimmer(imageUrl = article.urlToImage ?: "")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = article.title ?: "No Title",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            text = article.description ?: "No Description",
            modifier = Modifier
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Divider(modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ImageWithNormalLoader(urlToImage: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(urlToImage)
            .crossfade(true)
            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
            .error(android.R.drawable.ic_dialog_alert)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(5.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ImageWithShimmer(imageUrl: String) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(5.dp))
            .height(200.dp),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        },
        error = {
            Text("⚠️ Error loading image")
        },
        success = {
            SubcomposeAsyncImageContent()
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FeedFloTheme {
        Greeting("Android")
    }
}