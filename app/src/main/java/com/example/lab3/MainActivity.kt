package com.example.lab3

import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab3.ui.theme.LAB3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LAB3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JokeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLanguageChange = { tag ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val localeManager = getSystemService(LocaleManager::class.java)
                                localeManager?.applicationLocales = if (tag.isEmpty()) {
                                    LocaleList.getEmptyLocaleList()
                                } else {
                                    LocaleList.forLanguageTags(tag)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun JokeScreen(
    modifier: Modifier = Modifier,
    onLanguageChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val jokes = runCatching {
        context.resources.getStringArray(R.array.jokes)
    }.getOrDefault(emptyArray())

    var currentJokeIndex by rememberSaveable { mutableIntStateOf(-1) }
    var jokesSeenCount by rememberSaveable { mutableIntStateOf(0) }

    val currentLangTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val localeManager = context.getSystemService(LocaleManager::class.java)
        val locales = localeManager?.applicationLocales
        if (locales != null && !locales.isEmpty) locales[0].language else ""
    } else ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = currentLangTag.isEmpty(),
                onClick = { onLanguageChange("") },
                label = { Text(stringResource(R.string.lang_system), textAlign = TextAlign.Center) }
            )
            FilterChip(
                selected = currentLangTag == "pl",
                onClick = { onLanguageChange("pl") },
                label = { Text(stringResource(R.string.lang_pl), textAlign = TextAlign.Center) }
            )
            FilterChip(
                selected = currentLangTag == "en",
                onClick = { onLanguageChange("en") },
                label = { Text(stringResource(R.string.lang_en), textAlign = TextAlign.Center) }
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            val displayedJoke = if (currentJokeIndex in jokes.indices) {
                jokes[currentJokeIndex]
            } else {
                stringResource(R.string.initial_prompt)
            }

            Text(
                text = displayedJoke,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (jokes.isNotEmpty()) {
                    currentJokeIndex = jokes.indices.random()
                    jokesSeenCount++
                }
            },
            shape = CircleShape,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.defaultMinSize(minWidth = 140.dp, minHeight = 140.dp)
        ) {
            Text(
                text = stringResource(R.string.show_joke_btn),
                textAlign = TextAlign.Center
            )
        }

        if (jokesSeenCount > 0) {
            Text(
                text = pluralStringResource(R.plurals.jokes_seen, jokesSeenCount, jokesSeenCount),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(
                    R.string.greeting,
                    stringResource(R.string.guest_name),
                    jokesSeenCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JokePreview() {
    LAB3Theme {
        JokeScreen()
    }
}