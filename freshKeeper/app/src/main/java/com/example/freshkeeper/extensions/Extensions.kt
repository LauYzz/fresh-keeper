package com.example.freshkeeper.extensions

import android.content.Context
import android.provider.SyncStateContract.Helpers.update
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.example.freshkeeper.activity.MainActivity
import com.example.freshkeeper.R
import com.example.freshkeeper.helper.DatabaseHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.freshkeeper.data.SharedPreferencesAccess
import com.example.freshkeeper.fragment.dialog.DialogLoadingFragment
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