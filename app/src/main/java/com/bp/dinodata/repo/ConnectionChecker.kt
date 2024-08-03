package com.bp.dinodata.repo

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

interface IConnectionChecker {
    fun hasNetworkAccess(): Boolean
}

class ConnectionChecker(
    private val connectivityManager: ConnectivityManager
): IConnectionChecker {
    private val hasNetConnectivity: Boolean
        get() = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

    override fun hasNetworkAccess(): Boolean {
        return hasNetConnectivity
    }
}