package com.example.p3l_i_10796

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.api.InstrukturApi
import com.example.p3l_i_10796.api.LoginApi
import com.example.p3l_i_10796.databinding.FragmentInstrukturProfilBinding
import com.example.p3l_i_10796.models.Auth
import com.example.p3l_i_10796.models.Instruktur
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class InstrukturProfilFragment : Fragment() {
    private var _binding: FragmentInstrukturProfilBinding? = null

    private val binding get() = _binding!!

    private var queue: RequestQueue? = null
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstrukturProfilBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedPreferences = (activity as HomeActivity).getSharedPreferences()

        val id = sharedPreferences.getInt("id", 0)

        queue = Volley.newRequestQueue(activity)

        getProfileById(id)

        binding.btnAktivitas.setOnClickListener{
            val Intent = Intent(activity, HistoriAktivitasInstrukturActivity::class.java)
            startActivity(Intent)
        }

        binding.btnLogout.setOnClickListener{
            logout()
        }
    }

    private fun getProfileById(id: Int){

        val stringRequest: StringRequest = object :
            StringRequest(
                Method.GET, InstrukturApi.GETDATAINSTRUKTUR + id,
                { response ->
                    val jsonObject = JSONObject(response.toString())
                    val json = jsonObject.getJSONObject("data")
                    val Instruktur = Gson().fromJson(json.toString(), Instruktur::class.java)

                    binding!!.nama.setText("Nama : "  +  Instruktur.NAMA_INSTRUKTUR)
                    binding!!.Email.setText("Email : " + Instruktur.EMAIL_INSTRUKTUR)
                    binding!!.jenisKelamin.setText("Jenis Kelamin : " + Instruktur.JENIS_KELAMIN_INSTRUKTUR)
                    binding!!.noTelepon.setText("No Telepon  : " + Instruktur.TELEPON_INSTRUKTUR)
                    binding!!.tglLahir.setText("Tanggal Lahir : " + Instruktur.TANGGAL_LAHIR_INSTRUKTUR)
                    binding!!.waktuTerlambat.setText("Akumulasi Terlambat :  " + Instruktur.JUMLAH_TERLAMBAT)
                    if(Instruktur.JUMLAH_TERLAMBAT.isNullOrEmpty()){
                        binding!!.waktuTerlambat.setText("Akumulasi Terlambat :  0")
                    }

                    FancyToast.makeText(requireActivity(),"Menampilkan profil instruktur", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                },
                Response.ErrorListener{ error ->
                    try{
                        val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errors = JSONObject(responseBody)
                        FancyToast.makeText(
                            requireActivity(),
                            errors.getString("message"),
                            FancyToast.LENGTH_SHORT, FancyToast.INFO, false
                        ).show()
                    } catch (e: Exception){
                        FancyToast.makeText(requireActivity(), e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()
                headers["Authorization"] = "Bearer " + sharedPreferences.getString("token",null);
                headers["Accept"] = "application/json"
                return headers
            }
        }
        queue!!.add(stringRequest)

    }

    private fun logout(){
        val auth = Auth(
            "",
            "")

        val stringRequest: StringRequest =
            object : StringRequest(Request.Method.POST, LoginApi.LOGOUT_URL, Response.Listener { response ->
                val gson = Gson()
                var user_logout = gson.fromJson(response, Auth::class.java)


                if(user_logout != null) {

                    val intent = Intent(activity, MainActivity::class.java)
                    FancyToast.makeText(context as Activity,"Berhasil Logout",
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,false).show()
                    sharedPreferences.edit()
                        .putInt("id",-1)
                        .putString("id", null)
                        .putString("role",null)
                        .putString("token",null)
                        .apply()
                    startActivity(intent)
                }
                else {
                    FancyToast.makeText(context as Activity,"Gagal Logout",
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,false).show()
                }
                return@Listener
            }, Response.ErrorListener { error ->
                try {
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(context as Activity, errors.getString("message"), FancyToast.LENGTH_LONG, FancyToast.INFO, false).show()
                }catch (e: Exception) {
                    FancyToast.makeText(context as Activity, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer " + sharedPreferences.getString("token",null);
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(auth)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8;"
                }
            }
        queue!!.add(stringRequest)
    }

}