package com.sergo_smyk.test_app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sergo_smyk.downloader.SimpleDownloader
import com.sergo_smyk.downloader.api.DownloadItem
import com.sergo_smyk.downloader.api.DownloadRequest
import com.sergo_smyk.downloader.api.Downloader
import com.sergo_smyk.downloader.app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val testId = "test_download_id"

    private val testLink = "http://speedtest.tele2.net/10MB.zip"

    private lateinit var binding: ActivityMainBinding

    private lateinit var downloader: Downloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
        downloader = SimpleDownloader(application, MyService::class.java)
        setupLogic()
    }

    private fun setupLogic() {
        lifecycleScope.launch(Dispatchers.IO) {
            downloader.observe(testId).collect {
                showInfo(it)
            }
        }

        binding.startButton.setOnClickListener {
            downloader.download(getRequest())
        }

        binding.removeButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                downloader.removeItemFromDownloader(testId)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo(info: DownloadItem) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.info.text = """
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
            appId = testId,
            title = "10MB",
            fileName = "10MB.zip",
            link = testLink,
            savePath = "test_dir"
        )
    }
}