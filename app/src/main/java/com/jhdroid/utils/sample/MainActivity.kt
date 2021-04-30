package com.jhdroid.utils.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.jhdroid.utils.extension.startActivity
import com.jhdroid.utils.handler.EventObserver
import com.jhdroid.utils.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        setupEvent()
    }

    private fun setupEvent() {
        viewModel.subActivityEvent.observe(this, EventObserver {
            startActivity(SubActivity())
        })
    }
}