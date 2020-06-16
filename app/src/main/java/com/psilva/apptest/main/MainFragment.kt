package com.psilva.apptest.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.psilva.apptest.R


import com.psilva.apptest.adapters.ListAdapter
import com.psilva.apptest.databases.enums.DatabaseEnum
import com.psilva.apptest.databases.interfaces.IPerformanceTestListener
import kotlinx.android.synthetic.main.main_fragment.view.*

class MainFragment : Fragment(), IPerformanceTestListener {

    private lateinit var _adapter: ListAdapter
    private lateinit var _viewModel : MainViewModel
    private lateinit var _view : View
    private lateinit var _context : Context


    companion object {
        fun newInstance() = MainFragment()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _view = inflater.inflate(R.layout.main_fragment, container, false)

        _viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initComponents()

        return _view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }



    override fun onPerformanceTestStart() {
        //disable config bottom menu and refresh button
        disableButtons()
        //show some loading
        showProgressBar()
    }

    override fun onPerformanceTestEnd() {
        //hide loading
        hideProgressBar()
        //enable config bottom menu and refresh button
        enableButtons()
    }





    private fun disableButtons() {
        _view.fbRefreshScan.isEnabled = false
        _view.bottomAppBar.navigationIcon = null
    }

    private fun enableButtons() {
        _view.fbRefreshScan.isEnabled = true
        _view.bottomAppBar.navigationIcon = resources.getDrawable(R.drawable.ic_menu_white_24dp)
    }

    private fun showProgressBar() {
        _view.pbDatabasesTest.visibility = View.VISIBLE
        _view.pbDatabasesTest.show()
    }

    private fun hideProgressBar() {
        _view.pbDatabasesTest.visibility = View.INVISIBLE
        _view.pbDatabasesTest.hide()
    }




    private fun initComponents() {
        setHasOptionsMenu(true)

        _view.fbRefreshScan.setOnClickListener { _viewModel.fetchData(_context) }

        _view.bottomAppBar.setNavigationOnClickListener { showBottomSheetMenu() }

        _view.rvResultList.layoutManager = LinearLayoutManager(_context)

        _adapter = ListAdapter(_context, _viewModel.getDatabaseListToTest())
        _view.rvResultList.adapter = _adapter// set adapter

        showBottomSheetMenu()

        _viewModel.setPerformanceTestListener(this)

        _viewModel.getData().observe(viewLifecycleOwner, Observer {
            refreshList()
        })
    }

    private fun showBottomSheetMenu() {
        //https://github.com/afollestad/material-dialogs/blob/master/documentation/BOTTOMSHEETS.md

        MaterialDialog(_context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.menu_options_title)
            customView(R.layout.bottom_sheet_custom_view, scrollable = true, horizontalPadding = true)
            positiveButton(R.string.alert_dialog_submit_button) { dialog ->
                submitParameters(dialog.getCustomView())
            }
            negativeButton(android.R.string.cancel)

            bindValues(getCustomView())
        }
    }

    private fun bindValues(customView: View) {
        bindQuantityTest(customView)
        bindQuantityData(customView)
        bindRunTypes(customView)
        bindDatabases(customView)
    }

    // bind quantity data to test
    private fun bindQuantityData(customView: View) {
        val testRunQtyData: EditText = customView.findViewById(R.id.etTestRunQtyData)

        testRunQtyData.setText(_viewModel.getQuantityTestData().toString())
    }

    // bind quantity tests to do
    private fun bindQuantityTest(customView: View) {
        val testRunQty: EditText = customView.findViewById(R.id.etTestRunQty)

        testRunQty.setText(_viewModel.getQuantityTest().toString())
    }

    // bind databases to test
    private fun bindDatabases(customView: View) {

        val cbDatabaseRoomChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRoom)
        val cbDatabaseRealmChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRealm)

        _viewModel.getDatabaseListToTest().forEach {
            when(it.key) {
                DatabaseEnum.ROOM -> cbDatabaseRoomChecked.isChecked = true
                DatabaseEnum.REALM -> cbDatabaseRealmChecked.isChecked = true
            }
        }
    }

    // bind test Type
    private fun bindRunTypes(customView: View) {
        val testRunType: Spinner = customView.findViewById(R.id.spTestRunType)
        val testTypeList = _viewModel.getDatabaseTestTypeList()

        testRunType.adapter = ArrayAdapter(_context, android.R.layout.simple_spinner_item, testTypeList)
    }

    private fun submitParameters(customView: View) {
        val testRunQty: EditText = customView.findViewById(R.id.etTestRunQty)
        val testRunQtyData: EditText = customView.findViewById(R.id.etTestRunQtyData)

        val testType: Spinner = customView.findViewById(R.id.spTestRunType)

        val cbDatabaseRoomChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRoom)
        val cbDatabaseRealmChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRealm)

        var databaseList = _viewModel.getDatabaseListToTest()
        var databaseListToAdd = mutableListOf<DatabaseEnum>()



        if(cbDatabaseRoomChecked.isChecked && !databaseList.contains(DatabaseEnum.ROOM)) {
            databaseListToAdd.add(DatabaseEnum.ROOM)
        }

        if(!cbDatabaseRoomChecked.isChecked && databaseList.contains(DatabaseEnum.ROOM)) {
            databaseList.remove(DatabaseEnum.ROOM)
        }


        if(cbDatabaseRealmChecked.isChecked && !databaseList.contains(DatabaseEnum.REALM)) {
            databaseListToAdd.add(DatabaseEnum.REALM)
        }

        if(!cbDatabaseRealmChecked.isChecked && databaseList.contains(DatabaseEnum.REALM)) {
            databaseList.remove(DatabaseEnum.REALM)
        }

        _viewModel.submitParameters(testRunQty.text.toString().toLong(), testRunQtyData.text.toString().toLong(), testType.selectedItem.toString(), databaseListToAdd)

        refreshList()
    }

    // method to refresh the list, only notify data set of changes, not need to instantiate a new adapter
    private fun refreshList() {
        _adapter.notifyDataSetChanged()
        _view.rvResultList.adapter!!.notifyDataSetChanged()
    }
}
