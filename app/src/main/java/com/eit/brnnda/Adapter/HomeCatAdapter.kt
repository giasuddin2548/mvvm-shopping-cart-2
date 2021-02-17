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
import com.eit.brnnda.databinding.SampleCatLayoutBinding
import com.eit.brnnda.databinding.SampleHomeCatLayoutBinding
import com.eit.brnnda.dataclass.CategoryDataItem
import java.util.*
import kotlin.collections.ArrayList

class HomeCatAdapter(
    private val clickListener: (CategoryDataItem) -> Unit


) : RecyclerView.Adapter<HomeCatViewHolder>(), Filterable {

    private val dataList = ArrayList<CategoryDataItem>()
    fun setList(data: List<CategoryDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<CategoryDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCatViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleHomeCatLayoutBinding = DataBindingUtil.inflate(view, R.layout.sample_home_cat_layout, parent, false)


        return HomeCatViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: HomeCatViewHolder, position: Int) {
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

class HomeCatViewHolder(private val binding: SampleHomeCatLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(
        data: CategoryDataItem,
        clickListener: (CategoryDataItem) -> Unit

    ) {
        binding.tvCatName.text = data.name



        val url: String = Constent.decodedStringURL +"/assets/images/categories/"

        Glide.with(itemView)
                .load(url+data.photo)
                .centerCrop()
                .placeholder(R.mipmap.brnnda)
                .into(binding.imageViewId)


        binding.imageViewId.setOnClickListener {
            clickListener(data)
        }



    }


}