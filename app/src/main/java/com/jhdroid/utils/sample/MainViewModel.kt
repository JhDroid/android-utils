package com.jhdroid.utils.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jhdroid.utils.handler.Event

class MainViewModel : ViewModel() {

    private val _subActivityEvent = MutableLiveData<Event<Unit>>()
    val subActivityEvent: LiveData<Event<Unit>> = _subActivityEvent

    private val _clickCount = MutableLiveData(0)
    val count: LiveData<Int> = _clickCount

    fun moveSubActivity() {
        _subActivityEvent.value = Event(Unit)
    }

    fun increaseClickCount() {
        _clickCount.value = _clickCount.value?.plus(1)
    }
}
