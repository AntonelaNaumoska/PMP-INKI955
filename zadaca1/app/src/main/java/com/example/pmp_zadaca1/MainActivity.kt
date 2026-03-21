package com.example.pmp_zadaca1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class MainActivity : ComponentActivity() {

    private val dictionary = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        writeStaticFile()
        getDictionary()

        setContent {
            AppUI()
        }
    }

    @Composable
    fun AppUI() {
        var searchText by remember { mutableStateOf("") }
        var tagText by remember { mutableStateOf("") }
        var resultText by remember { mutableStateOf("") }

        val tagsList = remember { mutableStateListOf<Pair<String, String>>() }

        LaunchedEffect(Unit) {
            tagsList.clear()
            dictionary.forEach { (en, mk) ->
                tagsList.add(en to mk)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Enter Twitter search query") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val query = searchText.trim().lowercase()
                    var result: String? = null

                    if (dictionary.containsKey(query)) {
                        result = dictionary[query]
                    } else {
                        for ((en, mk) in dictionary) {
                            if (mk.lowercase() == query) {
                                result = en
                                break
                            }
                        }
                    }

                    resultText = result ?: "Not found"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Search")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                OutlinedTextField(
                    value = tagText,
                    onValueChange = { tagText = it },
                    label = { Text("Tag Query") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val english = searchText.trim()
                        val macedonian = tagText.trim()

                        if (english.isNotEmpty() && macedonian.isNotEmpty()) {
                            dictionary[english.lowercase()] = macedonian.lowercase()
                            save(english, macedonian)

                            tagsList.add(english to macedonian)
                            tagText = ""
                        }
                    }
                ) {
                    Text("Save")
                }
            }

            Text(
                text = resultText,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Tagged Searches",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                tagsList.forEach { (en, mk) ->
                    TagRow(en, mk)
                }
            }

            Button(
                onClick = {
                    dictionary.clear()
                    getDictionaryFile().writeText("")
                    tagsList.clear()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Clear Tags")
            }
        }
    }

    @Composable
    fun TagRow(en: String, mk: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = "$en - $mk",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )

            Button(onClick = {  }) {
                Text("Edit")
            }
        }
    }

    private fun getDictionaryFile(): File {
        return File(filesDir, "en_mk_recnik.txt")
    }

    private fun writeStaticFile() {
        val file = getDictionaryFile()
        if (!file.exists()) {
            assets.open("en_mk_recnik.txt").use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun getDictionary() {
        val file = getDictionaryFile()
        if (!file.exists()) return
        val reader = file.bufferedReader()
        reader.forEachLine {
            val parts = it.split(",")
            if (parts.size == 2) {
                val en = parts[0].trim().lowercase()
                val mk = parts[1].trim().lowercase()
                dictionary[en] = mk
            }
        }
        reader.close()
    }

    fun save(en: String, mk: String) {
        val text = "$en, $mk\n"
        openFileOutput("en_mk_recnik.txt", MODE_APPEND).use {
            it.write(text.toByteArray())
        }
    }
}