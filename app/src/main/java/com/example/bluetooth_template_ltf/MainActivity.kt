package com.example.bluetooth_template_ltf

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.ui.AppBarConfiguration
import com.example.bluetooth_template_ltf.databinding.ActivityMainBinding
import com.example.bluetooth_template_ltf.helperBT.BTActivityWrapper
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import com.jakewharton.rxbinding3.view.drags
import com.jakewharton.rxbinding3.view.layoutChanges
import com.jakewharton.rxbinding3.view.systemUiVisibilityChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import com.jakewharton.rxbinding3.widget.changes
import android.view.View
import androidx.core.view.isVisible


class MainActivity : BTActivityWrapper() {
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var xIncrement: Double = 0.5
    private var yIncrement: Double = 0.7
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var lastMessageSentTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initBluetooth()

        binding.btnConnect.setOnClickListener {
            connectBluetooth(binding.inputBluetooth.text.toString())
        }

        binding.btnLeft.setOnClickListener{
            x-=xIncrement
            var positionString = String.format("%.1f %.1f", x, y)
            if(connected){
                sendBluetoothMessage(positionString)
                binding.xText.text = String.format("x: %.1f", x)
            }
        }

        binding.btnRight.setOnClickListener{
            x+=xIncrement
            var positionString = String.format("%.1f %.1f", x, y)
            if(connected){
                sendBluetoothMessage(positionString)
                binding.xText.text = String.format("x: %.1f", x)
            }
        }

        binding.btnUp.setOnClickListener{
            y+=yIncrement
            var positionString = String.format("%.1f %.1f", x, y)
            if(connected){
                sendBluetoothMessage(positionString)
                binding.yText.text = String.format("y: %.1f", y)
            }
        }

        binding.btnDown.setOnClickListener{
            y-=yIncrement
            var positionString = String.format("%.1f %.1f", x, y)
            if(connected){
                sendBluetoothMessage(positionString)
                binding.yText.text = String.format("y: %.1f", y)
            }
        }


//        binding.sliderServo.changes()
//            .debounce(100, TimeUnit.MILLISECONDS)
//            .distinctUntilChanged()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { value ->
//                angleServo = (value.toDouble()/100)*180
//                var angleString = String.format("%.1f %.1f", angleServo, angleStepper)
//                if (connected) {
//                    sendBluetoothMessage(angleString)
//                    binding.angleTextServo.text = String.format("Angle Servo: %.1f", angleServo)
//                } else {
//                    alert("Tidak terkoneksi dengan bluetooth device")
//                }
//            }
//
//        binding.sliderStepper.changes()
//            .debounce(100, TimeUnit.MILLISECONDS)
//            .distinctUntilChanged()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { value ->
//                angleStepper = (value.toDouble()/100)*180
//                var angleString = String.format("%.1f %.1f", angleServo, angleStepper)
//                if (connected) {
//                    sendBluetoothMessage(angleString)
//                    binding.angleTextStepper.text = String.format("Angle Stepper: %.1f", angleStepper)
//                } else {
//                    alert("Tidak terkoneksi dengan bluetooth device")
//                }
//            }


        // Ini buat check bluetooth connection,
        // lebih bagusnya kalau pake observables daripada
        // pake timer kek gini. But I value my time :D
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                if(connected && !binding.positionText.isVisible) {
                    binding.btnConnect.visibility = View.GONE
                    binding.inputBluetooth.visibility = View.GONE
                    binding.positionText.visibility = View.VISIBLE
                    binding.btnLeft.visibility = View.VISIBLE
                    binding.btnRight.visibility = View.VISIBLE
                    binding.btnUp.visibility = View.VISIBLE
                    binding.btnDown.visibility = View.VISIBLE
                    binding.xText.visibility = View.VISIBLE
                    binding.yText.visibility = View.VISIBLE
                } else if (!connected) {
                    binding.positionText.visibility = View.GONE
                    binding.btnLeft.visibility = View.GONE
                    binding.btnRight.visibility = View.GONE
                    binding.btnUp.visibility = View.GONE
                    binding.btnDown.visibility = View.GONE
                    binding.xText.visibility = View.GONE
                    binding.yText.visibility = View.GONE
//                    binding.sliderStepper.isEnabled = false
                }
                mainHandler.postDelayed(this, 200)
            }
        })
    }

    override fun onMessageSent(message: String) {

    }

    override fun onMessageReceived(message: String) {
    }
}