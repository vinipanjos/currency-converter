package com.vinicius.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.vinicius.currencyconverter.api.Endpoint
import com.vinicius.currencyconverter.databinding.ActivityMainBinding
import com.vinicius.currencyconverter.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrencies()

        binding.btConvert.setOnClickListener {convertMoney()}
    }
    fun convertMoney(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencyRate(binding.spFrom.selectedItem.toString(), binding.spTo.selectedItem.toString()).enqueue(object :
            Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = response.body()?.entrySet()?.find {it.key == binding.spTo.selectedItem.toString() }
                val rate: Double = data?.value.toString().toDouble()
                val conversion = binding.etValueFrom.toString().toDouble() * rate

                binding.tvResult.text = conversion.toString()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Deu erro !")
            }

        })
    }

    fun getCurrencies() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencies().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                val posBrl = data.indexOf("brl")
                val posUsd = data.indexOf("usd")

                val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                binding.spFrom.adapter = adapter
                binding.spTo.adapter = adapter

                binding.spFrom.setSelection(posBrl)
                binding.spTo.setSelection(posUsd)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Deu erro !")
            }
        })
    }
}