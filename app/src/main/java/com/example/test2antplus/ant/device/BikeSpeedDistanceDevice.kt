package com.example.test2antplus.ant.device

import android.content.Context
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle
import com.pawegio.kandroid.runOnUiThread
import java.math.BigDecimal
import java.util.*

class BikeSpeedDistanceDevice(
    private val context: Context,
    private val getSpeed: (speed: String) -> Unit,
    private val getDistance: (distance: String) -> Unit,
    private val getCadence: (cadence: String) -> Unit,
    private val showToast: (text: String) -> Unit,
    private val setDependencies: (name: String, packageName: String) -> Unit
) {
    private var bsdPcc: AntPlusBikeSpeedDistancePcc? = null
    private var bcPcc: AntPlusBikeCadencePcc? = null
    private var bcReleaseHandle: PccReleaseHandle<AntPlusBikeCadencePcc>? = null

    val mResultReceiver: IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> =
        object : IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> {
            // Handle the result, connecting to events on success or reporting failure to user.
            override fun onResultReceived(
                result: AntPlusBikeSpeedDistancePcc,
                resultCode: RequestAccessResult,
                initialDeviceState: DeviceState
            ) {
                when (resultCode) {
                    RequestAccessResult.SUCCESS -> {
                        bsdPcc = result
                        bsdPcc?.let {
                            subscribeToEvents()
                        }
                    }
                    RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                        showToast.invoke("Channel Not Available")
                    }
                    RequestAccessResult.ADAPTER_NOT_DETECTED -> {
                        showToast.invoke("ANT Adapter Not Available. Built-in ANT hardware or external adapter required.")
                    }
                    RequestAccessResult.BAD_PARAMS -> {
                        showToast.invoke("Bad request parameters.")
                    }
                    RequestAccessResult.OTHER_FAILURE -> {
                        showToast.invoke("RequestAccess failed. See logcat for details.")
                    }
                    RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                        setDependencies.invoke(
                            AntPlusHeartRatePcc.getMissingDependencyName(),
                            AntPlusHeartRatePcc.getMissingDependencyPackageName()
                        )
                    }
                    RequestAccessResult.UNRECOGNIZED -> {
                        showToast.invoke("Failed: UNRECOGNIZED.\nPluginLib Upgrade Required?")
                    }
                    else -> {
                        showToast.invoke("Unrecognized result: $resultCode")
                    }
                }
            }

            /**
             * Subscribe to all the heart rate events, connecting them to display
             * their data.
             */
            private fun subscribeToEvents() {
                // 2.095m circumference = an average 700cx23mm road tire
                bsdPcc?.subscribeCalculatedSpeedEvent(
                    object : CalculatedSpeedReceiver(BigDecimal(2.095)) {
                        override fun onNewCalculatedSpeed(
                            estTimestamp: Long,
                            eventFlags: EnumSet<EventFlag>,
                            speed: BigDecimal
                        ) {
                            runOnUiThread {
                                getSpeed.invoke(speed.setScale(1, 0).toString())
                            }
                        }
                    })

                bsdPcc?.subscribeCalculatedAccumulatedDistanceEvent(
                    object : CalculatedAccumulatedDistanceReceiver(BigDecimal(2.095)) {
                        override fun onNewCalculatedAccumulatedDistance(
                            estTimestamp: Long,
                            eventFlags: EnumSet<EventFlag>,
                            distance: BigDecimal
                        ) {
                            runOnUiThread {
                                getDistance.invoke(distance.setScale(2, 0).toString())
                            }
                        }
                    })

                bsdPcc?.subscribeRawSpeedAndDistanceDataEvent { _, _, speed, distance ->
                    runOnUiThread {
                        getSpeed.invoke(speed.toString())
                        getDistance.invoke(distance.toString())
                    }
                }

                if (bsdPcc!!.isSpeedAndCadenceCombinedSensor) {
                    runOnUiThread {
                        bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(
                            context,
                            bsdPcc!!.antDeviceNumber,
                            0,
                            true,
                            { result, resultCode, _ ->
                                // IPluginAccessResultReceiver<AntPlusBikeCadencePcc> :
                                // Handle the result, connecting to events
                                // on success or reporting failure to user.
                                when (resultCode) {
                                    RequestAccessResult.SUCCESS -> {
                                        bcPcc = result
                                        bcPcc?.let {
                                            it.subscribeCalculatedCadenceEvent { _, _, cadence ->
                                                runOnUiThread {
                                                    getCadence.invoke(cadence.toString())
                                                }
                                            }
                                        }
                                    }
                                    RequestAccessResult.CHANNEL_NOT_AVAILABLE -> {
                                    }

                                    RequestAccessResult.BAD_PARAMS -> {
                                    }

                                    RequestAccessResult.OTHER_FAILURE -> {
                                    }

                                    RequestAccessResult.DEPENDENCY_NOT_INSTALLED -> {
                                    }

                                    else -> {
                                        showToast.invoke("Unrecognized result: $resultCode")
                                    }
                                }
                            },
                            // Receives state changes and shows it on the status display line

                            { state ->
                                // AntPluginPcc.IDeviceStateChangeReceiver:
                                runOnUiThread {
                                    if (state == DeviceState.DEAD) {
                                        bcPcc = null
                                    }
                                }
                            })
                    }
                } else {
                    // Subscribe to the events available in the pure cadence profile
                    runOnUiThread {
                    }

                    bsdPcc?.subscribeCumulativeOperatingTimeEvent { _, _, _ ->
                        runOnUiThread {
                        }
                    }

                    bsdPcc?.subscribeMotionAndSpeedDataEvent { _, _, _ ->
                        runOnUiThread {
                        }
                    }
                }
            }
        }

    // Receives state changes and shows it on the status display line
    val mDeviceStateChangeReceiver = AntPluginPcc.IDeviceStateChangeReceiver { newDeviceState ->
        runOnUiThread {
            if (newDeviceState == DeviceState.DEAD) {
                bsdPcc = null
            }
        }
    }
}