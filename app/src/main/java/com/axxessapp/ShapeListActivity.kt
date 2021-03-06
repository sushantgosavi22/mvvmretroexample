package com.axxessapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.axxessapp.Model.ResultModel
import com.axxessapp.adapters.ShapeAdapter
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.axxessapp.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.concurrent.TimeUnit


class ShapeListActivity : AppCompatActivity(), ShapeAdapter.OnShapeClickListener, ShapeListingViewModel.ApiCallBack {

    private var search: SearchView? = null
    var queryText: String = ""

    private lateinit var appStoreHomeViewModel: ShapeListingViewModel
    lateinit var binding: ActivityMainBinding
    var dataList: ArrayList<ShapeModelData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        appStoreHomeViewModel = ViewModelProviders.of(this).get(ShapeListingViewModel::class.java)
        binding.mainModelHome = appStoreHomeViewModel
        setRecyclerView(dataList)
    }


    private fun subscribeDataCallBack() {
        if(Utils.isInternetConnected(this)){
            showProgress()
            appStoreHomeViewModel.getImagesList(this,queryText,this)
        }else{
            Utils.showToast(this,getString(R.string.no_internet_connection))
        }
    }

    private fun setAdapter(list : List<ShapeModelData>) {
        dataList = ArrayList(list)
        if(list.isNotEmpty()){
            binding.emptyView.visibility = View.GONE
        }else{
            val text = if(queryText.isEmpty()){getString(R.string.search_for_result)}else{getString(R.string.no_record_found)+" $queryText ...."}
            binding.emptyView.visibility = View.VISIBLE
            binding.emptyView.text = text
        }
        shapeAdapter.setAppList(list)
    }


    private lateinit var shapeAdapter: ShapeAdapter

    private fun setRecyclerView(dataList: ArrayList<ShapeModelData>) {
        shapeAdapter = ShapeAdapter(this)
        val categoryLinearLayoutManager = GridLayoutManager(this,4)
        categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.root.recyclerShapeList.layoutManager = categoryLinearLayoutManager
        shapeAdapter.setAppList(dataList)
        binding.root.recyclerShapeList.adapter = shapeAdapter
    }

    private fun performSearch() {
        search?.let {
            if (queryText.isNotEmpty() && it.query.toString().equals(queryText, ignoreCase = true)) {
                subscribeDataCallBack()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            var menuItem: MenuItem? = menu?.findItem(R.id.search)
            menuItem?.let { menuItem->
                menuItem.isVisible = true
                search = menu?.findItem(R.id.search)?.actionView as SearchView
                search?.apply {
                    this.visibility = View.VISIBLE
                    RxSearchView.queryTextChanges(this)
                            .debounce(250, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                queryText =it.toString()
                                performSearch()
                            }
                    if(queryText.isNotEmpty()){
                        this.setQuery(queryText,false)
                    }
                }
            }
        }
        return true
    }
    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var model = ResultModel()
        model.data = dataList
        outState.putSerializable(Utils.LIST_DATA,model)
        outState.putString(Utils.QUERY,queryText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        var model =  savedInstanceState.getSerializable(Utils.LIST_DATA)
        if(model!=null && model is ResultModel){
            dataList =   model?.data
            setAdapter(dataList)
        }
        var queryText =  savedInstanceState.getString(Utils.QUERY)
        if(queryText?.isNotEmpty()==true) {
            this.queryText = queryText
        }
    }
    override fun onCallBack(success: Boolean?, data: List<ShapeModelData>, error: Throwable?) {
        error?.let {
            Utils.showToast(this,it.message)
        }
        hideProgress()
        setAdapter(data)
    }

    override fun onShapeClick(data: ShapeModelData?) {
        data?.let {
            var intent = Intent(this,ShapeDetailActivity::class.java)
            intent.putExtra(Utils.SHAPE_MODEL,it)
            startActivity(intent)
        }
    }
}
