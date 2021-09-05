package ru.fintech.devlife.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import kotlinx.serialization.ExperimentalSerializationApi
import ru.fintech.devlife.R
import kotlin.IllegalArgumentException

@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

    private lateinit var tabView: TabLayout
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var posterView: ImageView
    private lateinit var tvDescription: TextView

    private lateinit var errorLayout: LinearLayout
    private lateinit var btnRetry: MaterialButton

    private val viewModel: MainViewModel by viewModels()

    private var loadingPoster = false
    private var getError = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)

        initViews()

        viewModel.poster.observe(this, {
            //Log.i("fetchMain", "observe get: ${it?.get(0)?.posterUrl}")
            Glide.with(this)
                .load(it.posterUrl)
                .transform(FitCenter(), RoundedCorners(24))
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.broken_image)
                .into(posterView)
            it.description?.apply { tvDescription.text = it.description }
            viewModel.complete()
        })

        viewModel.uiAction.observe(this, { action ->
            //Log.i("fetchMain", "observe get: ${it?.get(0)?.posterUrl}")
            when(action) {
                UiAction.LOADING -> loadingPoster = true
                UiAction.OK -> {
                    loadingPoster = false
                    if (getError) {
                        getError = false
                        uiState()
                    }
                }
                UiAction.ERROR -> {
                    getError = true
                    errorState()
                }
                else -> throw IllegalArgumentException()
            }
        })

        viewModel.uiStatus.observe(this, { status ->
            //Log.i("fetchMain", "observe get: ${it?.get(0)?.posterUrl}")
            when(status) {
                UiStatus.START -> btnPrevious.isEnabled = false
                UiStatus.MIDDLE -> {
                    if (!btnPrevious.isEnabled) btnPrevious.isEnabled = true
                    if (!btnNext.isEnabled) btnNext.isEnabled = true
                }
                UiStatus.END -> btnNext.isEnabled = false
                else -> throw IllegalArgumentException()
            }
        })
    }

    private fun initViews() {
        tabView = findViewById(R.id.tabView)
        btnPrevious = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        posterView = findViewById(R.id.imgPoster)
        tvDescription = findViewById(R.id.tvDescription)
        errorLayout = findViewById(R.id.error)
        btnRetry = findViewById(R.id.btnRetry)

        errorLayout.visibility = View.GONE

        tabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    if(!loadingPoster) {
                        when (position) {
                            0 -> viewModel.changeCategory("latest")
                            1 -> viewModel.changeCategory("top")
                            2 -> viewModel.changeCategory("hot")
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

        btnRetry.setOnClickListener {
            viewModel.retryLoad()
            errorLayout.visibility = View.GONE
        }
    }

    private fun errorState() {
        posterView.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnPrevious.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    private fun uiState() {
        posterView.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        btnPrevious.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }
}