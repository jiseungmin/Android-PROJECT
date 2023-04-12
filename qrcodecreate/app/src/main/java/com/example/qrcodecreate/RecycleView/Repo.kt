package com.example.qrcodecreate.RecycleView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.qrcodecreate.RecyclevView_fs.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Repo {
    fun getData(): LiveData<MutableList<User>> {
        val mutableData = MutableLiveData<MutableList<User>>()
        val database = Firebase.database("https://qrapp-2f5a6-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("User")
        myRef.addValueEventListener(object : ValueEventListener {
            val listData: MutableList<User> = mutableListOf<User>()
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val getData = userSnapshot.getValue(User::class.java)
                        listData.add(getData!!)

                        mutableData.value = listData
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return mutableData
    }
}