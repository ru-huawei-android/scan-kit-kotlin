package com.huawei.scankit.kotlin.frgments

import android.graphics.Rect
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.scankit.kotlin.Config
import com.huawei.scankit.kotlin.R
import com.huawei.scankit.kotlin.custom.ScanUtils
import kotlinx.android.synthetic.main.fragment_customized_view.view.*

class CustomizedViewFragment : Fragment() {

    private lateinit var remoteView: RemoteView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customized_view, container, false)
        val flContent = view.findViewById<FrameLayout>(R.id.flCustomizedContent)

        // Set the scanning area. Set the parameters as required.
        val dm = resources.displayMetrics
        val density = dm.density
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        // Set the width and height of the barcode scanning box to 300 dp.
        val scanFrameSize = (SCAN_FRAME_SIZE * density).toInt()

        val rect = Rect().apply {
            left = screenWidth / 2 - scanFrameSize / 2
            right = screenWidth / 2 + scanFrameSize / 2
            top = screenHeight / 2 - scanFrameSize / 2
            bottom = screenHeight / 2 + scanFrameSize / 2
        }

        remoteView = RemoteView.Builder()
            .setContext(activity)
            .setBoundingBox(rect)
            .setFormat(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
            .build()

        // Load the customized view to the activity.
        remoteView.onCreate(savedInstanceState)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        flContent.addView(remoteView, 0, params)

        // Subscribe to the recognition result callback event.
        remoteView.setOnResultCallback { result: Array<HmsScan> -> showResult(result) }

        return view
    }

    override fun onStart() {
        super.onStart()
        remoteView.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView.onPause()
    }

    override fun onStop() {
        super.onStop()
        remoteView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView.onDestroy()
    }

    private fun showResult(result: Array<HmsScan>) {
        val fragmentTop = resources.getDimensionPixelOffset(R.dimen.app_bar_height).toFloat()

        val value = result.joinToString(separator = Config.DOUBLE_LINE_TRANSLATION) {
            ScanUtils.convertHmsScanToString(it)
        }

        val rectangles = result.map {
            it.borderRect.apply {
                top += fragmentTop.toInt()
                bottom += fragmentTop.toInt()
            }
        }.toTypedArray()

        Log.i(TAG, value)

        view?.tvCustomizedResult?.apply {
            text = value
            Linkify.addLinks(this, Linkify.ALL)
        }

        view?.bcvCustomizedResult?.setBorderRectangles(rectangles)
    }

    companion object {
        private const val TAG = "CustomizedViewFragment"
        private const val SCAN_FRAME_SIZE = 300
    }
}