package com.example.p3l_i_10796

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.adapter.AddBookingKelasAdapter
import com.example.p3l_i_10796.adapter.JadwalAdapter
import com.example.p3l_i_10796.api.LoginApi
import com.example.p3l_i_10796.api.MemberApi
import com.example.p3l_i_10796.databinding.ActivityAddBookingKelasBinding
import com.example.p3l_i_10796.models.BookingClass
import com.example.p3l_i_10796.models.JadwalHarian
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.nio.charset.StandardCharsets

class AddBookingKelas : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityAddBookingKelasBinding
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_booking_kelas)
        binding = ActivityAddBookingKelasBinding.inflate(layoutInflater)
        queue = Volley.newRequestQueue(this)

        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)

        allData()
    }

    private fun allData() {
        binding.srClass.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, LoginApi.tampilanJadwal, Response.Listener { response ->
                var jo = JSONObject(response.toString())
                var schedule = arrayListOf<JadwalHarian>()
                var id : Int = jo.getJSONArray("data").length()

                for(i in 0 until id) {
                    var data = JadwalHarian(
                        jo.getJSONArray("data").getJSONObject(i).getString("TANGGAL_JADWAL_HARIAN"),
                        jo.getJSONArray("data").getJSONObject(i).getString("NAMA_INSTRUKTUR"),
                        jo.getJSONArray("data").getJSONObject(i).getString("NAMA_KELAS"),
                        jo.getJSONArray("data").getJSONObject(i).getString("STATUS_JADWAL_HARIAN"),
                        jo.getJSONArray("data").getJSONObject(i).getString("HARI_JADWAL_UMUM"),
                        jo.getJSONArray("data").getJSONObject(i).getInt("ID_KELAS"),
                        jo.getJSONArray("data").getJSONObject(i).getDouble("TARIF"),
                    )
                    schedule.add(data)
                }
                var data_array: Array<JadwalHarian> = schedule.toTypedArray()

                val layoutManager = LinearLayoutManager(this)
                val adapter : AddBookingKelasAdapter = AddBookingKelasAdapter(schedule,this)
                val rvPermission : RecyclerView = findViewById(R.id.rv_jadwal)

                rvPermission.layoutManager = layoutManager
                rvPermission.setHasFixedSize(true)
                rvPermission.adapter = adapter

                binding.srClass.isRefreshing = false

                if (!data_array.isEmpty()) {
                    FancyToast.makeText(this@AddBookingKelas, "Menampilkan Data Jadwal Harian", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show()
                }else {
                    MotionToast.darkToast(
                        this, "Notification Booking!",
                        "Data not found",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                }

            },   Response.ErrorListener { error ->
                try  {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@AddBookingKelas, errors.getString("message"),
                        Toast.LENGTH_LONG
                    ).show();
                } catch (e: java.lang.Exception) {
                    Toast.makeText(
                        this@AddBookingKelas, e.message,
                        Toast.LENGTH_LONG
                    ).show();
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"

                return headers
            }
        }
        queue!!.add(stringRequest)
    }

    fun bookingClass(id_member: String, id_kelas: Int, tanggal: String){
        val booking = BookingClass(
            id_member,
            id_kelas,
            tanggal,
        )

        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, MemberApi.STORE_KELAS, Response.Listener { response ->
                val gson = Gson()
                var booking_data = gson.fromJson(response, BookingClass::class.java)

                var resJO = JSONObject(response.toString())
                val userobj = resJO.getJSONObject("data")

                if(booking_data!= null) {
                    MotionToast.darkColorToast(this,"Notification Booking!",
                        "Succesfully Booking Class!",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                    val intent = Intent(this@AddBookingKelas, BookingKelasActivity::class.java)
                    sharedPreferences.edit()
                        .putString("booking",null)
                        .apply()
                    startActivity(intent)
                }
                else {
                    MotionToast.darkColorToast(this,"Notification Booking!",
                        "Failed Booking Class",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                }
                return@Listener
            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    MotionToast.darkColorToast(this,"Notification Booking!",
                        errors.getString("message"),
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                }catch (e: java.lang.Exception) {
                    Toast.makeText(this@AddBookingKelas, e.message,
                        Toast.LENGTH_LONG).show();
                }
            }) {
                @kotlin.jvm.Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer " + sharedPreferences.getString("token",null);
                    return headers
                }

                @kotlin.jvm.Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(booking)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8;"
                }
            }
        queue!!.add(stringRequest)
    }
}