package com.huawei.scankit.kotlin.frgments

import android.content.Context
import android.graphics.*
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzer
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.scankit.kotlin.Config
import com.huawei.scankit.kotlin.R
import com.huawei.scankit.kotlin.custom.CameraController
import com.huawei.scankit.kotlin.custom.ScanUtils
import kotlinx.android.synthetic.main.fragment_bitmap.view.*
import java.io.ByteArrayOutputStream

class BitmapFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var cameraController: CameraController
    private lateinit var scanViewSize: Size

    private val cameraManager: CameraManager by lazy {
        requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bitmap, container, false)

        val surfaceView = view.findViewById<SurfaceView>(R.id.svBitmapResult)

        view.rgBitmapFunctions.setOnCheckedChangeListener { _, _ ->
            view.bcvBitmapResult.clear()
            view.tvBitmapResult.text = ""
        }

        surfaceView.holder.addCallback(this)

        cameraController = CameraController(cameraManager, surfaceView)

        view.bcvBitmapResult.post {
            val width: Int = view.bcvBitmapResult.measuredWidth
            val height: Int = view.bcvBitmapResult.measuredHeight
            scanViewSize = Size(width, height)
        }

        return view
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        Log.i(TAG, "surfaceCreated()")
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        Log.i(TAG, "surfaceChanged()")
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        Log.i(TAG, "surfaceDestroyed()")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause()")
        cameraController.stopCameraPreview()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume()")
        cameraController.startCameraPreview(onImageAvailableListener)
    }

    private val onImageAvailableListener = OnImageAvailableListener { reader: ImageReader ->
        val image = reader.acquireLatestImage()
        if (image == null) {
            Log.i(TAG, "image is null, do nothing.")
            return@OnImageAvailableListener
        }
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer[bytes]

        val yuv = YuvImage(bytes, ImageFormat.NV21, image.width, image.height, null)

        val stream = ByteArrayOutputStream()
        val rect = Rect(0, 0, image.width, image.height)
        yuv.compressToJpeg(rect, 100, stream)

        val length = stream.toByteArray().size
        val bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, length)

        image.close()

        when (view?.rgBitmapFunctions?.checkedRadioButtonId) {
            R.id.rbFunctionBitmap -> scanBitmap(bitmap)
            R.id.rbFunctionMultiProcessor -> scanMultiprocessor(bitmap)
            else -> {
            }
        }
    }

    private fun scanBitmap(bitmap: Bitmap) {
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
            .setPhotoMode(false)
            .create()

        val hmsScans = ScanUtil.decodeWithBitmap(activity, bitmap, options)

        // Process the decoding result when the scanning is successful.
        if (hmsScans != null && hmsScans.isNotEmpty()) {
            // Display the scanning result.
            showResult(hmsScans, Size(bitmap.width, bitmap.height))
        }
    }

    private fun scanMultiprocessor(bitmap: Bitmap) {
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
            .create()

        val barcodeDetector = HmsScanAnalyzer(options)
        val image = MLFrame.fromBitmap(bitmap)
        val result = barcodeDetector.analyseFrame(image)

        Log.d(TAG, "scanMultiprocessor: ${result != null} ${result.size() > 0}")
        // Process the decoding result when the scanning is successful.
        if (result != null && result.size() > 0) {
            val size = Size(bitmap.width, bitmap.height)

            val hmsScans = (0 until result.size()).map { index ->
                result[result.keyAt(index)]
            }.toTypedArray()

            // Display the scanning result.
            showResult(hmsScans, size)
        }
    }

    private fun showResult(result: Array<HmsScan>, bitmapSize: Size) {
        // Obtain the scanning result object HmsScan.
        val value = result.joinToString(separator = Config.DOUBLE_LINE_TRANSLATION) {
            ScanUtils.convertHmsScanToString(it)
        }

        val rectangles = result.map {
            ScanUtils.convertCameraRect(it.borderRect, bitmapSize, scanViewSize)
        }.toTypedArray()

        Log.i(TAG, value)

        requireActivity().runOnUiThread {
            view?.tvBitmapResult?.apply {
                text = value
                Linkify.addLinks(this, Linkify.ALL)
            }

            view?.bcvBitmapResult?.setBorderRectangles(rectangles)
        }
    }

    companion object {
        var TAG = "BitmapFragment"
    }
}