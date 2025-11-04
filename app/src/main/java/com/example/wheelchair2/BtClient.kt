package com.example.headposecontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import java.util.concurrent.Executors
import android.annotation.SuppressLint

class BtClient(private val context: Context) {
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var out: OutputStream? = null
    private val io = Executors.newSingleThreadExecutor()

    companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private fun hasConnectPerm(): Boolean {
        return if (Build.VERSION.SDK_INT >= 31)
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        else true
    }

    @SuppressLint("MissingPermission")
    fun connectByMac(mac: String, onConnected: (Boolean) -> Unit) {
        io.execute {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter == null) {
                onConnected(false)
                return@execute
            }
            try {
                val device = adapter.getRemoteDevice(mac)
                adapter.cancelDiscovery()
                var sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
                try {
                    sock.connect()
                } catch (e: Exception) {
                    // Fallback insecure
                    val m = device.javaClass.getMethod(
                        "createInsecureRfcommSocketToServiceRecord", UUID::class.java
                    )
                    sock = m.invoke(device, SPP_UUID) as BluetoothSocket
                    sock.connect()
                }
                socket = sock
                out = sock.outputStream
                onConnected(true)
            } catch (e: Exception) {
                e.printStackTrace()
                try { socket?.close() } catch (_: IOException) {}
                onConnected(false)
            }
        }
    }


    fun sendCommand(cmd: String) {
        io.execute {
            try {
                out?.write((cmd + "\n").toByteArray(Charsets.UTF_8))
                out?.flush()
            } catch (_: Exception) { /* ignore */ }
        }
    }

    fun close() {
        io.execute {
            try { out?.close() } catch (_: Exception) {}
            try { socket?.close() } catch (_: Exception) {}
            out = null; socket = null
        }
    }
}
