package com.tensorfix.flashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.getMainExecutor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var frontFlash:Boolean = false
    var rearFlash:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cam :CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val flashes = flashAvalableDevices(cam)
        for (id in flashes){
            when(id){
                "0" -> btnRearFlash.isEnabled = true
                "1" -> btnFrontFlash.isEnabled = true
            }
        }
        val torchCallback:CameraManager.TorchCallback = object: CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                flashMode(cameraId,enabled)
            }

            override fun onTorchModeUnavailable(cameraId: String) {
                super.onTorchModeUnavailable(cameraId)
                println("camera $cameraId  torch mode is unavalable")

            }
        }
        cam.registerTorchCallback(torchCallback,null)
        val btnListner = View.OnClickListener { v: View? ->
            when (v?.id){
                R.id.btnRearFlash -> {
                    if (!rearFlash) (turnOnFlash("0",true,cam)) else (turnOnFlash("0",false,cam))
                }
                R.id.btnFrontFlash -> {
                    if (!frontFlash) (turnOnFlash("1",true,cam)) else (turnOnFlash("1",false,cam))
                }

        }
        }
        btnRearFlash.setOnClickListener(btnListner)
        btnFrontFlash.setOnClickListener(btnListner)


    }

    private fun flashAvalableDevices(cameramanager:CameraManager):ArrayList<String>{
        val camId = cameramanager.cameraIdList
        val avalableFlashes = arrayListOf<String>()
        for (x in camId){
            val flashAvalable:Boolean? = cameramanager.getCameraCharacteristics(x).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
            if (flashAvalable!!)(avalableFlashes.add(x))

        }

        return avalableFlashes


    }

    private fun turnOnFlash(camId: String, status:Boolean,cameramanager: CameraManager){
        cameramanager.setTorchMode(camId,status)
        println("turn on flash called $camId $status")

    }

     fun flashMode(camId: String,flashStatus:Boolean){
        if (camId == "0" && flashStatus){
            btnRearFlash.text = "Turn Off"
            rearFlash = true
        }
         else if (camId == "1" && flashStatus){
            btnFrontFlash.text = "Turn Off"
            frontFlash = true
        }
         else if (camId == "0" && !flashStatus){
            btnRearFlash.text = "Turn On"
            rearFlash = false

        }
         else {
            btnFrontFlash.text = "Turn On"
            frontFlash = false

        }



    }



}


