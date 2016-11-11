package org.thaliproject.p2p.wifidirectdemo.peers

import android.net.wifi.p2p.WifiP2pDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.R
import timber.log.Timber

class PeersAdapter(val data: MutableList<WifiP2pDevice>, val onPeerClickListener: OnPeerClickListener) :
        RecyclerView.Adapter<PeersAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val device = data[position]
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
            onPeerClickListener.onPeerClicked(data[adapterPosition])
        }
    }

    interface OnPeerClickListener {

        fun onPeerClicked(peer: WifiP2pDevice)
    }
}

