package com.example.p3l_i_10796

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        getSupportActionBar()?.hide()

        var role = sharedPreferences.getString("role",null)


        if(role == "member"){
            setThatFragments(MemberFragment())
            nav_view.setOnItemSelectedListener {
                when(it){
                    R.id.nav_home ->{
                        setThatFragments(MemberFragment())
                    }

                    R.id.nav_profil->{
                        setThatFragments(MemberProfilFragment())
                    }
                }
                true
            }
        }

        if(role == "Manager Operasional"){
            setThatFragments(MOFragment())
            nav_view.setOnItemSelectedListener {
                when(it){
                    R.id.nav_home->{
                        setThatFragments(MOFragment())
                    }
                }
                true
            }
        }

        if(role == "instruktur"){
            setThatFragments(InstrukturFragment())
            nav_view.setOnItemSelectedListener {
                when(it){
                    R.id.nav_home->{
                        setThatFragments(InstrukturFragment())
                    }

                    R.id.nav_profil->{
                        setThatFragments(InstrukturProfilFragment())
                    }
                }
                true
            }
        }
    }

    private fun setThatFragments(fragment : Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.layoutFragment,fragment)
            commit()
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }
}