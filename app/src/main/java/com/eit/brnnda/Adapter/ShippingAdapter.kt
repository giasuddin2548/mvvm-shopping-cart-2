package com.eit.brnnda.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eit.brnnda.R
import com.eit.brnnda.dataclass.ShppingDataItem

class ShippingAdapter : RecyclerView.Adapter<ShippingAdapter.ShippingViewHolder>() {

    private var selected = -1
    private val dataList = ArrayList<ShppingDataItem>()
    fun setList(data: List<ShppingDataItem>) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_shipping_layout, parent, false)
        return ShippingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ShippingViewHolder, position: Int) {
        val myPosition = dataList[position]
        holder.textViewName.text=myPosition.title
        holder.textViewSubtitle.text=myPosition.subtitle

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
        fun onItemClick(view: View, position: Int, myPosition: ShppingDataItem)
    }

    inner class ShippingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView = itemView.findViewById(R.id.textNameId)
        var textViewSubtitle: TextView = itemView.findViewById(R.id.textNameSubtitileId)
        var mCheckBox: TextView = itemView.findViewById(R.id.selectedCartId)

    }

}

