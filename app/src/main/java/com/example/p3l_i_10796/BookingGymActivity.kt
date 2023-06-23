package com.example.p3l_i_10796

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.adapter.BookingGymAdapter
import com.example.p3l_i_10796.api.MemberApi
import com.example.p3l_i_10796.databinding.ActivityBookingGymBinding
import com.example.p3l_i_10796.models.BookingGym
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.nio.charset.StandardCharsets

class BookingGymActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingGymBinding
    private var queue: RequestQueue? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingGymBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)

        val id = sharedPreferences.getString("id", null)
        queue = Volley.newRequestQueue(this)

        binding.srBooking.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if(id != null){
                allDataBooking(id)
            }
        })

        if(id != null){
            allDataBooking(id)
        }

        binding.buttonCreate.setOnClickListener {
            val intent = Intent(this@BookingGymActivity, AddBookingGym::class.java)
            startActivity(intent)
        }

    }

    private fun allDataBooking(id: String) {
        binding.srBooking.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, MemberApi.GET_ALL_URL+ id,
                Response.Listener { response ->
                    var jo = JSONObject(response.toString())
                    var BookingGym = arrayListOf<BookingGym>()
                    var id: Int = jo.getJSONArray("data").length()

                    for (i in 0 until id) {
                        var data = BookingGym(
                            jo.getJSONArray("data").getJSONObject(i).getString("KODE_BOOKING_GYM"),
                            jo.getJSONArray("data").getJSONObject(i).getString("ID_MEMBER"),
                            jo.getJSONArray("data").getJSONObject(i).getString("SLOT_WAKTU_GYM"),
                            jo.getJSONArray("data").getJSONObject(i)
                                .getString("STATUS_PRESENSI_GYM"),
                            jo.getJSONArray("data").getJSONObject(i)
                                .getString("TANGGAL_BOOKING_GYM"),
                            jo.getJSONArray("data").getJSONObject(i)
                                .getString("TANGGAL_MELAKUKAN_BOOKING"),
                            jo.getJSONArray("data").getJSONObject(i)
                                .getString("WAKTU_PRESENSI_GYM"),
                        )
                        BookingGym.add(data)
                    }

                    var data_array: Array<BookingGym> = BookingGym.toTypedArray()

                    val layoutManager = LinearLayoutManager(this)
                    val adapter: BookingGymAdapter  = BookingGymAdapter(BookingGym, this)
                    val rvPermission: RecyclerView = findViewById(R.id.rv_booking)

                    rvPermission.layoutManager = layoutManager
                    rvPermission.setHasFixedSize(true)
                    rvPermission.adapter = adapter

                    binding.srBooking.isRefreshing = false

                    if (!data_array.isEmpty()) {
                        MotionToast.darkToast(
                            this, "Notification Display!",
                            "Succesfully get data",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )
                    }else {
                        MotionToast.darkToast(
                            this, "Notification Display!",
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

                },
                Response.ErrorListener { error ->
                    binding.srBooking.isRefreshing = true
                    try {
                        val responseBody =
                            String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        MotionToast.darkToast(
                            this,"Notification Display!",
                            errors.getString("message"),
                            MotionToastStyle.INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                        binding.srBooking.isRefreshing = false
                    } catch (e: Exception) {
                        MotionToast.darkToast(
                            this,"Notification Display!",
                            e.message.toString(),
                            MotionToastStyle.INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                        binding.srBooking.isRefreshing = false
                    }

                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer " + sharedPreferences.getString("token", null);
                return headers
            }
        }
        queue!!.add(stringRequest)


    }


    fun cancelBookingGym(id: String) {
//        binding.srBooking.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, MemberApi.BATALGYM + id, Response.Listener { response ->
                var jo = JSONObject(response.toString())
//                var history = arrayListOf<HistoryBookingClass>()
                if (jo.getJSONObject("data") != null) {
                    MotionToast.darkToast(
                        this, "Notification Booking!",
                        "Succesfully cancel booking",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                    val intent = Intent(this@BookingGymActivity, HomeActivity::class.java)
                    startActivity(intent)
                }else {
                    MotionToast.darkToast(
                        this, "Notification Booking!",
                        "Failed cancel booking",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                }

            }, Response.ErrorListener { error ->
                binding.srBooking.isRefreshing = true
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    MotionToast.darkToast(
                        this,"Notification Booking!",
                        errors.getString("message"),
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                    binding.srBooking.isRefreshing = false
                } catch (e: Exception){
                    MotionToast.darkToast(
                        this,"Notification Booking!",
                        e.message.toString(),
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
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
