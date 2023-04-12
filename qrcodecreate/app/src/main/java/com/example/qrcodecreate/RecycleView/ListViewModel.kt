package com.example.qrcodecreate.RecycleView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qrcodecreate.RecyclevView_fs.User

class ListViewModel : ViewModel() {
    private val repo = Repo()
    fun fetchData(): LiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()
        repo.getData().observeForever{
            mutableData.value = it
        }
        return mutableData
    }
}