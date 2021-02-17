package com.eit.brnnda.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eit.brnnda.R
import com.eit.brnnda.databinding.CartitemSampleBinding
import com.eit.brnnda.databinding.CheckoutSampleBinding
import com.eit.brnnda.databinding.SingleOrderLayoutBinding
import com.eit.brnnda.dataclass.Product
import java.util.*
import kotlin.collections.ArrayList

class SingleOrderAdapter(
        private val clickListenerPlus: (Product) -> Unit,
        private val clickListenerMinus: (Product) -> Unit,
        private val clickListenerDelete: (Product) -> Unit


) : RecyclerView.Adapter<SingleViewHolder>(), Filterable {

    private val activeUserList = ArrayList<Product>()
    fun setList(activeUserData: List<Product>) {
        activeUserList.clear()
        activeUserList.addAll(activeUserData)
    }

    var filterList: List<Product>

    init {
        this.filterList = activeUserList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SingleOrderLayoutBinding = DataBindingUtil.inflate(view, R.layout.single_order_layout, parent, false)


        return SingleViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
        val myPosition = filterList[itemCount - position - 1]
        holder.bind(myPosition, clickListenerPlus, clickListenerMinus, clickListenerDelete)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    filterList = activeUserList //ekhane filter na kore main list ke, filter list a add kore
                } else {
                    val resultList = ArrayList<Product>()
                    for (row in activeUserList) {

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
                filterList = results!!.values as List<Product>
                notifyDataSetChanged()
            }
        }
    }

}

class SingleViewHolder(private val binding: SingleOrderLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(data: Product, clickListenerPlus: (Product) -> Unit, clickListenerMinus: (Product) -> Unit, clickListenerDelete: (Product) -> Unit) {

        binding.tvName.text = data.name


        binding.tvPriceId.text ="${data.qty}X ৳${data.price} =৳${data.qty*data.price}"


        binding.tvName.setOnClickListener {
            clickListenerPlus(data)
        }

        binding.tvName.setOnClickListener {
            clickListenerMinus(data)
        }

        binding.tvName.setOnClickListener {
            clickListenerDelete(data)
        }


    }


}