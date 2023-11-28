package ca.algomau.cosc3596.dalnemri.finalproject.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application){

    private val dataStore = DataStoreManager(application)
    val getBaseUrl = dataStore.getBaseUrl().asLiveData(Dispatchers.IO)
    val getApiKey = dataStore.getApiKey().asLiveData(Dispatchers.IO)
    val getModel = dataStore.getModel().asLiveData(Dispatchers.IO)

    fun setBaseUrl(url: String) {
        viewModelScope.launch {
            dataStore.setBaseUrl(url)
        }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch {
            dataStore.setApiKey(key)
        }
    }

    fun setModel(model: String) {
        viewModelScope.launch {
            dataStore.setModel(model)
        }
    }
}