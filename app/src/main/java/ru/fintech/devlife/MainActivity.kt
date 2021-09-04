package ru.fintech.devlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    private lateinit var tabView: TabLayout
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        tabView = findViewById(R.id.tabView)
        btnPrevious = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)

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