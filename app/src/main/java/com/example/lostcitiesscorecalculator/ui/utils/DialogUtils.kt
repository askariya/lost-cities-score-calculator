package com.example.lostcitiesscorecalculator.ui.utils

import android.content.Context
import android.text.Html
import androidx.appcompat.app.AlertDialog

object DialogUtils {
    fun showConfirmationDialog(context: Context,
                               title: String,
                               message: String,
                               positiveButtonText: String,
                               negativeButtonText: String,
                               onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))

        builder.setPositiveButton(positiveButtonText) { dialog, which ->
            onConfirm()
        }

        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}