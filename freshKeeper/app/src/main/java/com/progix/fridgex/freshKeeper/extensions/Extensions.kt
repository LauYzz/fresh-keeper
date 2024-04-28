package com.progix.fridgex.freshKeeper.extensions

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.example.freshkeeper.R


import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.progix.fridgex.freshKeeper.activity.MainActivity
import com.progix.fridgex.freshKeeper.data.SharedPreferencesAccess
import com.progix.fridgex.freshKeeper.fragment.dialog.DialogLoadingFragment
import com.progix.fridgex.freshKeeper.helper.DatabaseHelper
import kotlinx.coroutines.*
import java.util.*
import java.util.regex.Pattern


object Extensions {

    fun Context.dipToPixels(dipValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics)

    @ColorInt
    fun Context.getAttrColor(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun Context.adjustFontSize(fontScale: Float = 1.0f): Context {
        val configuration = resources.configuration
        configuration.fontScale = fontScale
        return createConfigurationContext(configuration)
    }

    fun AppCompatActivity.initDataBase() {
        println("language is: ")
        println(Locale.getDefault().displayLanguage)

        DatabaseHelper.DB_NAME = "FridgeXX_en.db"

        val mDBHelper = DatabaseHelper(this)
        MainActivity.mDb = mDBHelper.writableDatabase
        if (SharedPreferencesAccess.loadBoolean(
                this,
                "triedOnce"
            ) && !SharedPreferencesAccess.loadBoolean(
                this,
                "upgraded"
            ) || DatabaseHelper.mNeedUpdate
        ) {
            SharedPreferencesAccess.saveBoolean(this, "triedOnce", true)
            SharedPreferencesAccess.saveBoolean(this, "upgraded", false)
            DatabaseHelper.mNeedUpdate = true
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.updatedRecently))
                .setMessage(getString(R.string.updateMessage))
                .setPositiveButton(getString(R.string.update)) { _, _ ->
                    job?.cancel()
                    job = CoroutineScope(Dispatchers.Main).launch {
                        val loadingFragment = DialogLoadingFragment()
                        loadingFragment.isCancelable = false
                        if (!loadingFragment.isAdded) loadingFragment.show(
                            supportFragmentManager,
                            "custom"
                        )
                        asyncUpdatingDatabase(mDBHelper)
                        loadingFragment.dismiss()
                        SharedPreferencesAccess.saveBoolean(applicationContext, "upgraded", true)
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.bdSuccess),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        // 手动重新获取数据库连接
                        MainActivity.mDb = mDBHelper.writableDatabase
                    }
                }
                .setCancelable(false)
                .show()
            SharedPreferencesAccess.saveBoolean(this, "upgraded", false)
        }
    }

    private suspend fun asyncUpdatingDatabase(mDBHelper: DatabaseHelper) =
        withContext(Dispatchers.IO) {
            mDBHelper.updateDataBase()
        }

    private var job: Job? = null

    fun String?.isNumeric(): Boolean {
        return if (this == null) {
            false
        } else Pattern.compile("-?\\d+(\\.\\d+)?").matcher(this).matches()
    }
}