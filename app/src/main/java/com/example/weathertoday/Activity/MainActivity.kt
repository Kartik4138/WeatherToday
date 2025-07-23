package com.example.weathertoday.Activity

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathertoday.Adapter.ForecastAdapter
import com.example.weathertoday.Model.CurrentResponseApi
import com.example.weathertoday.Model.ForecastResponseApi
import com.example.weathertoday.R
import com.example.weathertoday.ViewModel.WeatherViewModel
import com.example.weathertoday.databinding.ActivityMainBinding
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            if(lat==0.0){
                var lat=51.50
                var lon=-0.12
                var name="London"
            }

            addCityTxt.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }

            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat, lon, "metric")
                .enqueue(object : Callback<CurrentResponseApi> {
                    override fun onResponse(
                        call: Call<CurrentResponseApi?>,
                        response: Response<CurrentResponseApi?>
                    ) {
                        if(response.isSuccessful){
                            val data = response.body()
                            progressBar.visibility = View.GONE
                            mainLayout.visibility = View.VISIBLE
                            data?.let {
                                statusTxt.text = it.weather?.get(0)?.main ?: "-"
                                humidityTxt.text = it.main?.humidity?.toString()+"%"
                                windTxt.text = it.wind?.speed?.let {Math.round(it).toString()}+"Km"
                                currentTempTxt.text=it.main?.temp?.let {Math.round(it).toString()} +"°"
                                maxTempTxt.text=it.main?.tempMax?.let {Math.round(it).toString()} +"°"
                                minTempTxt.text=it.main?.tempMin?.let {Math.round(it).toString()} +"°"

                                val drawable = if(isNightNow()) {
                                    R.drawable.night_back
                                }

                                else{
                                    setDynamicallyWallplaper(it.weather?.get(0)?.icon?:"-")
                                }
                                bgImage.setImageResource(drawable)
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<CurrentResponseApi?>,
                        t: Throwable
                    ) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                    }
                })

            var radius=10f
            val decorView=window.decorView
            val rootView=(decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }


            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object : Callback<ForecastResponseApi> {
                    override fun onResponse(
                        call: Call<ForecastResponseApi>,
                        response: Response<ForecastResponseApi>
                    ) {
                        if(response.isSuccessful){
                            val data = response.body()
                            blurView.visibility=View.VISIBLE

                            data?.let{
                                forecastAdapter.differ.submitList(it.list)
                                rvForecast.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = forecastAdapter
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Forecast error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

        }
    }

    private fun isNightNow(): Boolean{
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }
    private fun setDynamicallyWallplaper(icon:String):Int{
        return when(icon.dropLast(1)){
            "01"->{
                R.drawable.snow_back
            }
            "02","03","04"->{
                R.drawable.cloudy_back
            }
            "09","10","11"->{
                R.drawable.rain_back
            }
            "13"->{
                R.drawable.snow_back
            }
            "50"->{
                R.drawable.haze_back
            }

            else->0
        }
    }


}