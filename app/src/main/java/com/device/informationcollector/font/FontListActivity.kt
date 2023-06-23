package com.device.informationcollector.font

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatgpt.masterclass.datacollectionlibrary.font.model.FontModel
import com.device.deviceinformationlibrary.DataCollection
import com.device.informationcollector.databinding.ActivityFontListBinding
import java.io.File

class FontListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFontListBinding
    private var list: MutableList<FontModel> = ArrayList<FontModel>()
    private lateinit var adapter: FontAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFontListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // call function from Datacollection class of device font list
        val ff: Array<File> = DataCollection.deviceFontList()

        // using for loop get font name
        for (i in 0 until ff.size) {
            list.add(FontModel(ff[i].name))
        }
        // this creates a vertical layout Manager
        binding.recyclerViewFont.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFont.setHasFixedSize(true)
        // This will pass the ArrayList to our Adapter
        adapter = FontAdapter(list)
        // Setting the Adapter with the recyclerview
        binding.recyclerViewFont.adapter = adapter
    }
}