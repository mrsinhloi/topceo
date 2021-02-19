package com.topceo.socialspost.common

import android.content.Context
import com.topceo.socialspost.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogUtils {

    interface SelectMultipleCallback {
        fun onCancel() {}
        fun onOK(checkedItems: BooleanArray) {}
    }

    interface SelectMultipleCallbackFacebook : SelectMultipleCallback {
        fun onLogout() {}
    }

    companion object {

        /**
         * Dialog select multiple items basic
         */
        fun selectMultipleItemDialog(context: Context, title: Int, arrayList: Array<String>, checkedItems: BooleanArray?, callback: SelectMultipleCallback) {

            var checkedList = BooleanArray(arrayList.size)
            if (checkedItems != null) {
                checkedList = checkedItems
            }

            MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setNeutralButton(R.string.cancel) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        callback.onCancel()
                    }
                    .setPositiveButton(R.string.ok) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        callback.onOK(checkedList)
                    }
                    .setMultiChoiceItems(arrayList, checkedItems) { dialogInterface, position, checked ->
                        checkedList[position] = checked
                    }
                    .show()
        }


        /**
         * /**
         * Dialog select multiple items for facebook page selected
        */
         */
        fun selectMultipleItemDialogWithLogoutFacebook(context: Context, title: Int, arrayList: Array<String?>, checkedItems: BooleanArray?, callback: SelectMultipleCallbackFacebook) {

            var checkedList = BooleanArray(arrayList.size)
            if (checkedItems != null) {
                checkedList = checkedItems
            }

            MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setNegativeButton(R.string.cancel) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        callback.onCancel()
                    }
                    .setNeutralButton(R.string.logout) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        callback.onLogout()
                    }
                    .setPositiveButton(R.string.ok) { dialogInterface, which ->
                        dialogInterface.dismiss()
                        callback.onOK(checkedList)
                    }
                    .setMultiChoiceItems(arrayList, checkedItems) { dialogInterface, position, checked ->
                        checkedList[position] = checked
                    }
                    .show()
        }


    }
}