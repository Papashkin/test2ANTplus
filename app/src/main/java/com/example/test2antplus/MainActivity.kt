package com.example.test2antplus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.test2antplus.di.AppComponent
import com.example.test2antplus.di.DaggerAppComponent
import com.example.test2antplus.di.modules.AppModule
import com.example.test2antplus.di.modules.NavigationModule
import com.example.test2antplus.navigation.AppNavigator
import com.example.test2antplus.navigation.AppRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var graph: AppComponent
    }

    @Inject
    lateinit var cicerone: Cicerone<AppRouter>
    @Inject
    lateinit var router: AppRouter
    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private lateinit var navigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainApplication.graph.inject(this)

        graph = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .navigationModule(NavigationModule())
            .build()

        graph.inject(this)

        navigator = AppNavigator(this)

        router.startChain("profiles screen")
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
//        cicerone.navigatorHolder.removeNavigator()
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
//        cicerone.navigatorHolder.setNavigator(navigator)
    }

    override fun onBackPressed() {
        router.backTo(null)
        super.onBackPressed()
    }
}