package com.topceo.login.workchat.ui

import android.content.Context
import android.provider.ContactsContract
import com.topceo.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import java.text.DecimalFormat

fun jsonObj(block: JSONObject.() -> Unit): JSONObject = JSONObject().apply(block)
fun JSONObject.userId(_id: Long) { put("_id", _id) }
fun JSONObject.identityId(identityId: String) { put("identityId", identityId) }
fun JSONObject.realName(realName: String) { put("realName", realName) }
fun JSONObject.avatar(avatar: String) { put("avatar", avatar) }
fun JSONObject.address(address: String) { put("address", address) }
fun JSONObject.gender(gender: String) { put("gender", gender) }
fun JSONObject.dob(dob: String) { put("dob", dob) }
fun JSONObject.phone(phone: String) { put("phone", phone) }
fun JSONObject.memo(memo: String) { put("memo", memo) }

//Get
fun JSONObject.userId(): String = optString("_id")
fun JSONObject.email(): String = optString("email")
fun JSONObject.avatar(): String = optString("avatar")
fun JSONObject.name(): String = optString("name")
fun JSONObject.payAccount(): Boolean = optBoolean("payAccount", true)
fun JSONObject.registerPromoReceived(): Boolean = optBoolean("registerPromoReceived", true)
fun JSONObject.token(): String = optString("token")
fun JSONObject.publicKey(): String = optString("publicKey")
fun JSONObject.privateKey(): String = optString("privateKey")
fun JSONObject.identityId(): String = optString("identityId")
fun JSONObject.realName(): String = optString("realName")
fun JSONObject.address(): String = optString("address")
fun JSONObject.gender(): String = optString("gender")
fun JSONObject.dob(): String = optString("dob")
fun JSONObject.pin(): String = optString("pin")
fun JSONObject.isValidate(): String? = getStringValue("isValidate")
fun JSONObject.validateDate(): Long = optLong("validateDate", -1)
fun JSONObject.level(): Int = optInt("level", -1)
fun JSONObject.createDate(): Long = optLong("createDate", -1)
fun JSONObject.lastUpdateDate(): Long = optLong("lastUpdateDate", -1)
fun JSONObject.lastLoginDate(): Long = optLong("lastLoginDate", -1)

fun JSONObject.updateDate(): Long = optLong("updateDate", -1)
fun JSONObject.phone(): String = optString("phone")
fun JSONObject.memo(): String? = getStringValue("memo")

fun JSONObject.getStringValue(key: String): String? = if (has(key)) {
    if (isNull(key)) null else getString(key)
} else {
    null
}
fun JSONObject.getLongValue(key: String): Long? = if (has(key)) {
    if (isNull(key)) null else getLong(key)
} else {
    null
}
fun JSONObject.getIntValue(key: String): Int? = if (has(key)) {
    if (isNull(key)) null else getInt(key)
} else {
    null
}

//
fun JSONObject.publicKey(publicKey: String) { put("publicKey", publicKey)}
//fun JSONObject.amount(amount: String) { put("amount", amount)}
fun JSONObject.amount(amount: Long) { put("amount", amount)}
fun JSONObject.amount(): Long {
    return if (has("amount")) {
        if (isNull("amount")) 0 else getLong("amount")
    } else {
        0
    }
}


fun decimalFormatter():DecimalFormat = DecimalFormat("#,###,###")
fun Int.toDecimalStr(): String = decimalFormatter().format(this)
fun Long.toDecimalStr(): String = decimalFormatter().format(this)

/*fun Fragment.messageDialog(title: String = "", message: String = "") {
    MaterialAlertDialogBuilder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok", null)
            .show()
}*/
fun Context.messageDialog(title: String = "", message: String = "", positive: () -> Unit = {}) {
    MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                positive()
            }
            .show()
}
fun Context.errorDialog(title: String = "", message: String = "", positive: () -> Unit = {}): Boolean {
    MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                positive()
            }
            .show()
    return false
}
fun Context.dialog(title: String = "", message: String = "", positive: () -> Unit = {}, negative: () -> Unit = {}) {
    MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ -> positive() }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                negative()
                dialog.dismiss()
            }
            .show()
}


val CONTACT_PROJECTION = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
)
val CONTACT_DATA_PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.NUMBER
)