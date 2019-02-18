package com.example.test2antplus.presenter

import android.content.Context
import com.example.test2antplus.MainApplication
import com.example.test2antplus.Profile
import com.example.test2antplus.navigation.AppRouter
import com.example.test2antplus.navigation.Screens
import com.example.test2antplus.ui.view.ProfileFragment
import javax.inject.Inject

class ProfilePresenter(private val view: ProfileFragment) {
    @Inject lateinit var router: AppRouter
    @Inject lateinit var appContext: Context

    private var profiles: List<Profile> = arrayListOf()
    private lateinit var selectedProfile: Profile

    init {
        MainApplication.graph.inject(this)
        view.showLoading()
        setData()
    }

    private fun setData() {
        view.hideLoading()
        if (profiles.isEmpty()) {
            view.hideProfilesList()
            view.showEmptyProfilesList()
        } else {
            view.hideEmptyProfilesList()
            view.showProfilesList()
        }
        view.setProfilesList(profiles)
    }

    fun selectProfile(id: Int) {
        selectedProfile = profiles[id]

        router.navigateTo(Screens.SCAN_FRAGMENT)
    }

    fun onCreateProfileClick() {
        router.navigateTo(Screens.SETTING_FRAGMENT)
    }

}