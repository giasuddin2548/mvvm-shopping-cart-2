package com.eit.brnnda.Adapter
import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringURL
import com.eit.brnnda.databinding.SampleFeatersLayoutBinding
import com.eit.brnnda.dataclass.ProductDataItem
import java.util.*
import kotlin.collections.ArrayList

class FeaturedProductsAdapter(private val clickListener: (ProductDataItem) -> Unit,private val clickListenerAddtoCart: (ProductDataItem) -> Unit) : RecyclerView.Adapter<FeaturedProductsViewHolder>(), Filterable {

    private val dataList = ArrayList<ProductDataItem>()
    fun setList(data: List<ProductDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<ProductDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedProductsViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleFeatersLayoutBinding =
                DataBindingUtil.inflate(view, R.layout.sample_featers_layout, parent, false)


        return FeaturedProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: FeaturedProductsViewHolder, position: Int) {
        val myPosition = filterList[position]
        holder.bind(myPosition, clickListener, clickListenerAddtoCart)
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

class FeaturedProductsViewHolder(private val binding: SampleFeatersLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(data: ProductDataItem,
            clickListener: (ProductDataItem) -> Unit,
             clickAddToListener: (ProductDataItem) -> Unit
    ) {
        binding.tvProductName.text = data.name

        binding.tvDiscountPrice.text = "৳"+ data.price
        binding.tvOriginalPrice.text = "৳"+ data.previous_price

        binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        val url: String = "$decodedStringURL/assets/images/thumbnails/"

        Glide.with(itemView).load(url+data.thumbnail).centerCrop().placeholder(R.mipmap.brnnda).into(binding.ivProductImageId)



        binding.itemClickProductId.setOnClickListener {
            clickListener(data)
        }
        binding.tvAdd.setOnClickListener {
            clickAddToListener(data)
        }




    }


}