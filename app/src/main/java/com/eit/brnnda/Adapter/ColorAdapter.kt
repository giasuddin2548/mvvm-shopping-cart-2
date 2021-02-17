package com.eit.brnnda.Adapter
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.brnnda.R

class ColorAdapter : RecyclerView.Adapter<ColorAdapter.SingleViewHolder>() {

    private var selected = -1
    private val dataList = ArrayList<String>()
    fun setList(data: List<String>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var onClick: OnItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickListener: OnItemClickListener) {
        this.onClick = mOnItemClickListener
    }
    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_color_layout, parent, false)
        return SingleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        val myPosition = dataList[position]
        holder.mlayout.setBackgroundColor(Color.parseColor(myPosition))

        if (selected == position) {
            holder.mCheckBox.text = "✓"
            holder.itemView.isSelected = true
        } else {
            holder.mCheckBox.text = ""
            holder.itemView.isSelected = false
        }
        if (onClick != null) {
            holder.itemView.setOnClickListener {
                onClick!!.onItemClick(holder.itemView, holder.adapterPosition, myPosition)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, myPosition: String)
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mlayout: LinearLayout = itemView.findViewById(R.id.sample_color_layoutId)
        var mCheckBox: TextView = itemView.findViewById(R.id.selectedColorId)

    }

}

