package com.eit.brnnda.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent
import com.eit.brnnda.databinding.SampleTabLayoutBinding
import com.eit.brnnda.dataclass.ProductDataItem
import java.util.*
import kotlin.collections.ArrayList

class HotNewTrendingSalesAdapter(private val clickListener: (ProductDataItem) -> Unit) :
    RecyclerView.Adapter<HotNewTrendingSaleViewHolder>(), Filterable {

    private val dataList = ArrayList<ProductDataItem>()
    fun setList(data: List<ProductDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<ProductDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotNewTrendingSaleViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleTabLayoutBinding =
            DataBindingUtil.inflate(view, R.layout.sample_tab_layout, parent, false)


        return HotNewTrendingSaleViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: HotNewTrendingSaleViewHolder, position: Int) {
        val myPosition = filterList[position]
        holder.bind(myPosition, clickListener)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    filterList =
                        dataList //ekhane filter na kore main list ke, filter list a add kore
                } else {
                    val resultList = ArrayList<ProductDataItem>()
                    for (row in dataList) {

                        val name = row.name.toLowerCase(Locale.ROOT)

                        if (name.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }


                    }

                    ////


                    ///////
                    filterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results!!.values as List<ProductDataItem>
                notifyDataSetChanged()
            }
        }
    }

}

class HotNewTrendingSaleViewHolder(private val binding: SampleTabLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(
        data: ProductDataItem,
        clickListener: (ProductDataItem) -> Unit
    ) {
        binding.tvProductName.text = data.name

        binding.tvDiscountPrice.text = "$" + data.price
        binding.tvOriginalPrice.text = "$" + data.previous_price

        val url: String = Constent.decodedStringURL +"/assets/images/thumbnails/"

        Glide.with(itemView).load(url + data.thumbnail).centerCrop()
            .placeholder(R.mipmap.brnnda).into(binding.ivProductImageId)

        binding.itemClickProductId.setOnClickListener {
            clickListener(data)
        }

    }


}