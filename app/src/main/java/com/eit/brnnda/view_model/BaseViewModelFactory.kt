package com.eit.brnnda.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eit.brnnda.Network.NetworkRepository
import java.lang.IllegalArgumentException

class BaseViewModelFactory(private val repository: NetworkRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)){
            return MyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}