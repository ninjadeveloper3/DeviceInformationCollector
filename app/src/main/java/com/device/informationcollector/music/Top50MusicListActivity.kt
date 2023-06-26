package com.device.informationcollector.music

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatgpt.masterclass.datacollectionlibrary.music.adapter.Top50SongsListAdapter
import com.device.deviceinformationlibrary.DataCollection
import com.device.deviceinformationlibrary.models.MusicSong
import com.device.informationcollector.databinding.ActivityTop50MusicListBinding
import com.device.informationcollector.dialog.DialogUtils
import com.device.informationcollector.permission.PermissionCheckers

class Top50MusicListActivity : AppCompatActivity() {
    private lateinit var musicList: MutableList<MusicSong>
    private lateinit var top50List: List<MusicSong>
    private lateinit var adapter: Top50SongsListAdapter
    private lateinit var binding: ActivityTop50MusicListBinding

    private lateinit var permissionCheckers: PermissionCheckers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTop50MusicListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initStoragePermission()

    }

    private fun initViews() {
        permissionCheckers = PermissionCheckers(this)
        musicList = ArrayList<MusicSong>()
        top50List = ArrayList<MusicSong>()
    }

    private fun initStoragePermission() {
        if (permissionCheckers.isPermissionGranted(PermissionCheckers.EXTERNAL_STORAGE)) {
            songsList()
        } else {
            permissionCheckers.requestPermission(
                PermissionCheckers.EXTERNAL_STORAGE,
                PermissionCheckers.EXTERNAL_STORAGE_CODE
            )
        }
    }


    private fun songsList() {
        musicList = DataCollection.getMusicCollection(this)
        top50List = DataCollection.getTop50MostPlayedSongs(musicList)

        binding.songsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Top50SongsListAdapter(musicList)
        binding.songsRecyclerView.adapter = adapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionCheckers.EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PermissionCheckers.GRANTED) {
                songsList()
            } else {
                if (!shouldShowRequestPermissionRationale(PermissionCheckers.EXTERNAL_STORAGE)) {
                    DialogUtils.goToSystemLocationSetting(
                        this, PermissionCheckers.EXTERNAL_STORAGE_CUSTOM_DIALOG_MESSAGE
                    )
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.EXPAND_STATUS_BAR
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            songsList()
        }
    }

}