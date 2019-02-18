package com.example.test2antplus.di

import com.example.test2antplus.MainActivity
import com.example.test2antplus.MainApplication
import com.example.test2antplus.di.modules.AppModule
import com.example.test2antplus.di.modules.NavigationModule
import com.example.test2antplus.presenter.ProfilePresenter
import com.example.test2antplus.presenter.ScanPresenter
import com.example.test2antplus.presenter.SettingPresenter
import com.example.test2antplus.presenter.WorkPresenter
import com.example.test2antplus.ui.view.ProfileFragment
import com.example.test2antplus.ui.view.ScanFragment
import com.example.test2antplus.ui.view.SettingsFragment
import com.example.test2antplus.ui.view.WorkFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(mainApplication: MainApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(scanFragment: ScanFragment)
    fun inject(scanPresenter: ScanPresenter)
    fun inject(workFragment: WorkFragment)
    fun inject(workPresenter: WorkPresenter)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(settingPresenter: SettingPresenter)
    fun inject(profileFragment: ProfileFragment)
    fun inject(profilePresenter: ProfilePresenter)
}