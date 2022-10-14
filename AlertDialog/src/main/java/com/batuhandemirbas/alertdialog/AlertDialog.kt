package com.batuhandemirbas.alertdialog

import android.app.AlertDialog
import android.content.Context
import android.location.LocationManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogs {
    fun presentAlert(context: Context) {
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        val builder = MaterialAlertDialogBuilder(context)

        with(builder) {
            setTitle("Konum İzni")

            setMessage("Lütfen konumunuzu açınız")

            setNegativeButton("iptal") { dialog, which ->
                // Respond to negative button press
            }
            setPositiveButton("aç") { dialog, which ->
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
