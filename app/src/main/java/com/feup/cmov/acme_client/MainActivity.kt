package com.feup.cmov.acme_client

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.fragment.app.FragmentManager
import com.feup.cmov.acme_client.databinding.ActivityMainBinding
import com.feup.cmov.acme_client.screens.checkout.order_placed.OrderPlacedFragment
import com.feup.cmov.acme_client.screens.orders.pickup_success.PickupSuccessFragment
import com.feup.cmov.acme_client.utils.Debug
import com.feup.cmov.acme_client.utils.ShowFeedback
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // TODO: REMOVE THIS BEFORE TURNING IN PROJECT!
    @Inject
    lateinit var debug: Debug

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debug.startDebugMode()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        activity = this
        Companion.fragmentManager = supportFragmentManager
    }

    override fun onBackPressed() {
        // Prevent pressing back button on some fragments.
        // Namely: OrderPlacedFragment
        try {
            val navHostFragment: NavHostFragment =
                supportFragmentManager.findFragmentById(R.id.acmeNavHostFragment) as NavHostFragment
            navHostFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment !is OrderPlacedFragment && fragment !is PickupSuccessFragment)
                    super.onBackPressed()
            }
        }
        catch(e: Exception) {
            Log.e("MainActivity", e.toString())
        }
    }

    companion object {
        private lateinit var activity: Activity
        private lateinit var fragmentManager: FragmentManager

        fun getActivity(): Activity {
            return activity
        }

        fun getFragmentManager(): FragmentManager {
            return fragmentManager
        }
    }
}