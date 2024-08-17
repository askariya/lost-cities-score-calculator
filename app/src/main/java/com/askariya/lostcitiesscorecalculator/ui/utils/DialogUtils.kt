package com.askariya.lostcitiesscorecalculator.ui.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

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

        // Set null for button action initially
        builder.setPositiveButton(positiveButtonText, null)

        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Set a click listener to perform haptic feedback and dismiss the dialog
            positiveButton.setOnClickListener {
                positiveButton.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onConfirm()
                dialog.dismiss() // Close the dialog
            }

            // Set a click listener to perform haptic feedback and dismiss the dialog
            negativeButton.setOnClickListener {
                negativeButton.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                dialog.dismiss() // Close the dialog
            }
        }
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
        // Prevent notification from closing when clicking elsewhere.
        builder.setCancelable(false)

        // Set null for button action initially
        builder.setPositiveButton(positiveButtonText, null)

        val dialog: AlertDialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            // Set a click listener to perform haptic feedback and dismiss the dialog
            positiveButton.setOnClickListener {
                positiveButton.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                onConfirm()
                dialog.dismiss() // Close the dialog
            }
        }
        dialog.show()
    }

    fun showGameSavedNotification(context: Context){
        Toast.makeText(context, "Game Saved", Toast.LENGTH_SHORT).show()
    }

    fun showGameLoadedNotification(context: Context){
        Toast.makeText(context, "Loaded Save", Toast.LENGTH_SHORT).show()
    }

    // Function to make the text field flash
    fun flashTextColor(textView: TextView, fromColorId: Int, toColorId: Int) {
        val colorFrom = ContextCompat.getColor(textView.context, fromColorId)
        val colorTo = ContextCompat.getColor(textView.context, toColorId) // Flash color

        val animator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        animator.duration = 250 // Half a second total for flashing
        animator.repeatCount = 1
        animator.repeatMode = ValueAnimator.REVERSE

        animator.addUpdateListener { animation ->
            textView.setTextColor(animation.animatedValue as Int)
        }

        animator.start()
    }
}