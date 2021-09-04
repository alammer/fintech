package ru.fintech.devlife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    private lateinit var tabView: TabLayout
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton

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
                when (tab?.position) {
                    0 -> Log.i("MainActivity", "Function called: onTabSelected() 0 ${tab.position}")
                    1 -> Log.i("MainActivity", "Function called: onTabSelected() 1 ${tab.position}")
                    2 -> Log.i("MainActivity", "Function called: onTabSelected() 2 ${tab.position}")
                    else -> throw IllegalArgumentException()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }
}