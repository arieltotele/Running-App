package com.example.running_app.ui.host

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.running_app.BuildConfig
import com.example.running_app.R
import com.example.running_app.databinding.ActivityMainBinding
import com.example.running_app.ui.viewmodel.UserProfileViewModel
import com.example.running_app.util.Constants
import com.example.running_app.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Read meta-data from AndroidManifest.xml
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val metaData = appInfo.metaData

        // Get value from BuildConfig
        val mapsApiKey = BuildConfig.MAPS_API_KEY

        // Overwrite meta-data values with BuildConfig
        metaData.putString("com.google.android.geo.API_KEY", mapsApiKey)

        navHostFragment = supportFragmentManager.
        findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemReselectedListener { /* Do-Nothing */ }

        navHostFragment.findNavController().addOnDestinationChangedListener{ _, destination, _ ->
                binding.bottomNavigationView.visibility = when (destination.id) {
                    R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment -> View.VISIBLE
                    else -> View.GONE
                }
        }

        userProfileViewModel.userProfile.observe(this, Observer { userProfile ->
            if (userProfile != null && userProfile.weight > 0f) {
                binding.tvToolbarTitle.text = "Let's go, ${userProfile.name}!"
            } else {
                Timber.Forest.d("User profile is null or weight is not valid: ${userProfile?.weight}")
            }
        })

        navigateToTrackingFragmentIfNeeded(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

    fun requestNotificationPermissions(){
        if (TrackingUtility.hasNotificationPermission(this)){
            return
        }else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                Constants.REQUEST_CODE_NOTIFICATION_PERMISSIONS,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

    }

    fun requestPermissions(bgLocation: Boolean = false){
        if (TrackingUtility.hasLocationPermissions(this)){ return }
        if (bgLocation && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to \"Allow all the time\" to track runs in background.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
            requestNotificationPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
            this)
    }
}