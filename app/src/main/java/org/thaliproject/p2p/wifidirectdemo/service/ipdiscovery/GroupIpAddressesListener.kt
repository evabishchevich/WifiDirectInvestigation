package org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery

interface GroupIpAddressesListener {
    fun onGroupIpAddressesReceived(ipAddresses: List<String>)
}