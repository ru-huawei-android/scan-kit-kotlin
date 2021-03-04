package com.huawei.scankit.kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.huawei.scankit.kotlin.frgments.BitmapFragment
import com.huawei.scankit.kotlin.frgments.CustomizedViewFragment
import com.huawei.scankit.kotlin.frgments.DefaultViewFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TabConfigurationStrategy {

    private lateinit var tabTitles: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)

        // TabLayout titles
        tabTitles = resources.getStringArray(R.array.tabs_title)

        viewPager.adapter = SearchViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager, this).attach()
        if (checkPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, CAMERA_REQ_CODE)
        } else {
            Log.i(TAG, "Permission granted.")
        }
    }

    private fun checkPermission() = PERMISSIONS.all {
        ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = tabTitles[position]
        viewPager.setCurrentItem(tab.position, true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_REQ_CODE && grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, getString(R.string.permission_granted))
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.REQUEST_CODE_SCAN_ONE
            && viewPager.currentItem == Config.POSITION_DEFAULT_VIEW
        ) {
            val fragmentTag = getString(R.string.fragment_tag, Config.POSITION_DEFAULT_VIEW)
            val defaultViewFragment =
                supportFragmentManager.findFragmentByTag(fragmentTag) as DefaultViewFragment?
            defaultViewFragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    internal class SearchViewPagerAdapter(
        fragmentActivity: FragmentActivity
    ): FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                POSITION_DEFAULT_VIEW -> DefaultViewFragment()
                POSITION_CUSTOMIZED_VIEW -> CustomizedViewFragment()
                else -> BitmapFragment()
            }
        }

        override fun getItemCount() = PAGE_COUNT
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val POSITION_DEFAULT_VIEW = 0
        private const val POSITION_CUSTOMIZED_VIEW = 1
        private const val PAGE_COUNT = 3

        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private const val CAMERA_REQ_CODE = 1
    }
}