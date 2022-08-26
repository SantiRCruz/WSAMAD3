package com.example.wsamad3.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.wsamad3.R
import com.example.wsamad3.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar


class MapFragment : Fragment(R.layout.fragment_map),GoogleMap.OnMarkerClickListener{
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private val registerPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            enableLocation()
        }else{
            Snackbar.make(binding.root,"You Must enable the permission",Snackbar.LENGTH_SHORT).show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        childFragmentManager.findFragmentById(R.id.map)
        createFragment()
        clicks()

    }

    private fun clicks() {
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it

            enableLocation()
        }
    }

    private fun enableLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ->{ createLines() }
            else ->{registerPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)}
        }
    }

    @SuppressLint("MissingPermission")
    private fun createLines() {
        map.isMyLocationEnabled = true

        val loc = (requireContext().getSystemService(LOCATION_SERVICE) as LocationManager).getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val latLng = LatLng(loc!!.latitude,loc.longitude)
        val marker = MarkerOptions().position(latLng).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        map.addMarker(marker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18f))



        val latLng1 = LatLng(30.510667, 114.327195)
        val marker1 = MarkerOptions().position(latLng1).title("Zone Zero").icon(BitmapDescriptorFactory.fromResource(R.drawable.covid))
        map.addMarker(marker1)

        val lines = PolylineOptions()
            .width(4f)
            .color(R.color.blue_200)
            .add(latLng)
            .add(latLng1)

        map.addPolyline(lines)
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(m: Marker): Boolean {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(m.position,18f))
        return false
    }


}