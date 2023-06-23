package com.chatgpt.masterclass.datacollectionlibrary.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.device.deviceinformationlibrary.models.MusicSong
import com.device.informationcollector.R

data class Top50SongsListAdapter(private val mList: List<MusicSong>) :
    RecyclerView.Adapter<Top50SongsListAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.top_50_songs_items_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val musicSong = mList[position]
        // sets the font name on textview
        holder.textView.text = musicSong.title

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.songNameTv)
    }

}
