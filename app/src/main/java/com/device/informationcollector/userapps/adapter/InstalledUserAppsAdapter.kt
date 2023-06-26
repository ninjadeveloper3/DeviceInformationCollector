package com.device.informationcollector.userapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.device.deviceinformationlibrary.models.InstalledUserAppsModel
import com.device.informationcollector.R

class InstalledUserAppsAdapter(private val mList: List<InstalledUserAppsModel>)
    : RecyclerView.Adapter<InstalledUserAppsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.installed_user_apps_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val installedSystemAppsModel = mList[position]
        // sets the font name on textview
        holder.textView.text = installedSystemAppsModel.appName

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.appsnameTv)
    }
}