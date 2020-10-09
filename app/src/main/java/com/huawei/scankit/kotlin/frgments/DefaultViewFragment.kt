package com.huawei.scankit.kotlin.frgments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.scankit.kotlin.Config
import com.huawei.scankit.kotlin.R
import com.huawei.scankit.kotlin.custom.ScanUtils
import kotlinx.android.synthetic.main.fragment_default_view.view.*

class DefaultViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_default_view, container, false)

        view.btnDefaultStartScan.setOnClickListener {
            // Set scanning parameters (Optional)
            val options = HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
                .create()

            ScanUtil.startScan(activity, Config.REQUEST_CODE_SCAN_ONE, options)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        if (requestCode == Config.REQUEST_CODE_SCAN_ONE) {
            val hmsScan: HmsScan? = data.getParcelableExtra(ScanUtil.RESULT)
            if (hmsScan != null) {
                val text = ScanUtils.convertHmsScanToString(hmsScan)
                Log.i(TAG, text)
                view?.tvDefaultResult?.text = text
            }
        }
    }

    companion object {
        var TAG = "DefaultViewFragment"
    }
}