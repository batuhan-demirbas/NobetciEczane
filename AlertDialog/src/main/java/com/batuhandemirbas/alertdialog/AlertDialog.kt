package com.batuhandemirbas.alertdialog

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogs {
    fun presentAlert(context: Context, text: String, title: String) {
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        val builder = MaterialAlertDialogBuilder(context)

        with(builder) {
            setTitle("Konum Bilgisi")

            setMessage("Lütfen konum bilginizi giriniz")

            setNegativeButton("cancel") { dialog, which ->
                // Respond to negative button press
            }
            setPositiveButton("ok") { dialog, which ->
                // Respond to positive button press
            }
            .show()
        }
    }

    fun customDialog(context: Context) {
        val builder = AlertDialog.Builder(context)

        with(builder) {
            setView(R.layout.dialog_custom)
            create()
            show()
        }
    }

}
