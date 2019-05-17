package com.example.appk.module.refrence

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import com.example.appk.R
import kotlinx.coroutines.*

class KotlinCoroutinesActivity : AppCompatActivity() {

    private val TAG = "LYT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_coroutine)
        GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG,"launch start = "+ Thread.currentThread().name)
            val user = withContext(Dispatchers.Unconfined) {
                Log.d(TAG,"new User = "+Thread.currentThread().name)
                User("LYT", 20)
            }
            Log.d(TAG,"launch end = "+Thread.currentThread().name)
            val cur = async(Dispatchers.Default) {
                val s = Thread.currentThread()
                Log.d(TAG,"cur start = "+Thread.currentThread().name)
                delay(5000)
                val e = Thread.currentThread()
                Log.d(TAG,"${s == e}")
                Log.d(TAG,"cur done = "+Thread.currentThread().name)
                10
            }
            val suggest = launch(Dispatchers.Default) {
                Log.d(TAG,"suggest start = "+Thread.currentThread().name)
                delay(200)
                Log.d(TAG,"suggest done = "+Thread.currentThread().name)
            }
            Log.d(TAG,"${cur.await()}")
            Log.d(TAG,"e")
        }
    }
}

data class User(val name: String, val age: Int)


