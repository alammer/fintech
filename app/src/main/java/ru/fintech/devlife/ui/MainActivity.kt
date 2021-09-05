package ru.fintech.devlife.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import ru.fintech.devlife.R
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    private lateinit var tabView: TabLayout
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var posterView: ShapeableImageView

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        viewModel.posterList.observe(this, {
            //Log.i("fetchMain", "observe get: ${it?.get(0)?.posterUrl}")
            Glide.with(this)
                .load(it?.get(0)?.posterUrl)
                //.centerCrop()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.broken_image)
                .into(posterView)
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
                    when (position) {
                        0 -> Log.i(
                            "MainActivity",
                            "Function called: onTabSelected() 0 ${tab.position}"
                        )
                        1 -> Log.i(
                            "MainActivity",
                            "Function called: onTabSelected() 1 ${tab.position}"
                        )
                        2 -> Log.i(
                            "MainActivity",
                            "Function called: onTabSelected() 2 ${tab.position}"
                        )
                        else -> throw IllegalArgumentException()
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
            Log.i("MainActivity", "Function called: click on btnPrevious!!!")
        }

        btnNext.setOnClickListener {
            Log.i("MainActivity", "Function called: click on btnNext!!!")
        }
    }
}