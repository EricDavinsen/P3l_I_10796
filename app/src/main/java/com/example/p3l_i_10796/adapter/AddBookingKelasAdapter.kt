package com.example.p3l_i_10796.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.p3l_i_10796.AddBookingKelas
import com.example.p3l_i_10796.R
import com.example.p3l_i_10796.models.JadwalHarian
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.format.DateTimeFormatter

class AddBookingKelasAdapter (private var JadwalHarianList: List<JadwalHarian>, context: Context) :
    RecyclerView.Adapter<AddBookingKelasAdapter.ViewHolder>() {

    private var filteredJadwalHarianList: MutableList<JadwalHarian>
    private val context: Context

    init{
        filteredJadwalHarianList = ArrayList(JadwalHarianList)
        this.context=context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_jadwal_adapter, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredJadwalHarianList.size
    }

    fun setJadwalHarianList(JadwalHarianList: Array<JadwalHarian>){
        this.JadwalHarianList = JadwalHarianList.toList()
        filteredJadwalHarianList = JadwalHarianList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val JadwalHarian = JadwalHarianList[position]
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        holder.tvTanggalJadwalHarian.text = JadwalHarian.TANGGAL_JADWAL_HARIAN.toString()
        holder.tvNamaKelas.text = JadwalHarian.NAMA_KELAS.toString()
        holder.tvNamaInstruktur.text = JadwalHarian.NAMA_INSTRUKTUR.toString()
        holder.tvKeteranganJadwalHarian.text = JadwalHarian.STATUS_JADWAL_HARIAN.toString()
        holder.tvTarif.text = JadwalHarian.TARIF.toString()
        holder.tvHariJadwal.text = JadwalHarian.HARI_JADWAL_UMUM.toString()
        holder.cvJadwalHarian.setOnClickListener {

            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin booking kelas ini?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Iya") { _, _ ->
                    if (context is AddBookingKelas) {
                        context.getSharedPreferences("session", Context.MODE_PRIVATE)
                            .getString("id", null)
                            ?.let { it1 ->
                                context.bookingClass(
                                    it1,
                                    JadwalHarian.ID_KELAS,
                                    JadwalHarian.TANGGAL_JADWAL_HARIAN
                                )
                            }
                    }
                }
                .show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvNamaKelas: TextView
        var tvNamaInstruktur: TextView
        var tvKeteranganJadwalHarian: TextView
        var tvTarif: TextView
        var tvHariJadwal: TextView
        var tvTanggalJadwalHarian: TextView
        var cvJadwalHarian: CardView

        init{
            tvNamaKelas = itemView.findViewById(R.id.tv_NamaKelas)
            tvNamaInstruktur = itemView.findViewById(R.id.tv_NamaInstruktur)
            tvKeteranganJadwalHarian = itemView.findViewById(R.id.tv_KeteranganJadwalHarian)
            tvTarif = itemView.findViewById(R.id.tv_Tarif)
            tvHariJadwal = itemView.findViewById(R.id.tv_HariJadwal)
            tvTanggalJadwalHarian = itemView.findViewById(R.id.tv_tanggalHarian)
            cvJadwalHarian = itemView.findViewById(R.id.cv_jadwal)
        }
    }
}