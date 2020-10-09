package com.huawei.scankit.kotlin.custom

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.huawei.scankit.kotlin.Config
import java.util.*
import java.util.concurrent.Executors

class CameraDeviceCallback(
    private val cameraPreview: SurfaceView,
    private val imageReader: ImageReader,
    private val handler: Handler
) : CameraDevice.StateCallback() {

    companion object {
        private const val TAG = "CameraDeviceCallback"
    }

    private var cameraSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null

    fun closeCameraCaptureSession() {
        cameraSession?.stopRepeating()
        cameraDevice?.close()
        cameraSession?.close()
    }

    override fun onOpened(cameraDevice: CameraDevice) {
        Log.i(TAG, "CameraDevice.StateCallback onOpened()")
        this.cameraDevice = cameraDevice
        val csc: CameraCaptureSession.StateCallback = object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                Log.i(TAG, "CameraCaptureSession.StateCallback onConfigured()")

                val builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(cameraPreview.holder.surface)
                    addTarget(imageReader.surface)
                }

                cameraSession = cameraCaptureSession
                cameraSession?.setRepeatingRequest(builder.build(), null, null)
            }

            override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                Log.e(TAG, "CameraCaptureSession.StateCallback onConfigureFailed()")
            }
        }

        val previewSurface = cameraPreview.holder.surface
        val imageSurface = imageReader.surface

        try {
            if (Config.isVersionP) {
                val outputConfigurations = Vector<OutputConfiguration>().apply {
                    add(OutputConfiguration(previewSurface))
                    add(OutputConfiguration(imageSurface))
                }
                val sessionConfiguration = SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR, outputConfigurations,
                    Executors.newSingleThreadExecutor(), csc
                )
                cameraDevice.createCaptureSession(sessionConfiguration)
            } else {
                val v = Vector<Surface>().apply {
                    add(previewSurface)
                    add(imageSurface)
                }
                cameraDevice.createCaptureSession(v, csc, handler)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "CameraDevice.StateCallback Error", e)
        }
    }

    override fun onDisconnected(cameraDevice: CameraDevice) {
        Log.i(TAG, "CameraDevice.StateCallback onDisconnected()")
        cameraDevice.close()
    }

    override fun onError(cameraDevice: CameraDevice, i: Int) {
        Log.i(TAG, "CameraDevice.StateCallback onError()")
    }
}