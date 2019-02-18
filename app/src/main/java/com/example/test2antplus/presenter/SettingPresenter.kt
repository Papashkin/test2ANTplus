package com.example.test2antplus.presenter

import android.content.Context
import com.example.test2antplus.MainApplication
import com.example.test2antplus.ui.view.SettingsInterface
import javax.inject.Inject

class SettingPresenter(private val view: SettingsInterface) {
    @Inject lateinit var appContext: Context
    init {
        MainApplication.graph.inject(this)
    }

    fun setText() {
        view.showName("")
    }
}