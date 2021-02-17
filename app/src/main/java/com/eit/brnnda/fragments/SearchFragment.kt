package com.eit.brnnda.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eit.brnnda.Adapter.HotNewTrendingSalesAdapter

import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentSearchBinding
import com.eit.brnnda.dataclass.ProductDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var adapter: HotNewTrendingSalesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = activity?.run {
            val dao = RoomDatabaseAbstract.invoke(application).getCartDao
            repository = NetworkRepository(dao)
            factory = BaseViewModelFactory(repository)
            ViewModelProvider(this, factory).get(MyViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.myViewModel = vm
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.textviewItemFoundId.visibility=View.VISIBLE
        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })


        try {


        } catch (e: Exception) {

        }
        initFlashDealRecyclerView()

        binding.searchViewButtonId.setOnClickListener {
            val text = binding.editTextSearchviewid.text.toString()

            if (text != null) {
                binding.progressbar.visibility=View.VISIBLE
                binding.searchViewButtonId.visibility=View.INVISIBLE
                displayFlashDealItemList(text)
            } else {
                binding.progressbar.visibility=View.GONE
                binding.searchViewButtonId.visibility=View.VISIBLE
                requireContext().MyToast("Please input something")
            }
        }


    }


    private fun initFlashDealRecyclerView() {
        binding.reyclerViewSearchId.layoutManager = GridLayoutManager(context, 1)
        adapter = HotNewTrendingSalesAdapter { selected: ProductDataItem -> productClick(selected) }
        binding.reyclerViewSearchId.adapter = adapter

    }


    private fun displayFlashDealItemList(text: String) {
        vm.searchApi(decodedStringKey, text).observe(viewLifecycleOwner, Observer {
            binding.progressbar.visibility=View.INVISIBLE
            binding.progressbar.visibility=View.GONE
            binding.searchViewButtonId.visibility=View.VISIBLE

            if (it.isNotEmpty()){

                adapter.setList(it)
                adapter.notifyDataSetChanged()
                binding.textviewItemFoundId.visibility=View.INVISIBLE
                binding.textviewItemFoundId.visibility=View.GONE

            }else{
                adapter.setList(it)
                adapter.notifyDataSetChanged()
                binding.textviewItemFoundId.visibility=View.VISIBLE
            }

        })
    }

    private fun productClick(selected: ProductDataItem) {
        val bundle = Bundle()
        bundle.putString("SLUG", selected.slug)
        findNavController().navigate(R.id.action_searchFragment_to_productFragment, bundle)
    }


}