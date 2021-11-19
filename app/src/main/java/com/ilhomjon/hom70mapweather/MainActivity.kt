package com.ilhomjon.hom70mapweather

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.ilhomjon.hom70mapweather.databinding.ActivityMainBinding
import com.ilhomjon.hom70mapweather.models.MyWeather
import com.ilhomjon.hom70mapweather.utils.NetworkHelper
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var networkHelper: NetworkHelper
    lateinit var requestQueue: RequestQueue

    var latLng: LatLng? = null
    var urlString =
        "https://api.openweathermap.org/data/2.5/weather?lat=-34.0&lon=151.0&appid=47fc9b7e325547dd55e54f0952a99319"

    var url1 = "https://api.openweathermap.org/data/2.5/"
    var latlngS = "weather?lat=-34.0&lon=151.0&"
    var keyS = "appid=47fc9b7e325547dd55e54f0952a99319"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        latLng = LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("long", 0.0))

        networkHelper = NetworkHelper(this)
        requestQueue = Volley.newRequestQueue(this)

        latlngS = "weather?lat=${latLng?.latitude.toString()}&lon=${latLng?.longitude.toString()}&"
        urlString = url1+latlngS+keyS
        println(urlString)

        binding.tvMore.setOnClickListener {
            val url = "https://openweathermap.org/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        if (networkHelper.isNetworkConnected()) {
            loading()
        } else {
            Toast.makeText(this, "Internetga qayta ulaning...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loading() {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, urlString, null,
            object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject?) {
                    if (response!=null){
                        val myWeather =
                            Gson().fromJson<MyWeather>(response.toString(), MyWeather::class.java)

                        binding.tvDesc.text = myWeather.weather[0].description
                        binding.tvTemp.text = "${(myWeather.main.temp-273.15).toInt()} °C"
                        binding.tvTempMin.text = "${(myWeather.main.temp_min-273.15).toInt()} °C"
                        binding.tempMax.text = "${(myWeather.main.temp_max-273.15).toInt()} °C"

                        binding.tvHum.text = myWeather.main.humidity.toString() +" %"

                        binding.name.text = myWeather.name
                        binding.tvWindSpeed.text = myWeather.wind.speed.toString()+" m/s"

                        if (myWeather.clouds.all>10){
                            binding.imageW.setImageResource(R.drawable.cloudverry)
                        }else if (myWeather.clouds.all>0){
                            binding.imageW.setImageResource(R.drawable.cloudy)
                        }
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Toast.makeText(this@MainActivity, "Serverga bog'lanib bo'lmadi iltimos keyinroq qayta urinib ko'ring....", Toast.LENGTH_SHORT).show()
                }
            })
        requestQueue.add(jsonObjectRequest)
    }
}