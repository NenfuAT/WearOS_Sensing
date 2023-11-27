package com.k21091.wearsensing.presentation
import android.app.Application
class GlobalVariable :Application(){
    var mode: String? = null

    companion object {
        private var instance : GlobalVariable? = null
        fun  getInstance(): GlobalVariable {
            if (instance == null)
                instance = GlobalVariable()
            return instance!!
        }
    }
}