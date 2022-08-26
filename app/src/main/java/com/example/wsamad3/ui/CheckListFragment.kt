package com.example.wsamad3.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wsamad3.R
import com.example.wsamad3.core.Constants
import com.example.wsamad3.data.*
import com.example.wsamad3.data.models.Symptom
import com.example.wsamad3.databinding.FragmentCheckListBinding
import com.example.wsamad3.ui.adapter.SymptomsAdapter
import com.google.android.material.snackbar.Snackbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException


class CheckListFragment : Fragment(R.layout.fragment_check_list) {
    private lateinit var binding: FragmentCheckListBinding
    private val symptomList = mutableListOf<Symptom>()
    private var uriResult: Uri? = null
    private val registerGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.data

            data?.let { uri ->
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor =
                    requireContext().contentResolver.query(uri, projection, null, null, null)
                val column = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
                if (cursor.moveToNext()) uriResult = Uri.parse(cursor.getString(column))

                binding.imgClose.visibility = View.VISIBLE
                binding.imgAdd.setImageURI(uriResult)
            }


        }
    private val registerPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (false in it.values) {
                Snackbar.make(
                    binding.root,
                    "You must enable the permissions",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                pickFromGallery()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckListBinding.bind(view)
        obtainActualDate()
        clicks()

    }

    private fun clicks() {
        binding.imgAdd.setOnClickListener { requestPermission() }
        binding.imgClose.setOnClickListener {
            uriResult = null
            binding.imgClose.visibility = View.GONE
            binding.imgAdd.setImageResource(R.drawable.add)
        }
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSignIn.setOnClickListener { validate() }
    }

    private fun validate() {
        if (uriResult==null){
            Snackbar.make(binding.root,"You can't send info without a photo",Snackbar.LENGTH_SHORT).show()
            return
        }
        if ((binding.rvData.adapter as SymptomsAdapter).checkedBoxes().isNullOrEmpty()){
            Log.e("validate: ", (binding.rvData.adapter as SymptomsAdapter).checkedBoxes().toString())
            Snackbar.make(binding.root,"You can't send info without select something",Snackbar.LENGTH_SHORT).show()
            return
        }
        alertDialog()
    }

    private fun alertDialog() {
        Log.e("alert: ", (binding.rvData.adapter as SymptomsAdapter).checkedBoxes().toString())
        AlertDialog.Builder(requireContext())
            .setTitle("Sure you want to send this info?")
            .setNegativeButton("No"){d,r ->
                d.dismiss()
            }
            .setPositiveButton("Yes"){d,r ->
                sendList()
                sendPhoto()
                d.dismiss()
            }
            .setCancelable(false)
            .show()

    }

    private fun sendPhoto() {
        Constants.okHttp.newCall(post2("https://cloudlabs-image-object-detection.p.rapidapi.com/objectDetection/byImageFile",
            image(uriResult!!))).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString() )
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getString("status")=="success"){
                    Snackbar.make(binding.root,"The image was successfully sent",Snackbar.LENGTH_SHORT).show()
                    requireActivity().runOnUiThread {
                        findNavController().popBackStack()
                    }
                }else{
                    Snackbar.make(binding.root,"The image was unsuccessfully sent",Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun sendList() {
        Constants.okHttp.newCall(post("day_symptoms", daysSymptom((binding.rvData.adapter as SymptomsAdapter).checkedBoxes()))).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString() )
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")){
                    Snackbar.make(binding.root,"The checklist was successfully sent",Snackbar.LENGTH_SHORT).show()
                }else{
                    Snackbar.make(binding.root,"The checklist was unsuccessfully sent",Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickFromGallery()
            }
            else -> {
                registerPermission.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        registerGallery.launch(i)
    }

    private fun obtainActualDate() {
        Constants.okHttp.newCall(get("symptom_list")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data = json.getJSONArray("data")
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    symptomList.add(
                        Symptom(
                            item.getInt("id"),
                            item.getString("title"),
                            item.getInt("priority")
                        )
                    )
                    requireActivity().runOnUiThread {
                        binding.rvData.adapter = SymptomsAdapter(symptomList)
                        binding.rvData.layoutManager = LinearLayoutManager(requireContext())

                    }
                }
            }
        })
    }
}