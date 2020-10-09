package com.huawei.scankit.kotlin.custom

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Size
import com.huawei.hms.ml.scan.HmsScan

object ScanUtils {
    private const val RIGHT_ANGLE = 90
    fun convertHmsScanToString(hmsScan: HmsScan?): String {
        val sb = StringBuilder()
        sb.append("Scan Type: ").append(convertScanTypeToString(hmsScan!!.getScanType()))
        sb.append("\nValue: ").append(hmsScan.getOriginalValue())
        if (hmsScan.getBookMarkInfo() != null) {
            sb.append("\nBook place (info): ").append(hmsScan.getBookMarkInfo().bookPlaceInfo)
            sb.append("\nBook place (uri): ").append(hmsScan.getBookMarkInfo().bookUri)
            sb.append("\nBook place (num): ").append(hmsScan.getBookMarkInfo().bookNum)
        }
        if (hmsScan.getContactDetail() != null) {
            sb.append("\nContact detail (title): ").append(hmsScan.getContactDetail().title)
            sb.append("\nContact detail (company): ").append(hmsScan.getContactDetail().company)
            sb.append("\nContact detail (note): ").append(hmsScan.getContactDetail().note)
        }
        if (hmsScan.getDriverInfo() != null) {
            sb.append("\nDriver info (city): ").append(hmsScan.getDriverInfo().city)
        }
        if (hmsScan.getEmailContent() != null) {
            sb.append("\nEmail content (address info): ")
                .append(hmsScan.getEmailContent().addressInfo)
            sb.append("\nEmail content (body info): ")
                .append(hmsScan.getEmailContent().bodyInfo)
        }
        if (hmsScan.getEventInfo() != null) {
            sb.append("\nEvent info (theme): ").append(hmsScan.getEventInfo().theme)
            sb.append("\nEvent info (abstract info): ")
                .append(hmsScan.getEventInfo().abstractInfo)
        }
        if (hmsScan.getLocationCoordinate() != null) {
            sb.append("\nLocation coordinate (latitude): ")
                .append(hmsScan.getLocationCoordinate().latitude)
            sb.append("\nLocation coordinate (longitude): ")
                .append(hmsScan.getLocationCoordinate().longitude)
        }
        if (hmsScan.wiFiConnectionInfo != null) {
            sb.append("\nWiFi connection info (ssidNumber): ")
                .append(hmsScan.wiFiConnectionInfo.ssidNumber)
            sb.append("\nWiFi connection info (cipherMode): ")
                .append(hmsScan.wiFiConnectionInfo.cipherMode)
            sb.append("\nWiFi connection info (password): ")
                .append(hmsScan.wiFiConnectionInfo.password)
        }
        if (hmsScan.getVehicleInfo() != null) {
            sb.append("\nVehicle info (countryCode): ").append(hmsScan.getVehicleInfo().countryCode)
        }
        if (hmsScan.borderRect != null) {
            sb.append("\nBorder Rect (left): ").append(hmsScan.borderRect.left)
            sb.append("\nBorder Rect (top): ").append(hmsScan.borderRect.top)
            sb.append("\nBorder Rect (right): ").append(hmsScan.borderRect.right)
            sb.append("\nBorder Rect (bottom): ").append(hmsScan.borderRect.bottom)
        }
        sb.append("\nZoom Value: ").append(hmsScan.zoomValue)
        return sb.toString()
    }

    fun convertCameraRect(rect: Rect, bitmapSize: Size, scanViewSize: Size): Rect {
        val rectF = RectF(rect)
        val mat = Matrix()
        mat.setRotate(RIGHT_ANGLE.toFloat(), bitmapSize.width / 2f, bitmapSize.height / 2f)
        mat.mapRect(rectF)
        rectF.top = rectF.top * scanViewSize.height / bitmapSize.height
        rectF.bottom = rectF.bottom * scanViewSize.height / bitmapSize.height
        rectF.left = rectF.left * scanViewSize.width / bitmapSize.width
        rectF.right = rectF.right * scanViewSize.width / bitmapSize.width
        val newRect = Rect()
        rectF.round(newRect)
        return newRect
    }

    private fun convertScanTypeToString(type: Int): String {
        return when (type) {
            0 -> "QRCODE_SCAN_TYPE"
            1 -> "AZTEC_SCAN_TYPE"
            2 -> "DATAMATRIX_SCAN_TYPE"
            3 -> "PDF417_SCAN_TYPE"
            4 -> "CODE39_SCAN_TYPE"
            5 -> "CODE93_SCAN_TYPE"
            6 -> "CODE128_SCAN_TYPE"
            7 -> "EAN13_SCAN_TYPE"
            8 -> "EAN8_SCAN_TYPE"
            9 -> "ITF14_SCAN_TYPE"
            10 -> "UPCCODE_A_SCAN_TYPE"
            11 -> "UPCCODE_E_SCAN_TYPE"
            12 -> "CODABAR_SCAN_TYPE"
            else -> "UNKNOWN"
        }
    }
}