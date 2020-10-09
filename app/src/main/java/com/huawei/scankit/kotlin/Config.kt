package com.huawei.scankit.kotlin

import android.os.Build

object Config {
    const val REQUEST_CODE_SCAN_ONE = 10
    const val POSITION_DEFAULT_VIEW = 0
    const val DOUBLE_LINE_TRANSLATION = "\n\n"

    val isVersionP: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}