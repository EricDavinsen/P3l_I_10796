package com.example.p3l_i_10796.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.p3l_i_10796.AddIzinInstruktur
import com.example.p3l_i_10796.IzinInstrukturActivity
import com.example.p3l_i_10796.R
import com.example.p3l_i_10796.models.IzinInstruktur
import org.w3c.dom.Text
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class IzinInstrukturAdapter(private var permissions: List<IzinInstruktur>, context: Context): RecyclerView.Adapter<IzinInstrukturAdapter.ViewHolder>()
{
    private val context: Context

    init {
        this.context = context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_izin_instruktur_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: IzinInstrukturAdapter.ViewHolder, position: Int) {
        val data = permissions[position]
        holder.tvKeterangan.text = "Keterangan: ${data.KETERANGAN_IZIN}"
        holder.tvTanggalIzin.text = "Tanggal Izin: ${data.TANGGAL_IZIN}"
        holder.tvMelakukan.text = "Tanggal Melakukan Izin: ${data.TANGGAL_PENGAJUAN_IZIN}"
        holder.tvStatus.text = "${data.STATUS_IZIN} - ${data.TANGGAL_KONFIRMASI_IZIN}"
        if(holder.tvStatus.text == "null - null") {
            holder.tvStatus.text = "Belum dikonfirmasi"
        }
        holder.cvpermission.setOnClickListener{
            Toast.makeText(context,data.KETERANGAN_IZIN, Toast.LENGTH_SHORT).show()
        }
        holder.tvInstrukturPengganti.text = "Instruktur Pengganti: ${data.NAMA_INSTRUKTUR_PENGGANTI}"
    }

    override fun getItemCount(): Int {
        return permissions.size
    }

//    fun setPermissionList(permissionss: Array<IzinInstruktur>){
//        this.permissions = permissionss.toList()
//    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var tvKeterangan: TextView
        var tvTanggalIzin: TextView
        var tvMelakukan: TextView
        var tvStatus: TextView
        var tvInstrukturPengganti: TextView
        var cvpermission: CardView

        init {
            tvKeterangan = view.findViewById(R.id.text_keterangan)
            tvTanggalIzin = view.findViewById(R.id.text_tanggal_izin)
            tvMelakukan = view.findViewById(R.id.text_tanggal_melakukan_izin)
            tvStatus = view.findViewById(R.id.text_status)
            tvInstrukturPengganti = view.findViewById(R.id.text_instruktur_pengganti)
            cvpermission = view.findViewById(R.id.cv_izin)
        }
    }
}