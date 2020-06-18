package com.psilva.apptest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psilva.apptest.R
import com.psilva.apptest.main.ResultDataModel
import com.psilva.android.databaseperformance.databases.enums.DatabaseEnum
import kotlinx.android.synthetic.main.list_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class ListAdapter(context: Context, private val result: LinkedHashMap<DatabaseEnum, ResultDataModel>?) : RecyclerView.Adapter<ListAdapter.ViewHodler>() {

    private val mContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ListAdapter.ViewHodler {
        return ViewHodler(LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false))
    }

    override fun getItemCount(): Int {
        if(result != null) {
            return result.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHodler, position: Int) {
        val model = result?.values?.elementAt(position)
        if(model != null) {
            holder.database!!.text = model.database.toString()
            holder.databaseLastRun!!.text = model.databaseLastRun.toString("yyyy-MM-dd HH:mm:ss")
            holder.databaseLastRunDurationCreate!!.text = model.databaseLastRunDurationCreate.toString()
            holder.databaseLastRunDurationRead!!.text = model.databaseLastRunDurationRead.toString()
            holder.databaseLastRunDurationUpdate!!.text = model.databaseLastRunDurationUpdate.toString()
            holder.databaseLastRunDurationDelete!!.text = model.databaseLastRunDurationDelete.toString()
        }
    }

    inner class ViewHodler(view : View) : RecyclerView.ViewHolder(view) {
        val database = view.tvLabelDatabaseName
        var databaseLastRun = view.tvDateLastRun
        var databaseLastRunDurationCreate = view.tvLastRunDurationCreate
        var databaseLastRunDurationRead = view.tvLastRunDurationRead
        var databaseLastRunDurationUpdate = view.tvLastRunDurationUpdate
        var databaseLastRunDurationDelete = view.tvLastRunDurationDelete
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}