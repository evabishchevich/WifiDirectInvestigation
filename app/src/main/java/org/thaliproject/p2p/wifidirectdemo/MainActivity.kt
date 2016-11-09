package org.thaliproject.p2p.wifidirectdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById(R.id.main_vp_peers) as ViewPager
        viewPager.adapter = PagesAdapter(supportFragmentManager)
        val tabs = findViewById(R.id.main_tl_tabs) as TabLayout
        tabs.setupWithViewPager(viewPager)

    }


}
