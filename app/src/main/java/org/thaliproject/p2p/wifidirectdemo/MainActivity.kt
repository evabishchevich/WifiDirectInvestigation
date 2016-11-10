package org.thaliproject.p2p.wifidirectdemo

import android.bluetooth.BluetoothAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById(R.id.main_vp_peers) as ViewPager
        viewPager.adapter = PagesAdapter(supportFragmentManager)
        val tabs = findViewById(R.id.main_tl_tabs) as TabLayout
        tabs.setupWithViewPager(viewPager)

        val adapter = BluetoothAdapter.getDefaultAdapter()
        val name = adapter.name
        Timber.i("BT name  $name")
        adapter.enable()
        val res = adapter.setName("XYZ")

        Timber.i("res  $res")
        Timber.i("BT new name  ${adapter.name}")
        Handler().postDelayed({ Timber.i("BT new name  ${adapter.name}") }, 3000L)

    }


}
