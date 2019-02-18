package com.example.test2antplus.ui.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.test2antplus.MainApplication
import com.example.test2antplus.R
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import com.example.test2antplus.presenter.SettingPresenter
import javax.inject.Inject

interface SettingsInterface {
    fun showName(text: String)
    fun showAge(age: Int)
    fun showGender()
    fun onBackPressed()
}


class SettingsFragment: Fragment(), SettingsInterface {

    @Inject lateinit var appContext: Context
    @Inject lateinit var router: AppRouter

    private lateinit var presenter: SettingPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainApplication.graph.inject(this)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = SettingPresenter(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun showName(text: String) {
    }

    override fun showAge(age: Int) {

    }

    override fun showGender() {

    }

    override fun onBackPressed() {
        router.backTo(Screens.SCAN_FRAGMENT)
    }
}