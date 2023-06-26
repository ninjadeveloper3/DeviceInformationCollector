package com.device.informationcollector.userapps
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.device.deviceinformationlibrary.models.InstalledUserAppsModel
import com.device.informationcollector.databinding.ActivityInstalledUserAppsBinding
import com.device.informationcollector.userapps.adapter.InstalledUserAppsAdapter

class InstalledUserAppsActivity : AppCompatActivity() {
    private var list: MutableList<InstalledUserAppsModel> = ArrayList<InstalledUserAppsModel>()
    private lateinit var adapter: InstalledUserAppsAdapter
    private lateinit var binding: ActivityInstalledUserAppsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstalledUserAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the package manager
        val packageManager: PackageManager = packageManager

        // Get a list of all installed applications
        val installedApps: List<ApplicationInfo> =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        // Iterate through the list and filter out the system apps
        val userInstalledApps: MutableList<ApplicationInfo> = mutableListOf()
        for (appInfo in installedApps) {
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                userInstalledApps.add(appInfo)
            }
        }

        // Do something with the user-installed apps
        for (appInfo in userInstalledApps) {
            val appName = packageManager.getApplicationLabel(appInfo)
            val packageName = appInfo.packageName
            list.add(InstalledUserAppsModel(appName.toString(), packageName.toString()))
            // Perform any desired operations with the app name and package name
            println("AppNameeee: $appName, Package Name: $packageName")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InstalledUserAppsAdapter(list)
        binding.recyclerView.adapter = adapter

    }
}