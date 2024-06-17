package com.progix.fridgex.freshKeeper.custom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.progix.fridgex.freshKeeper.application.FreshKeeperApplication

abstract class ApplicationBindedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as FreshKeeperApplication).setCurrentContext(this)
    }

    override fun onStart() {
        super.onStart()
        (applicationContext as FreshKeeperApplication).setCurrentContext(this)
    }

    override fun onResume() {
        super.onResume()
        (applicationContext as FreshKeeperApplication).setCurrentContext(this)
    }

}