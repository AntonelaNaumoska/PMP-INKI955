package com.example.pmp_zadaca1

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class MainActivity : AppCompatActivity() {
    private val dictionary = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultTxT = findViewById<TextView>(R.id.result)
        val searchBtn = findViewById<MaterialButton>(R.id.searchBtn)
        val tagTxt = findViewById<TextInputEditText>(R.id.tag)
        val search = findViewById<TextInputEditText>(R.id.search)
        val saveBtn = findViewById<MaterialButton>(R.id.saveBtn)
        val clearBtn = findViewById<MaterialButton>(R.id.clearBtn)

        writeStaticFile()
        getDictionary()

        saveBtn.setOnClickListener {
            val english = search.text.toString().trim()
            val macedonian = tagTxt.text.toString().trim()

            if (english.isNotEmpty() && macedonian.isNotEmpty()) {
                dictionary[english.lowercase()] = macedonian.lowercase()
                save(english, macedonian)
                tagTxt.setText("")
            }
        }
        searchBtn.setOnClickListener {
            val query = search.text.toString().trim().lowercase()
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

            resultTxT.text = result ?: "Not found"
        }
        clearBtn.setOnClickListener {
            dictionary.clear()
            getDictionaryFile().writeText("")
            val tagContainer = findViewById<LinearLayout>(R.id.container)
            tagContainer.removeAllViews()
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

    private fun addTagToLayout(en: String, mk: String) {
        val tagContainer = findViewById<LinearLayout>(R.id.container)

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
        }

        val textView = TextView(this).apply {
            text = "$en - $mk"
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val editBtn = MaterialButton(this).apply {
            text = "Edit"
        }

        row.addView(textView)
        row.addView(editBtn)
        tagContainer.addView(row)
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
                addTagToLayout(en, mk)
            }
        }
        reader.close()
    }

    fun save(en: String, mk: String) {
        val text = "$en, $mk\n"
        openFileOutput("en_mk_recnik.txt", MODE_APPEND).use {
            it.write(text.toByteArray())
        }
        addTagToLayout(en, mk)
    }
}