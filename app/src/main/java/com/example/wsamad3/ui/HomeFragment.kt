package com.example.wsamad3.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.wsamad3.R
import com.example.wsamad3.core.Constants
import com.example.wsamad3.data.get
import com.example.wsamad3.data.models.History
import com.example.wsamad3.data.models.Stats
import com.example.wsamad3.data.models.UniqueStat
import com.example.wsamad3.databinding.FragmentHomeBinding
import com.example.wsamad3.ui.adapter.StatsAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val historyList = mutableListOf<History>()
    private val statsList = mutableListOf<Stats>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        obtainStats()
        obtainCases()
        obtainHistory()
        obtainActualDate()
        animateIn()
        clicks()


    }

    private fun obtainStats() {
        Constants.okHttp.newCall(get("covid_stats")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data = json.getJSONObject("data")
                val world = data.getJSONObject("world")
                val currentCity = data.getJSONObject("current_city")
                val worldBefore = data.getJSONObject("world_before")
                val cityBefore = data.getJSONObject("city_before")
                statsList.add(
                    Stats(
                        "world",
                        world.getInt("infected"),
                        world.getInt("death"),
                        world.getInt("recovered"),
                        world.getInt("vaccinated"),
                        world.getInt("recovered_adults"),
                        world.getInt("recovered_young"),
                    )
                )
                statsList.add(
                    Stats(
                        "current city",
                        currentCity.getInt("infected"),
                        currentCity.getInt("death"),
                        currentCity.getInt("recovered"),
                        currentCity.getInt("vaccinated"),
                        currentCity.getInt("recovered_adults"),
                        currentCity.getInt("recovered_young"),
                    )
                )
                statsList.add(
                    Stats(
                        "world before",
                        worldBefore.getInt("infected"),
                        worldBefore.getInt("death"),
                        worldBefore.getInt("recovered"),
                        worldBefore.getInt("vaccinated"),
                        worldBefore.getInt("recovered_adults"),
                        worldBefore.getInt("recovered_young"),
                    )
                )
                statsList.add(
                    Stats(
                        "city before",
                        cityBefore.getInt("infected"),
                        cityBefore.getInt("death"),
                        cityBefore.getInt("recovered"),
                        cityBefore.getInt("vaccinated"),
                        cityBefore.getInt("recovered_adults"),
                        cityBefore.getInt("recovered_young"),
                    )
                )
                requireActivity().runOnUiThread {
                    setViewPagers()
                }
            }
        })
    }

    private fun setViewPagers() {
        setInfected()
        setDeaths()
        setRecovered()
        setVaccinated()
    }

    private fun setInfected() {
        val uniqueStat = mutableListOf<UniqueStat>()
        statsList.forEach {
            uniqueStat.add(UniqueStat(it.infected.toString(),"Infection Cases","Over all the ${it.tittle}",""))
        }
        binding.rvInfected.adapter = StatsAdapter(uniqueStat)
        binding.rvInfected.orientation = ViewPager2.ORIENTATION_VERTICAL
    }
    private fun setDeaths() {
        val uniqueStat = mutableListOf<UniqueStat>()
        statsList.forEach {
            uniqueStat.add(UniqueStat(it.death.toString(),"Deaths","Over all the ${it.tittle}",""))
        }
        binding.rvDeaths.adapter = StatsAdapter(uniqueStat)
        binding.rvDeaths.orientation = ViewPager2.ORIENTATION_VERTICAL
    }
    private fun setRecovered() {
        val uniqueStat = mutableListOf<UniqueStat>()
        statsList.forEach {
            uniqueStat.add(UniqueStat(it.recovered.toString(),"Recovered","${it.recovered_adults}% - adults","${it.recovered_young}% - Young"))
        }
        binding.rvRecovered.adapter = StatsAdapter(uniqueStat)
        binding.rvRecovered.orientation = ViewPager2.ORIENTATION_VERTICAL
    }
    private fun setVaccinated() {
        val uniqueStat = mutableListOf<UniqueStat>()
        statsList.forEach {
            var vaccinated = if (it.vaccinated>60){
                "It’s a nice result for making world safe"
            }else{
                "It’s very bad result for making world safe"
            }
            uniqueStat.add(UniqueStat("${it.vaccinated}%","in your ${it.tittle}","People Vaccinated",vaccinated))
        }
        binding.rvVaccinated.adapter = StatsAdapter(uniqueStat)
        binding.rvVaccinated.orientation = ViewPager2.ORIENTATION_VERTICAL
    }

    private fun clicks() {
        binding.imgQr.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_qrFragment) }
        binding.imgMap.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_mapFragment) }
        binding.btnSignIn.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_checkListFragment) }
    }

    private fun obtainHistory() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(Constants.USER, Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", "")
        val name = sharedPreferences.getString("name", "")
        binding.txtNoDateName.text = name
        binding.txtWithDateName.text = name
        Constants.okHttp.newCall(get("symptoms_history?user_id=$id")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")) {
                    val data = json.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        historyList.add(
                            History(
                                SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(
                                    item.getString(
                                        "date"
                                    )
                                ), item.getInt("probability_infection")
                            )
                        )
                    }
                    val finalData = historyList[data.length() - 1]
                    requireActivity().runOnUiThread {
                        binding.llWithData.visibility = View.VISIBLE
                        binding.llWithData1.visibility = View.VISIBLE
                        binding.txtDateMonth.text = SimpleDateFormat("dd/mm").format(finalData.date)
                        binding.txtYearHour.text =
                            SimpleDateFormat("/yyyy Kk:mmaa").format(finalData.date)
                        if (finalData.probability_infection > 50) {
                            binding.constraintLayout3.backgroundTintList =
                                getColorStateList(requireContext(), R.color.dark_blue)
                            binding.txtTitleWithData.text = "CALL TO DOCTOR"
                            binding.txtWithDataDescription.text = "You may be infected with a virus"
                        } else {
                            binding.constraintLayout3.backgroundTintList =
                                getColorStateList(requireContext(), R.color.blue_200)
                            binding.txtTitleWithData.text = "CLEAR"
                            binding.txtWithDataDescription.text =
                                "* Wear mask. Keep 2m distance. Wash hands."
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        binding.llNoData1.visibility = View.VISIBLE
                        binding.llNoData.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun obtainCases() {
        Constants.okHttp.newCall(get("cases")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data = json.getInt("data")
                requireActivity().runOnUiThread {
                    if (data > 0) {
                        binding.txtNumCases.text = "$data Cases"
                        binding.txtNumCases1.text = "$data Cases"
                        binding.llNoDataBg.backgroundTintList =
                            getColorStateList(requireContext(), R.color.dark_blue)
                        binding.llWithDataBg.backgroundTintList =
                            getColorStateList(requireContext(), R.color.dark_blue)
                    } else {
                        binding.txtNumCases.text = "No Cases"
                        binding.txtNumCases1.text = "No Cases"
                    }
                }
            }
        })
    }

    private fun obtainActualDate() {
        binding.txtActualDate.text = SimpleDateFormat("MMM dd, yyyy").format(Date())
    }

    private fun animateIn() {
        binding.txtTitle.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.sttb))
        binding.imgMap.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.sttb))
        binding.imgQr.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.sttb))
        binding.txtActualDate.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.sttb
            )
        )
        binding.constraintLayout.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.sttb
            )
        )
        binding.llNoData1.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.sbtt
            )
        )
        binding.llWithData.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.sbtt
            )
        )
    }

}