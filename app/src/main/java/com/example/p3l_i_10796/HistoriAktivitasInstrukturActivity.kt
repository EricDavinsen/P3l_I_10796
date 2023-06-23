package com.example.p3l_i_10796

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.adapter.HistoriAktivitasInstrukturAdapter
import com.example.p3l_i_10796.api.InstrukturApi
import com.example.p3l_i_10796.databinding.ActivityHistoriAktivitasInstrukturBinding
import com.example.p3l_i_10796.models.AktivitasInstruktur
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class HistoriAktivitasInstrukturActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityHistoriAktivitasInstrukturBinding
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoriAktivitasInstrukturBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)

        val id = sharedPreferences.getInt("id",0)
        queue = Volley.newRequestQueue(this)

        binding.srBooking.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener{
            if (id != null) {
                allData(id)
            }
        })
        if (id != null) {
            allData(id)
        }
    }

    private fun allData(id: Int) {
        binding.srBooking.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, InstrukturApi.GET_AKTIVITAS_URL + id, Response.Listener { response ->
                var jo = JSONObject(response.toString())
                var history = arrayListOf<AktivitasInstruktur>()
                var id : Int = jo.getJSONArray("data").length()

                for(i in 0 until id) {
                    var data = AktivitasInstruktur(
                        jo.getJSONArray("data").getJSONObject(i).getString("NAMA_KELAS"),
                        jo.getJSONArray("data").getJSONObject(i).getString("TANGGAL_JADWAL_UMUM"),
                        jo.getJSONArray("data").getJSONObject(i).getString("NAMA_INSTRUKTUR"),
                        jo.getJSONArray("data").getJSONObject(i).getString("TARIF"),
                        jo.getJSONArray("data").getJSONObject(i).getString("HARI_JADWAL_UMUM"),
                        jo.getJSONArray("data").getJSONObject(i).getString("WAKTU_JADWAL_UMUM"),
                        jo.getJSONArray("data").getJSONObject(i).getString("JAM_MULAI"),
                        jo.getJSONArray("data").getJSONObject(i).getString("JAM_SELESAI"),
                    )
                    history.add(data)
                }
                var data_array: Array<AktivitasInstruktur> = history.toTypedArray()

                val layoutManager = LinearLayoutManager(this)
                val adapter : HistoriAktivitasInstrukturAdapter = HistoriAktivitasInstrukturAdapter(history,this)
                val rvPermission : RecyclerView = findViewById(R.id.rv_booking)

                rvPermission.layoutManager = layoutManager
                rvPermission.setHasFixedSize(true)
                rvPermission.adapter = adapter

                binding.srBooking.isRefreshing = false

                if (!data_array.isEmpty()) {
                    FancyToast.makeText(this@HistoriAktivitasInstrukturActivity, "Berhasil Mendapatkan Data!", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show()
                }else {
                    FancyToast.makeText(this@HistoriAktivitasInstrukturActivity, "Data Tidak Ditemukan", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show()
                }

            }, Response.ErrorListener { error ->
                binding.srBooking.isRefreshing = true
                try {

                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(
                        this,
                        errors.getString("message"),
                        FancyToast.LENGTH_SHORT, FancyToast.INFO, false
                    ).show()

                    binding.srBooking.isRefreshing = false
                } catch (e: Exception){
                    FancyToast.makeText(this, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                    binding.srBooking.isRefreshing = false
                }

            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer " + sharedPreferences.getString("token",null);
                return headers
            }
        }
        queue!!.add(stringRequest)

    }
}