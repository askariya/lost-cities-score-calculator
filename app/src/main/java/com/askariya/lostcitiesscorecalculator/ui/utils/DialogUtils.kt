package com.askariya.lostcitiesscorecalculator.ui.utils

import android.content.Context
import android.text.Html
import android.widget.Toast
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

    fun showNotificationDialog(context: Context,
                               title: String,
                               message: String,
                               positiveButtonText: String,
                               onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
        builder.setCancelable(false) // Prevent notification from closing when clicking elsewhere.
        builder.setPositiveButton(positiveButtonText) { dialog, which ->
            onConfirm()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showGameSavedNotification(context: Context){
        Toast.makeText(context, "Game Saved", Toast.LENGTH_SHORT).show()
    }

    fun showGameLoadedNotification(context: Context){
        Toast.makeText(context, "Loaded Save", Toast.LENGTH_SHORT).show()
    }
}