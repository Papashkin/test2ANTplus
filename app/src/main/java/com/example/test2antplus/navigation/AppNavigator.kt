package com.example.test2antplus.navigation

import android.support.v4.app.Fragment
import com.example.test2antplus.MainActivity
import com.example.test2antplus.R
import com.example.test2antplus.SelectedDevice
import com.example.test2antplus.navigation.commands.BackTo
import com.example.test2antplus.navigation.commands.NavigateTo
import com.example.test2antplus.navigation.commands.StartChain
import com.example.test2antplus.ui.view.ProfileFragment
import com.example.test2antplus.ui.view.ScanFragment
import com.example.test2antplus.ui.view.SettingsFragment
import com.example.test2antplus.ui.view.WorkFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import javax.inject.Inject

class AppNavigator @Inject constructor(
    private val activity: MainActivity
): Navigator {
    private var fragmentStack = arrayListOf<String>()
    private lateinit var currentFragment: Fragment

    override fun applyCommands(commands: Array<out Command>?) {
        commands?.apply {
            this.forEach {
                when (it) {
                    is NavigateTo -> navigateTo(it)
                    is BackTo -> backTo(it)
                    is StartChain -> startChain(it)
                }
            }
        }
    }

    private fun navigateTo(command: NavigateTo) {
        when (command.screenKey) {
            Screens.SETTING_FRAGMENT -> {
                fragmentStack.add(command.screenKey)
                currentFragment = SettingsFragment()
                moveToFragment(currentFragment, command.screenKey, true)
            }
            Screens.SCAN_FRAGMENT -> {
                fragmentStack.add(command.screenKey)
                currentFragment = ScanFragment()
                moveToFragment(currentFragment, command.screenKey, true)
            }
            Screens.WORK_FRAGMENT -> {
                fragmentStack.add(command.screenKey)
                currentFragment = WorkFragment().newInstance(command.data as ArrayList<SelectedDevice>)
                moveToFragment(currentFragment, command.screenKey, true)
            }

        }
    }

    private fun backTo(command: BackTo) {
        val key = command.screenKey
        if (key == null) {
            backToRoot()
        } else {
            for (i in 0 until activity.supportFragmentManager.backStackEntryCount) {
                if (key == activity.supportFragmentManager.getBackStackEntryAt(i).name) {
                    activity.supportFragmentManager.popBackStackImmediate(key, 0)
                    break
                }
            }
        }
    }

    private fun backToRoot() {
        for (i in 0 until activity.supportFragmentManager.backStackEntryCount) {
            activity.supportFragmentManager.popBackStack()
        }
        activity.supportFragmentManager.executePendingTransactions()
    }

    private fun startChain(command: StartChain) {
        when (command.screenKey) {
//            Screens.SCAN_FRAGMENT -> {
//                fragmentStack.add(command.screenKey)
//                val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
//                fragmentTransaction.add(R.id.fragmentLayout, ScanFragment())
//                fragmentTransaction.addToBackStack(command.screenKey)
//                fragmentTransaction.commit()
//            }
            Screens.PROFILES_FRAGMENT -> {
                fragmentStack.add(command.screenKey)
                val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.fragmentLayout, ProfileFragment())
                fragmentTransaction.addToBackStack(command.screenKey)
                fragmentTransaction.commit()
            }
        }
    }

    private fun moveToFragment(fragment: Fragment, tag: String, addToStack: Boolean) {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        if (addToStack) {
            fragmentStack.add(tag)
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.add(R.id.fragmentLayout, fragment)
        fragmentTransaction.commit()
    }
}