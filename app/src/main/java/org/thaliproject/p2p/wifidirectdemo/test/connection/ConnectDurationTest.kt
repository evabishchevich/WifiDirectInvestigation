package org.thaliproject.p2p.wifidirectdemo.test.connection

import org.thaliproject.p2p.wifidirectdemo.Settings
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiAP
import org.thaliproject.p2p.wifidirectdemo.service.WifiService
import org.thaliproject.p2p.wifidirectdemo.test.BaseTest
import org.thaliproject.p2p.wifidirectdemo.test.TestResult
import org.thaliproject.p2p.wifidirectdemo.test.TestResultListener
import org.thaliproject.p2p.wifidirectdemo.test.Timer
import timber.log.Timber

class ConnectDurationTest(testResultListener: TestResultListener) : BaseTest(testResultListener) {

    private val attemps = 30;
    private val timer = Timer()

    fun start(wifiService: WifiService) {
        Thread(Runnable {
            val durations = mutableListOf<Long>()
            var currentAttempt = 0
            var canGo = true;
            while (currentAttempt < attemps) {
                if (!canGo) {
                    continue
                }
                if (currentAttempt >= attemps) {
                    break
                }
                canGo = false
                Timber.i("Attempt #$currentAttempt")
                Timber.d("start discovery")
                timer.start()
                wifiService.findNetworks(object : WifiService.NetworksAvailableListener {
                    override fun onNetworksAvailable(networks: List<WifiAP>) {
                        Timber.d("onNetworksAvailable, networks: $networks")
                        val wifiNetwork = networks.filter { it -> it.SSID.contains(Settings.DEVICE_NAME) }.first()
                        Timber.d("connect to : $wifiNetwork")
                        wifiService.connect(wifiNetwork, object : WifiService.ConnectionListener {
                            override fun onConnected(wifiAP: WifiAP) {
                                Timber.d("onConnected to : $wifiAP")
                                currentAttempt++;
                                canGo = true
                                durations.add(timer.finish())
                            }
                        })
                    }
                })
            }

            val averageDuration = Math.round(durations.average())
            Timber.d("Test finished; average duration is $averageDuration")
            testResultListener.onTestFinished(ConnectDurationTestResult(averageDuration))
        }).start()
    }

    class ConnectDurationTestResult(duration: Long) : TestResult() {
        override val data: String

        init {
            data = "Total duration is $duration milliseconds"
        }

    }

}