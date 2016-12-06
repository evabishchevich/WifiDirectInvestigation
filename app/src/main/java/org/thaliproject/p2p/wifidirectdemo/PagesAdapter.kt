package org.thaliproject.p2p.wifidirectdemo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import org.thaliproject.p2p.wifidirectdemo.peers.PeersFragment
import org.thaliproject.p2p.wifidirectdemo.test.TestFragment

class PagesAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    val PAGES_COUNT = 2
    private val TAB_NAMES = arrayOf("PEERS", "TEST")

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return PeersFragment()
            1 -> return TestFragment()
        }
        return PeersFragment()
    }

    override fun getCount(): Int {
        return PAGES_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_NAMES[position]
    }

}