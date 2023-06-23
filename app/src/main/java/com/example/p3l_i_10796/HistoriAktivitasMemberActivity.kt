package com.example.p3l_i_10796

import android.content.Context
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
import com.example.p3l_i_10796.adapter.HistoriAktivitasMemberAdapter
import com.example.p3l_i_10796.api.MemberApi
import com.example.p3l_i_10796.databinding.ActivityHistoriAktivitasMemberBinding
import com.example.p3l_i_10796.models.AktivitasMember
import org.json.JSONObject
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.nio.charset.StandardCharsets

class HistoriAktivitasMemberActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityHistoriAktivitasMemberBinding
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoriAktivitasMemberBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE)

        val id = sharedPreferences.getString("id",null)
        queue = Volley.newRequestQueue(this)

        binding.srBooking.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener{
            if (id != null) {
                allData(id)
            }
        })
        if (id != null) {
            allData(id)
        }

//        binding.btnAdd.setOnClickListener {
//            val intent = Intent(this@PresensiMemberActivity, PresensiMemberActivity::class.java)
//            sharedPreferences.edit()
//                .putString("booking","yes")
//                .apply()
//            startActivity(intent)
//        }
    }

    private fun allData(id: String) {
        binding.srBooking.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, MemberApi.GET_HISTORI_URL + id, Response.Listener { response ->
                var jo = JSONObject(response.toString())
                var history = arrayListOf<AktivitasMember>()
                var id : Int = jo.getJSONArray("data").length()

                for(i in 0 until id) {
                    var data = AktivitasMember(
                        jo.getJSONArray("data").getJSONObject(i).getString("KODE_BOOKING_KELAS"),
                        jo.getJSONArray("data").getJSONObject(i).getString("NAMA_KELAS"),
                        jo.getJSONArray("data").getJSONObject(i).getString("NAMA_INSTRUKTUR"),
                        jo.getJSONArray("data").getJSONObject(i).getString("TANGGAL_JADWAL_HARIAN"),
                        jo.getJSONArray("data").getJSONObject(i).getString("TANGGAL_MELAKUKAN_BOOKING"),
                        jo.getJSONArray("data").getJSONObject(i).getString("WAKTU_PRESENSI_KELAS"),
                        jo.getJSONArray("data").getJSONObject(i).getString("STATUS_PRESENSI_KELAS")
                    )
                    history.add(data)
                }
                var data_array: Array<AktivitasMember> = history.toTypedArray()

                val layoutManager = LinearLayoutManager(this)
                val adapter : HistoriAktivitasMemberAdapter = HistoriAktivitasMemberAdapter(history,this)
                val rvPermission : RecyclerView = findViewById(R.id.rv_booking)

                rvPermission.layoutManager = layoutManager
                rvPermission.setHasFixedSize(true)
                rvPermission.adapter = adapter

                binding.srBooking.isRefreshing = false

                if (!data_array.isEmpty()) {
//                    Toast.makeText(this@JanjiTemuActivity, "Data Berhasil Diambil!", Toast.LENGTH_SHORT).show()
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

            }, Response.ErrorListener { error ->
                binding.srBooking.isRefreshing = true
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
//                    Toast.makeText(this@JanjiTemuActivity, errors.getString("message"), Toast.LENGTH_SHORT).show()
                    MotionToast.darkToast(
                        this,"Notification Display!",
                        errors.getString("message"),
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                    binding.srBooking.isRefreshing = false
                } catch (e: Exception){
//                    Toast.makeText(this@JanjiTemuActivity, e.message, Toast.LENGTH_SHORT).show()
                    MotionToast.darkToast(
                        this,"Notification Display!",
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