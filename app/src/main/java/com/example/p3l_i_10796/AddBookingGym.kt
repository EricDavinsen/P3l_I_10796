package com.example.p3l_i_10796

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.api.LoginApi
import com.example.p3l_i_10796.api.MemberApi
import com.example.p3l_i_10796.databinding.ActivityAddBookingGymBinding
import com.example.p3l_i_10796.models.BookingGym
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddBookingGym : AppCompatActivity() {
    companion object{
        private val SLOT_LIST = arrayOf(
            "07:00-09:00",
            "09:00-11:00",
            "11:00-13:00",
            "13:00-15:00",
            "15:00-17:00",
            "17:00-19:00",
            "19:00-21:00")
    }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityAddBookingGymBinding
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookingGymBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)

        val id = sharedPreferences.getString("id",null)
        queue = Volley.newRequestQueue(this)

        setExposedDropDownMenu()

        val cal = Calendar.getInstance()
        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)
        val myDay = cal.get(Calendar.DAY_OF_MONTH)

        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)

        binding.edTglBooking.setOnFocusChangeListener { view, b ->
            val datePicker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    var date: String = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                    binding.edTglBooking.setText(date)
                }, myYear, myMonth, myDay
            )
            if (b) {
                datePicker.show()
            } else {
                datePicker.hide()
            }
        }

        binding.btnCancel.setOnClickListener {
            val intent = Intent(this@AddBookingGym, BookingGymActivity::class.java)
            startActivity(intent)
        }

        binding.btnSave.setOnClickListener {
            if (id != null) {
                storeGym(id)
            }
        }
    }

    fun setExposedDropDownMenu(){
        val adapterslot: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.item_list_dropdown,R.id.dataList, SLOT_LIST)
        binding.edSlotWaktu.setAdapter(adapterslot)
    }

    private fun storeGym(id: String) {
        val booking = BookingGym(
            "",
            id,
            binding.layoutSlotWaktu.getEditText()?.getText().toString(),
            null,
            binding.layoutTanggalBooking.getEditText()?.getText().toString(),
            null,
            null,
        )

        val stringRequest : StringRequest = object:
            StringRequest(Method.POST, MemberApi.STORE_GYM, Response.Listener { response ->
                val gson = Gson()
                var booking_gym = gson.fromJson(response, BookingGym::class.java)


                if (booking_gym != null) {
                    MotionToast.darkToast(
                        this, "Notification Booking!",
                        "Succesfully booking gym",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                    val intent = Intent(this@AddBookingGym, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    MotionToast.darkToast(
                        this, "Notification Booking!",
                        "Failed booking gym",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                }
                return@Listener
            },
            Response.ErrorListener { error ->
                try  {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@AddBookingGym, errors.getString("message"),
                        Toast.LENGTH_LONG
                    ).show();
                } catch (e: java.lang.Exception) {
                    Toast.makeText(
                        this@AddBookingGym, e.message,
                        Toast.LENGTH_LONG
                    ).show();
                }
            }) {
            @kotlin.jvm.Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] =
                    "Bearer " + sharedPreferences.getString("token", null);
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