package com.eit.brnnda.fragments.Category

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
import com.eit.brnnda.Adapter.CategoryAdapter
import com.eit.brnnda.Adapter.ChildCategoryAdapter
import com.eit.brnnda.Adapter.SubCategoryAdapter
import com.eit.brnnda.Network.NetworkRepository
import com.eit.brnnda.R
import com.eit.brnnda.Utils.Constent.decodedStringKey
import com.eit.brnnda.Utils.MyToast
import com.eit.brnnda.databinding.FragmentCatBinding
import com.eit.brnnda.dataclass.CategoryDataItem
import com.eit.brnnda.dataclass.ChildCatDataItem
import com.eit.brnnda.dataclass.SubCategoryDataItem
import com.eit.brnnda.room_database.RoomDatabaseAbstract
import com.eit.brnnda.view_model.BaseViewModelFactory
import com.eit.brnnda.view_model.MyViewModel

class CatFragment : Fragment() {
    private lateinit var binding: FragmentCatBinding
    private lateinit var vm: MyViewModel
    private lateinit var repository: NetworkRepository
    private lateinit var factory: BaseViewModelFactory
    private lateinit var adapter: CategoryAdapter
    private lateinit var subAdapter: SubCategoryAdapter
    private lateinit var childCategoryAdapter: ChildCategoryAdapter

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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_cat, container, false
        )
        binding.myViewModel = vm
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        binding.progressbar.visibility=View.VISIBLE


        vm.message.observe(viewLifecycleOwner, Observer { myEvent ->
            myEvent.getContentIfNotHandled()?.let {
                requireContext().MyToast(it)
            }
        })
        initCategoryRecyclerView()
        initSubCategoryRecyclerView()

        initChildCategoryRecyclerView()
    }

    //Category start
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //Init RecyclerView
    private fun initCategoryRecyclerView() {
        binding.reyclerViewCatId.layoutManager = GridLayoutManager(context, 1)
        adapter = CategoryAdapter { selected: CategoryDataItem -> catClick(selected) }
        binding.reyclerViewCatId.adapter = adapter
        displayCategoryList()
    }


    private fun displayCategoryList() {
        vm.getAllCategoryData(decodedStringKey).observe(viewLifecycleOwner, Observer {

//            binding.progressbar.visibility=View.GONE
            adapter.setList(it)
            adapter.notifyDataSetChanged()

        })
    }

    //Category end
    ///////////////////////////////////////////////////////////////////////////////////////////////////


    private fun initSubCategoryRecyclerView() {
        binding.recyclerViewSubCatId.layoutManager = GridLayoutManager(context, 2)
        subAdapter = SubCategoryAdapter { selected: SubCategoryDataItem -> subCatClick(selected) }
        binding.recyclerViewSubCatId.adapter = subAdapter

    }


    private fun initChildCategoryRecyclerView() {
        binding.recyclerViewChildCatId.layoutManager = GridLayoutManager(context, 2)
        childCategoryAdapter =
            ChildCategoryAdapter { selected: ChildCatDataItem -> childClicked(selected) }
        binding.recyclerViewChildCatId.adapter = childCategoryAdapter

    }



    private fun childClicked(selected: ChildCatDataItem) {
        val bundle = Bundle()
        bundle.putString("SLUG", selected.slug)
        bundle.putString("NAME", selected.name)
        findNavController().navigate(R.id.action_catFragment_to_childCatWiseProductFragment, bundle)

    }


    private fun subCatClick(selected: SubCategoryDataItem) {

        if (selected.childcat_status == "1") {

            binding.tvChildeader.text = selected.name
            binding.progressbarChild.visibility = View.VISIBLE
            vm.getChildCategoryList(decodedStringKey, selected.id)
                .observe(viewLifecycleOwner, Observer { response ->

                    binding.progressbarChild.visibility = View.GONE
                    if (response == null) {
                        binding.tvNoChildCat.visibility = View.VISIBLE
                    } else {
                        binding.tvNoChildCat.visibility = View.INVISIBLE
                        childCategoryAdapter.setList(response)
                        childCategoryAdapter.notifyDataSetChanged()
                    }


                })

        } else if (selected.childcat_status == "0") {
            val bundle = Bundle()
            bundle.putString("SLUG", selected.slug)
            bundle.putString("NAME", selected.name)
            findNavController().navigate(R.id.action_catFragment_to_subCatWiseProductFragment, bundle)

        } else {

        }













    }

    private fun catClick(data: CategoryDataItem) {


        if (data.subcat_status == "1") {

            binding.tvSubheader.text = data.name
            binding.progressbar.visibility = View.VISIBLE
            vm.getSubCategoryList(decodedStringKey, data.id.toString())
                .observe(viewLifecycleOwner, Observer { response ->

                    binding.progressbar.visibility = View.GONE
                    if (response == null) {
                        binding.tvNoSubCat.visibility = View.VISIBLE
                    } else {
                        binding.tvNoSubCat.visibility = View.INVISIBLE
                        subAdapter.setList(response)
                        subAdapter.notifyDataSetChanged()
                    }


                })


        } else if (data.subcat_status == "0") {
            val bundle = Bundle()
            bundle.putString("SLUG", data.slug)
            bundle.putString("NAME", data.name)
            findNavController().navigate(R.id.action_catFragment_to_catWiseProductFragment, bundle)

        } else {

        }


    }


}