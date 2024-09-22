package com.example.running_app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.running_app.BuildConfig
import com.example.running_app.R
import com.example.running_app.databinding.ActivityMainBinding
import com.example.running_app.util.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.example.running_app.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding

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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        binding.bottomNavigationView.setupWithNavController(navController)

        navHostFragment.findNavController().addOnDestinationChangedListener{ _, destination, _ ->
                binding.bottomNavigationView.visibility = when (destination.id) {
                    R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment -> View.VISIBLE
                    else -> View.GONE
                }
        }
    }

    fun requestPermissions(bgLocation: Boolean = false){
        if (TrackingUtility.hasLocationPermissions(this)){ return }
        if (bgLocation && Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            EasyPermissions.requestPermissions(
                this,
                "You need to \"Allow all the time\" to track runs in background.",
                REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                )
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
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