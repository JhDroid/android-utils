package com.jhdroid.utils.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jhdroid.utils.handler.Event
import com.jhdroid.utils.security.AES256Util

class MainViewModel : ViewModel() {

    private val _subActivityEvent = MutableLiveData<Event<Unit>>()
    val subActivityEvent: LiveData<Event<Unit>> = _subActivityEvent

    private val _clickCount = MutableLiveData(0)
    val count: LiveData<Int> = _clickCount

    private val _encodeData = MutableLiveData<String>()
    val encodeDate: LiveData<String> = _encodeData

    private val _decodeData = MutableLiveData<String>()
    val decodeDate: LiveData<String> = _decodeData

    fun moveSubActivity() {
        _subActivityEvent.value = Event(Unit)
    }

    fun increaseClickCount() {
        _clickCount.value = _clickCount.value?.plus(1)
    }

    fun encodeData(data: String) {
        _encodeData.value = data
    }

    fun decodeData(data: String) {
        _decodeData.value = data
    }
}
