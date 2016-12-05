package org.thaliproject.p2p.wifidirectdemo.peers.wifi

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.peers.Peer
import org.thaliproject.p2p.wifidirectdemo.peers.PeersAdapter
import timber.log.Timber

class RegularWifiPeersAdapter(override val data: MutableList<Peer>, val onPeerClickListener: OnPeerClickListener) :
        PeersAdapter, RecyclerView.Adapter<RegularWifiPeersAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val wifiAp = data[position] as WifiAP
        holder!!.SSID.text = wifiAp.SSID
        Timber.d("data size " + itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.item_peer, parent, false)
        return ViewHolder(view, onPeerClickListener)
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView: View, val onPeerClickListener: OnPeerClickListener) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val SSID: TextView

        init {
            SSID = itemView.findViewById(R.id.peer_tv_name) as TextView
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onPeerClickListener.onPeerClicked(data[adapterPosition] as WifiAP)
        }
    }

    interface OnPeerClickListener {

        fun onPeerClicked(peer: WifiAP)
    }
}
