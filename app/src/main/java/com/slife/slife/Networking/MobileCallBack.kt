package com.slife.slife.Networking

interface MobileCallBack<T> {
    fun success(data: T)
    fun failure(message: String)
}
