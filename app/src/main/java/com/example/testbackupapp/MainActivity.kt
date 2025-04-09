package com.example.testbackupapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import com.example.testbackupapp.ui.theme.TestBackupAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileReader

class MainActivity : ComponentActivity() {

    private val backupFileContents = MutableLiveData<String>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backupFileContents = backupFileContents.observeAsState().value
            TestBackupAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = backupFileContents ?: "[empty]",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        scope.launch {
            val file = File(filesDir, "someFile.txt")
            if (!file.exists()) {
                return@launch
            }
            FileReader(file).use {
                val content = it.readText()
                backupFileContents.postValue(content)
            }
        }
        super.onResume()
    }
}