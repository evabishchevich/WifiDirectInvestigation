package org.thaliproject.p2p.wifidirectdemo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.thaliproject.p2p.wifidirectdemo.peers.PeersFragment

class PagesAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    val PAGES_COUNT = 2
    private val TAB_NAMES = arrayOf("PEERS", "TEST")

    override fun getItem(position: Int): Fragment {
        //TODO change it
        return PeersFragment()
    }

    override fun getCount(): Int {
        return PAGES_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_NAMES[position]
    }


}