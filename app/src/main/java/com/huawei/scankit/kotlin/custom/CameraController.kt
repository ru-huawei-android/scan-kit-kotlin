package com.huawei.scankit.kotlin.custom

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class CameraController(
    private val cameraManager: CameraManager,
    private val cameraPreview: SurfaceView
) {

    private lateinit var imageReader: ImageReader
    private lateinit var cameraDeviceCallback: CameraDeviceCallback
    private lateinit var handlerThread: HandlerThread

    @SuppressLint("MissingPermission")
    fun startCameraPreview(listener: OnImageAvailableListener) {

        handlerThread = HandlerThread(THREAD_NAME)
        handlerThread.start()

        val mainHandler = Handler(Looper.getMainLooper())
        val backgroundHandler = Handler(handlerThread.looper)

        // we want to use the backFacing camera
        val backFacingId = getBackFacingCameraId(cameraManager)
        if (backFacingId == null) {
            Log.e(TAG, "Can not open Camera because no backFacing Camera was found")
            return
        }

        setupStreamReader(cameraManager, backFacingId, listener, backgroundHandler)
        cameraDeviceCallback = CameraDeviceCallback(cameraPreview, imageReader, mainHandler)

        try {
            //we have a backFacing camera, so we can start the preview
            cameraManager.openCamera(backFacingId, cameraDeviceCallback, mainHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e(TAG, "Start Camera Preview", e)
        }
    }

    fun stopCameraPreview() {
        cameraDeviceCallback.closeCameraCaptureSession()
        handlerThread.quitSafely()
        imageReader.close()
    }

    private fun getBackFacingCameraId(cameraManager: CameraManager): String? {
        return try {
            val ids = cameraManager.cameraIdList
            for (id in ids) {
                Log.i(TAG, "Found Camera ID: $id")
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (CameraCharacteristics.LENS_FACING_BACK == cameraDirection) {
                    Log.i(TAG, "Found back facing camera")
                    return id
                }
            }
            null
        } catch (ce: CameraAccessException) {
            ce.printStackTrace()
            null
        }
    }

    private fun setupStreamReader(
        cameraManager: CameraManager,
        backFacingId: String,
        listener: OnImageAvailableListener,
        handler: Handler
    ) {
        try {
            val characteristics = cameraManager.getCameraCharacteristics(backFacingId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val size = map?.getOutputSizes(SurfaceHolder::class.java)?.get(0)
            if (size != null) {
                val width = size.width
                val height = size.height
                cameraPreview.holder.setFixedSize(width, height)
                imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, MAX_IMAGES)
                imageReader.setOnImageAvailableListener(listener, handler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e(TAG, "SetupStreamReader", e)
        }
    }

    companion object {
        private const val THREAD_NAME = "Thread"
        private const val TAG = "CameraController"
        private const val MAX_IMAGES = 5
    }
}