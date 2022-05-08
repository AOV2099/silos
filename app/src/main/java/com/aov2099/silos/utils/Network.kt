package com.aov2099.silos.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi

class Network {
    companion object{
        @RequiresApi(Build.VERSION_CODES.M)
        fun conExists(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetwork != null
        }

    }
}