package com.example.p3l_i_10796

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.p3l_i_10796.databinding.FragmentMemberBinding

class MemberFragment : Fragment() {
    private var _binding: FragmentMemberBinding? = null

    private val binding get() = _binding!!

    private var queue: RequestQueue? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = (activity as HomeActivity).getSharedPreferences()

        queue = Volley.newRequestQueue(activity)

        binding.btnBookingGym.setOnClickListener{
            val Intent = Intent(activity, BookingGymActivity::class.java)
            startActivity(Intent)
        }

        binding.btnBookingKelas.setOnClickListener{
            val Intent = Intent(activity, BookingKelasActivity::class.java)
            startActivity(Intent)
        }

        binding.btnHistoriMember.setOnClickListener{
            val Intent = Intent(activity, HistoriAktivitasMemberActivity::class.java)
            startActivity(Intent)
        }

    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            MemberFragment().apply {

            }
    }

}