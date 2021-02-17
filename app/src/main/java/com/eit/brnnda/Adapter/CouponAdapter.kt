package com.eit.brnnda.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eit.brnnda.R
import com.eit.brnnda.databinding.SampleCouponLayoutBinding
import com.eit.brnnda.dataclass.CouponDataItem
import java.util.*
import kotlin.collections.ArrayList

class CouponAdapter(private val clickListener: (CouponDataItem) -> Unit) : RecyclerView.Adapter<CouponViewHolder>(), Filterable {

    private val dataList = ArrayList<CouponDataItem>()
    fun setList(data: List<CouponDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<CouponDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleCouponLayoutBinding =
            DataBindingUtil.inflate(view, R.layout.sample_coupon_layout, parent, false)


        return CouponViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val myPosition = filterList[itemCount - position - 1]
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
                    val resultList = ArrayList<CouponDataItem>()
                    for (row in dataList) {

                        val name = row.code.toLowerCase(Locale.ROOT)

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
                filterList = results!!.values as List<CouponDataItem>
                notifyDataSetChanged()
            }
        }
    }

}

class CouponViewHolder(private val binding: SampleCouponLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: CouponDataItem,
        clickListener: (CouponDataItem) -> Unit

    ) {
        if (data.type==1){
            binding.tvCouponTypeId.text = "৳"
        }else if (data.type==0){
            binding.tvCouponTypeId.text = "%"
        }
        binding.tvAmountOrPercentId.text=data.price.toString()

//        binding.tvCode.text = data.code
//        binding.tvCode.text = data.code


        binding.itemclick.setOnClickListener {

               clickListener(data)

        }



    }


}