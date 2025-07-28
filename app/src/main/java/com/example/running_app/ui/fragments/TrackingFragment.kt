package com.example.running_app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.running_app.R
import com.example.running_app.databinding.FragmentTrackingBinding
import com.example.running_app.services.Polyline
import com.example.running_app.services.TrackingService
import com.example.running_app.ui.MainActivity
import com.example.running_app.ui.viewmodels.RunMainViewModel
import com.example.running_app.util.Constants.ACTION_PAUSE_SERVICE
import com.example.running_app.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.running_app.util.Constants.ACTION_STOP_SERVICE
import com.example.running_app.util.Constants.MAP_ZOOM_IN_FOCUS
import com.example.running_app.util.Constants.POLYLINE_COLOR
import com.example.running_app.util.Constants.POLYLINE_WIDTH
import com.example.running_app.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private val viewModel: RunMainViewModel by viewModels()
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null

    private var isTracking = false
    private var locationPoints = mutableListOf<Polyline>()

    private var currentTimeInMillis = 0L

    private var toolbarMenu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_tracking_menu, menu)
                toolbarMenu = menu

                val cancelMenuItem = menu.findItem(R.id.mICancelRun)
                cancelMenuItem?.isVisible = currentTimeInMillis > 0L && !isTracking
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                     R.id.mICancelRun -> {
                         showCancelTrackingDialog()
                         true
                     }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED) // Asocia el MenuProvider con el ciclo de vida del Fragment

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        binding.btnToggleRun.setOnClickListener {
            if(TrackingUtility.hasNotificationPermission(requireContext())){
                toggleRun()
            }else{
                (activity as MainActivity).requestNotificationPermissions()
            }
        }

        subscribeToObservers()
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Do you want to cancel the run?")
            .setMessage("Are you sure you want to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
            dialog.show()
    }

    private fun stopRun(){
        sendCommandToService(ACTION_STOP_SERVICE)

        val action = TrackingFragmentDirections.trackingFragmentToRunFragment()
        findNavController().navigate(action)
    }

    private fun subscribeToObservers(){
        TrackingService.isTrackingActive.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.locationPoints.observe(viewLifecycleOwner, Observer {
            locationPoints = it
            addLatestPolyline()
            focusCameraInLocation()
        })

        TrackingService.timeRunInMilliseconds.observe(viewLifecycleOwner, Observer{
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormatStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleRun(){
        if (isTracking){
            toolbarMenu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if (!isTracking){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }else{
            binding.btnToggleRun.text = "Stop"
            toolbarMenu?.getItem(0)?.isVisible = true
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun focusCameraInLocation(){
        if (locationPoints.isNotEmpty() && locationPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    locationPoints.last().last(),
                    MAP_ZOOM_IN_FOCUS
                )
            )
        }
    }

    private fun addAllPolylines(){
        for (polyline in locationPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(locationPoints.isNotEmpty() && locationPoints.last().size > 1){
            val secondLastLatLng = locationPoints.last()[locationPoints.last().size - 2]
            val lastlangLng = locationPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(secondLastLatLng)
                .add(lastlangLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String){
        Intent(requireContext(), TrackingService::class.java).also { intent ->
            intent.action = action
            requireContext().startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}