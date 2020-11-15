package com.sergo_smyk.test_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sergo_smyk.downloader.SimpleDownloader
import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.DownloadRequest
import com.sergo_smyk.downloader.api.Downloader
import com.sergo_smyk.downloader.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val testLink ="http://speedtest.tele2.net/10MB.zip"

    private lateinit var textView: TextView

    private lateinit var button: Button

    private lateinit var downloader: Downloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloader = SimpleDownloader(application)

        initViews()
        setupLogic()
    }

    private fun initViews() {
        textView = findViewById(R.id.info)
        button = findViewById(R.id.button)
    }

    private fun setupLogic() {
        lifecycleScope.launch(Dispatchers.IO) {
            downloader.observe("test").collect {
                showInfo(it)
            }
        }

        button.setOnClickListener {
            downloader.download(getRequest())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo(info: DownloadItem) {
        lifecycleScope.launch(Dispatchers.Main) {
            textView.text = """
                Id: ${info.id}
                Title: ${info.title}
                TotalSize: ${info.totalSize}
                Progress: ${(info.progress * 100).toInt()}
                Status: ${info.status}
                Reason: ${info.reason}
            """.trimIndent()
        }
    }

    private fun getRequest(): DownloadRequest {
        return DownloadRequest(
            appId = "test",
            title = "10MB",
            fileName = "10MB.zip",
            link = testLink,
            savePath = "test_dir"
        )
    }
}