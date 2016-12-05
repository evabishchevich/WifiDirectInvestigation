package org.thaliproject.p2p.wifidirectdemo.peers.p2p

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.peers.Peer
import org.thaliproject.p2p.wifidirectdemo.peers.PeersAdapter
import org.thaliproject.p2p.wifidirectdemo.peers.p2p.WifiP2PPeer
import timber.log.Timber

class P2PPeersAdapter(override val data: MutableList<Peer>, val onPeerClickListener: OnPeerClickListener) :
        PeersAdapter, RecyclerView.Adapter<P2PPeersAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val device = data[position] as WifiP2PPeer
        holder!!.tvDeviceName.text = device.deviceName
        holder.tvDeviceAddress.text = device.deviceAddress
        Timber.d("data size " + itemCount)
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.item_peer, parent, false)
        return ViewHolder(view, onPeerClickListener)
    }

    inner class ViewHolder(itemView: View, val onPeerClickListener: OnPeerClickListener) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvDeviceName: TextView
        val tvDeviceAddress: TextView

        init {
            tvDeviceName = itemView.findViewById(R.id.peer_tv_name) as TextView
            tvDeviceAddress = itemView.findViewById(R.id.peer_tv_address) as TextView
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onPeerClickListener.onPeerClicked(data[adapterPosition] as WifiP2PPeer)
        }
    }

    interface OnPeerClickListener {

        fun onPeerClicked(peer: WifiP2PPeer)
    }
}

