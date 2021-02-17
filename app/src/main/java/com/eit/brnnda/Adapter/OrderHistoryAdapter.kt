package com.eit.brnnda.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eit.brnnda.R
import com.eit.brnnda.databinding.SampleCatLayoutBinding
import com.eit.brnnda.databinding.SampleOrderHistoryBinding
import com.eit.brnnda.dataclass.OrderHistoryDataItem
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(
    private val clickListener: (OrderHistoryDataItem) -> Unit


) : RecyclerView.Adapter<OrderHistoryViewHolder>(), Filterable {

    private val dataList = ArrayList<OrderHistoryDataItem>()
    fun setList(data: List<OrderHistoryDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<OrderHistoryDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleOrderHistoryBinding =
            DataBindingUtil.inflate(view, R.layout.sample_order_history, parent, false)


        return OrderHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
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
                    val resultList = ArrayList<OrderHistoryDataItem>()
                    for (row in dataList) {

                        val name = row.order_number.toLowerCase().toString()

                        if (name.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row);
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
                filterList = results!!.values as List<OrderHistoryDataItem>
                notifyDataSetChanged()
            }
        }
    }

}

class OrderHistoryViewHolder(private val binding: SampleOrderHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(
        data: OrderHistoryDataItem,
        clickListener: (OrderHistoryDataItem) -> Unit

    ) {
        binding.textViewVoucherId.text ="Order#"+data.order_number
        binding.textviewMyOrderDateid.text = data.created_at
        binding.tvOrderTotalMyOrder.text = "৳${data.pay_amount}"
        binding.statusButton.text = data.status


        binding.cvPaymentSummerDetail.setOnClickListener {
            clickListener(data)
        }



    }


}