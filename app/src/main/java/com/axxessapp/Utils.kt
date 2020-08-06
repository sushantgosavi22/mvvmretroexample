package com.axxessapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    const val LIST_DATA = "listData"
    const val QUERY = "query"
    const val COMMENT_TABLE = "comment"
    const val SHAPE_MODEL = "shapeModel"

    public fun showToast(context: Context, message: String?) {
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
    }


    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                getNetworkCapabilities(this)
            } ?: false
        } else {
            @Suppress("DEPRECATION")
            connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
    }

    /**
     * Get the network capabilities.
     *
     * @param networkCapabilities: Object of [NetworkCapabilities]
     */
    private fun getNetworkCapabilities(networkCapabilities: NetworkCapabilities): Boolean = networkCapabilities.run {
        when {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun getLocalFormatterDate(milliSeconds: Long?): String? {
        //DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        try{
            milliSeconds?.let {
                val dateFormatter: DateFormat = SimpleDateFormat("HH:mm MMM.dd yyyy")
                return dateFormatter.format(Date(milliSeconds))
            }
        }catch (e :Exception){
            e.printStackTrace()
        }
        return ""
    }
}