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
import com.eit.brnnda.dataclass.SubCategoryDataItem
import java.util.*
import kotlin.collections.ArrayList

class SubCategoryAdapter(private val clickListener: (SubCategoryDataItem) -> Unit) : RecyclerView.Adapter<SubCategoryViewHolder>(), Filterable {

    private val dataList = ArrayList<SubCategoryDataItem>()
    fun setList(data: List<SubCategoryDataItem>) {
        dataList.clear()
        dataList.addAll(data)
    }

    var filterList: List<SubCategoryDataItem>

    init {
        this.filterList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val view: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: SampleCatLayoutBinding =
            DataBindingUtil.inflate(view, R.layout.sample_cat_layout, parent, false)


        return SubCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
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
                    val resultList = ArrayList<SubCategoryDataItem>()
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
                filterList = results!!.values as List<SubCategoryDataItem>
                notifyDataSetChanged()
            }
        }
    }

}

class SubCategoryViewHolder(private val binding: SampleCatLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("SetTextI18n")
    fun bind(
        data: SubCategoryDataItem,
        clickListener: (SubCategoryDataItem) -> Unit

    ) {
        binding.tvCatName.text = data.name




        binding.imageViewId.setOnClickListener {
            clickListener(data)
        }



    }


}