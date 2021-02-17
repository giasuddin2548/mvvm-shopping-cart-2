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
import com.eit.brnnda.databinding.CartitemSampleBinding
import com.eit.brnnda.dataclass.CartData
import java.util.*
import kotlin.collections.ArrayList

class CartItemAdapter(
        private val clickListenerPlus: (CartData) -> Unit,
        private val clickListenerMinus: (CartData) -> Unit,
        private val clickListenerDelete: (CartData) -> Unit


) : RecyclerView.Adapter<ClientMessageViewHolder>(), Filterable {

    private val activeUserList = ArrayList<CartData>()
    fun setList(activeUserData: List<CartData>) {
        activeUserList.clear()
        activeUserList.addAll(activeUserData)
    }

    var filterList: List<CartData>

    init {
        this.filterList = activeUserList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientMessageViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: CartitemSampleBinding = DataBindingUtil.inflate(view, R.layout.cartitem_sample, parent, false)


        return ClientMessageViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: ClientMessageViewHolder, position: Int) {
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
                    val resultList = ArrayList<CartData>()
                    for (row in activeUserList) {

                        val name = row.cartItemName.toLowerCase(Locale.ROOT)

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
                filterList = results!!.values as List<CartData>
                notifyDataSetChanged()
            }
        }
    }

}

class ClientMessageViewHolder(private val binding: CartitemSampleBinding) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(data: CartData, clickListenerPlus: (CartData) -> Unit, clickListenerMinus: (CartData) -> Unit, clickListenerDelete: (CartData) -> Unit) {
        binding.tvProductBrandId.text = "SKU:"+data.cartItemSlugName
        binding.tvProductNameId.text = data.cartItemName
        binding.tvQuantityId.text = data.qty.toString()

        binding.tvPriceId.text ="${data.qty}X ৳${data.price} =৳${data.qty*data.price+(data.taxPrice*data.qty)}"

//qty*price+(taxPrice*qty)
        if (data.taxType==0){
            binding.tvTaxStatusId.text="Including tax"
        }else if (data.taxType==1){
            binding.tvTaxStatusId.text="Excluding ${data.taxValue}% tax"
        }

        val url: String = Constent.decodedStringURL+"/assets/images/thumbnails/"
        Glide.with(itemView).load(url+data.cartItemImage).centerCrop().placeholder(R.mipmap.brnnda).into(binding.imageViewProductId)




        binding.tvPlusId.setOnClickListener {
            clickListenerPlus(data)
        }

        binding.tvMinusId.setOnClickListener {
            clickListenerMinus(data)
        }

        binding.buttonItemDeleteId.setOnClickListener {
            clickListenerDelete(data)
        }


    }


}