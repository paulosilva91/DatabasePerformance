package com.psilva.apptest.main


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.Snackbar
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import com.psilva.android.databaseperformance.databases.enums.DatabaseOperationEnum
import com.psilva.android.databaseperformance.databases.interfaces.IPerformanceTestListener
import com.psilva.android.databaseperformance.databases.utils.AndroidPermissions
import com.psilva.apptest.R
import com.psilva.apptest.adapters.ListAdapter
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_export_csv_action -> {
                requestPermissionToWrite()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            AndroidPermissions.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> _viewModel.onExportCSVClicked()
        }
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

    override fun onPerformanceTestError(databaseEnum: DatabaseEnum, databaseOperationEnum: DatabaseOperationEnum, exception: Exception) {
        Snackbar.make(_view.rvResultList, String.format(getString(R.string.database_error_message), databaseOperationEnum, databaseEnum), Snackbar.LENGTH_LONG)
    }


    private fun disableButtons() {
        _view.fbRefreshScan.isEnabled = false
        _view.bottomAppBar.navigationIcon = null
    }

    private fun enableButtons() {
        _view.fbRefreshScan.isEnabled = true
        _view.bottomAppBar.navigationIcon = resources.getDrawable(R.drawable.ic_menu_white_24dp, null)
    }

    private fun showProgressBar() {
        _view.pbDatabasesTest.visibility = View.VISIBLE
        _view.pbDatabasesTest.show()
    }

    private fun hideProgressBar() {
        _view.pbDatabasesTest.visibility = View.INVISIBLE
        _view.pbDatabasesTest.hide()
    }

    private fun requestPermissionToWrite() {
        //Check permissions
        val permissionCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), AndroidPermissions.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
        } else {
            // got permission use it
            _viewModel.onExportCSVClicked()
        }
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
        bindQuantityData(customView)
        bindRunTypes(customView)
        bindDatabases(customView)
    }

    // bind quantity data to test
    private fun bindQuantityData(customView: View) {
        val testRunQtyData: EditText = customView.findViewById(R.id.etTestRunQtyData)

        testRunQtyData.setText(_viewModel.getQuantityTestData().toString())
    }

    // bind databases to test
    private fun bindDatabases(customView: View) {
        val cbDatabaseRoomChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRoom)
        val cbDatabaseRealmChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRealm)
        val cbDatabaseOrmLiteChecked: CheckBox = customView.findViewById(R.id.cbDatabaseOrmLite)
        val cbDatabaseCouchbaseChecked: CheckBox = customView.findViewById(R.id.cbDatabaseCouchbase)
        val cbDatabaseSQLiteChecked: CheckBox = customView.findViewById(R.id.cbDatabaseSqlite)
        val cbDatabaseGreenDaoChecked: CheckBox = customView.findViewById(R.id.cbDatabaseGreenDao)
        val cbDatabaseObjectBoxChecked: CheckBox = customView.findViewById(R.id.cbDatabaseObjectBox)

        _viewModel.getDatabaseListToTest().forEach {
            when(it.key) {
                DatabaseEnum.ROOM -> cbDatabaseRoomChecked.isChecked = true
                DatabaseEnum.REALM -> cbDatabaseRealmChecked.isChecked = true
                DatabaseEnum.ORMLITE -> cbDatabaseOrmLiteChecked.isChecked = true
                DatabaseEnum.COUCHBASE -> cbDatabaseCouchbaseChecked.isChecked = true
                DatabaseEnum.SQLITE -> cbDatabaseSQLiteChecked.isChecked = true
                DatabaseEnum.GREEN_DAO -> cbDatabaseGreenDaoChecked.isChecked = true
                DatabaseEnum.OBJECT_BOX -> cbDatabaseObjectBoxChecked.isChecked = true
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
        val testRunQtyData: EditText = customView.findViewById(R.id.etTestRunQtyData)

        val testType: Spinner = customView.findViewById(R.id.spTestRunType)

        val cbDatabaseRoomChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRoom)
        val cbDatabaseRealmChecked: CheckBox = customView.findViewById(R.id.cbDatabaseRealm)
        val cbDatabaseOrmLiteChecked: CheckBox = customView.findViewById(R.id.cbDatabaseOrmLite)
        val cbDatabaseCouchbaseChecked: CheckBox = customView.findViewById(R.id.cbDatabaseCouchbase)
        val cbDatabaseSQLiteChecked: CheckBox = customView.findViewById(R.id.cbDatabaseSqlite)
        val cbDatabaseGreenDaoChecked: CheckBox = customView.findViewById(R.id.cbDatabaseGreenDao)
        val cbDatabaseObjectBoxChecked: CheckBox = customView.findViewById(R.id.cbDatabaseObjectBox)


        _view.tvQuantityToTest.text = testRunQtyData.text.toString()
        _view.tvOperationType.text = testType.selectedItem.toString()


        val databaseList = _viewModel.getDatabaseListToTest()
        val databaseListToAdd = mutableListOf<DatabaseEnum>()



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

        if(cbDatabaseOrmLiteChecked.isChecked && !databaseList.contains(DatabaseEnum.ORMLITE)) {
            databaseListToAdd.add(DatabaseEnum.ORMLITE)
        }

        if(!cbDatabaseOrmLiteChecked.isChecked && databaseList.contains(DatabaseEnum.ORMLITE)) {
            databaseList.remove(DatabaseEnum.ORMLITE)
        }

        if(cbDatabaseCouchbaseChecked.isChecked && !databaseList.contains(DatabaseEnum.COUCHBASE)) {
            databaseListToAdd.add(DatabaseEnum.COUCHBASE)
        }

        if(!cbDatabaseCouchbaseChecked.isChecked && databaseList.contains(DatabaseEnum.COUCHBASE)) {
            databaseList.remove(DatabaseEnum.COUCHBASE)
        }


        if(cbDatabaseSQLiteChecked.isChecked && !databaseList.contains(DatabaseEnum.SQLITE)) {
            databaseListToAdd.add(DatabaseEnum.SQLITE)
        }

        if(!cbDatabaseSQLiteChecked.isChecked && databaseList.contains(DatabaseEnum.SQLITE)) {
            databaseList.remove(DatabaseEnum.SQLITE)
        }



        if(cbDatabaseGreenDaoChecked.isChecked && !databaseList.contains(DatabaseEnum.GREEN_DAO)) {
            databaseListToAdd.add(DatabaseEnum.GREEN_DAO)
        }

        if(!cbDatabaseGreenDaoChecked.isChecked && databaseList.contains(DatabaseEnum.GREEN_DAO)) {
            databaseList.remove(DatabaseEnum.GREEN_DAO)
        }


        if(cbDatabaseObjectBoxChecked.isChecked && !databaseList.contains(DatabaseEnum.OBJECT_BOX)) {
            databaseListToAdd.add(DatabaseEnum.OBJECT_BOX)
        }

        if(!cbDatabaseObjectBoxChecked.isChecked && databaseList.contains(DatabaseEnum.OBJECT_BOX)) {
            databaseList.remove(DatabaseEnum.OBJECT_BOX)
        }



        _viewModel.submitParameters(testRunQtyData.text.toString().toLong(), testType.selectedItem.toString(), databaseListToAdd)

        refreshList()
    }

    // method to refresh the list, only notify data set of changes, not need to instantiate a new adapter
    private fun refreshList() {
        _adapter.notifyDataSetChanged()
        _view.rvResultList.adapter!!.notifyDataSetChanged()
    }
}
