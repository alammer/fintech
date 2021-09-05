package ru.fintech.devlife.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.R
import java.lang.IllegalArgumentException

@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

    private lateinit var tabView: TabLayout
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var posterView: ImageView

    private val viewModel: MainViewModel by viewModels()

    private var loadingPoster = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        viewModel.poster.observe(this, {
            //Log.i("fetchMain", "observe get: ${it?.get(0)?.posterUrl}")
            Glide.with(this)
                .load(it.posterUrl)
                .transform(FitCenter(), RoundedCorners(24))
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.broken_image)
                .into(posterView)
            viewModel.complete()
        })

        viewModel.uiStatus.observe(this, { status ->
            //Log.i("fetchMain", "observe get: ${it?.get(0)?.posterUrl}")
            when(status) {
                UiStatus.LOADING -> loadingPoster = true
                UiStatus.OK -> loadingPoster = false
                UiStatus.START -> {
                    loadingPoster = false
                    btnPrevious.isEnabled = false
                }
                UiStatus.END -> {
                    loadingPoster = false
                    btnNext.isEnabled = false
                }
                UiStatus.ERROR -> Log.i("fetchMain", "Get error on UiStatus observe")
            }
        })
    }

    private fun initViews() {
        tabView = findViewById(R.id.tabView)
        btnPrevious = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        posterView = findViewById(R.id.imgPoster)

        tabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    if(!loadingPoster) {
                        when (position) {
                            0 -> viewModel.changeCategory("latest")
                            1 -> viewModel.changeCategory("hot")
                            2 -> viewModel.changeCategory("top")
                            else -> throw IllegalArgumentException()
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

        btnPrevious.setOnClickListener {
            if (!loadingPoster) {
                viewModel.previousPoster()
                if (!btnNext.isEnabled) btnNext.isEnabled = true
            }
            Log.i("MainActivity", "Function called: click on btnPrevious!!!")
        }

        btnNext.setOnClickListener {
            if (!loadingPoster) {
                viewModel.nextPoster()
                if (!btnPrevious.isEnabled) btnPrevious.isEnabled = true
            }
            Log.i("MainActivity", "Function called: click on btnNext!!!")
        }
    }
}