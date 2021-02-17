package com.eit.brnnda.Adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringURL
import com.eit.brnnda.databinding.SampleCatLayoutBinding
import com.eit.brnnda.dataclass.CategoryDataItem
import java.util.*
import kotlin.collections.ArrayList

class CategoryAdapter(
    private val clickListener: (CategoryDataItem) -> Unit


) : RecyclerView.Adapter<CategoryViewHolder>(), Filterable {

    private val dataList = ArrayList<CategoryDataItem>()
    fun setList(data: List<CategoryDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<CategoryDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleCatLayoutBinding =
            DataBindingUtil.inflate(view, R.layout.sample_cat_layout, parent, false)


        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
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
                    val resultList = ArrayList<CategoryDataItem>()
                    for (row in dataList) {

                        val name = row.name.toLowerCase(Locale.ROOT)

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
                filterList = results!!.values as List<CategoryDataItem>
                notifyDataSetChanged()
            }
        }
    }

}

class CategoryViewHolder(private val binding: SampleCatLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(
        data: CategoryDataItem,
        clickListener: (CategoryDataItem) -> Unit

    ) {
        binding.tvCatName.text = data.name



        val url: String = "$decodedStringURL/assets/images/categories/"

        Glide.with(itemView)
                .load(url+data.photo)
                .centerCrop()
                .placeholder(R.mipmap.brnnda)
                .into(binding.imageViewId)

        binding.itemclick.setBackgroundColor(Color.parseColor("#8DFFFFFF"));


        binding.itemclick.setOnClickListener {
            clickListener(data)
        }



    }


}