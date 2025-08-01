package com.example.weathertoday.Activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathertoday.Adapter.CityAdapter
import com.example.weathertoday.Model.CityResponseApi
import com.example.weathertoday.Model.ForecastResponseApi
import com.example.weathertoday.R
import com.example.weathertoday.ViewModel.CityViewModel
import com.example.weathertoday.databinding.ActivityCityListBinding
import com.example.weathertoday.databinding.ForecastViewholderBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy { CityAdapter() }
    private val cityViewModel: CityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {
            cityEdit.addTextChangedListener(object  : TextWatcher{
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    progressBar2.visibility= View.VISIBLE
                    cityViewModel.loadCity(p0.toString(), 10)
                        .enqueue(object : Callback<CityResponseApi>{
                            override fun onResponse(
                                call: Call<CityResponseApi?>,
                                response: Response<CityResponseApi?>
                            ) {
                                if(response.isSuccessful){
                                    val data = response.body()
                                    data?.let {
                                        progressBar2.visibility = View.GONE
                                        cityAdapter.differ.submitList(it)
                                        rvCity.apply {
                                            layoutManager = LinearLayoutManager(
                                                this@CityListActivity,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                            adapter = cityAdapter
                                        }
                                    }
                                }
                            }

                            override fun onFailure(
                                Call: Call<CityResponseApi?>,
                                t: Throwable
                            ) {
                                TODO("Not yet implemented")
                            }

                        })
                }

            })
        }

    }
}