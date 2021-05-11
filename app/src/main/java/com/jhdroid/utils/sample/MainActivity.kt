package com.jhdroid.utils.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jhdroid.utils.extension.startActivity
import com.jhdroid.utils.handler.EventObserver
import com.jhdroid.utils.sample.databinding.ActivityMainBinding
import com.jhdroid.utils.security.AES256Util

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

        viewModel.encodeDate.observe(this, Observer {
            binding.encodeResultTv.text = AES256Util.aesEncode(it)
        })

        viewModel.decodeDate.observe(this, Observer {
            binding.encodeResultTv.text = AES256Util.aesDecode(it)
        })
    }
}