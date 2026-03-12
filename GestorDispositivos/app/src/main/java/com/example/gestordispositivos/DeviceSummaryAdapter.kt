package com.example.gestordispositivos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Data class para la vista resumen por tipo de dispositivo.
 */
data class DeviceTypeSummary(
    val typeName: String,
    val count: Int,
    val iconResId: Int
)

/**
 * Adapter del RecyclerView que muestra el resumen de dispositivos
 * agrupados por tipo (Portátil, Móvil, Tablet, Servidor).
 */
class DeviceSummaryAdapter(
    private val items: List<DeviceTypeSummary>
) : RecyclerView.Adapter<DeviceSummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivTypeIcon: ImageView = view.findViewById(R.id.ivTypeIcon)
        val tvTypeName: TextView = view.findViewById(R.id.tvTypeName)
        val tvTypeCount: TextView = view.findViewById(R.id.tvTypeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.ivTypeIcon.setImageResource(item.iconResId)
        holder.tvTypeName.text = item.typeName
        holder.tvTypeCount.text = holder.itemView.context
            .getString(R.string.devices_count, item.count)
    }

    override fun getItemCount(): Int = items.size
}
