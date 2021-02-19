package com.topceo.firebase_auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.topceo.R
import com.topceo.accountkit.PhoneUtils
import com.topceo.db.TinyDB
import com.topceo.objects.other.User
import com.topceo.utils.MyUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.mh01_input_phone_number.view.*

class InputPhoneActivity : AppCompatActivity() {


    private var db: TinyDB? = null

    private var phoneTemp = ""
    var isValidateInLocal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = TinyDB(applicationContext)
//        init()
    }

    private fun init() {
        //UI
        setContentView(R.layout.mh01_input_phone_number)

        val b: Bundle? = intent.extras
        isValidateInLocal = b!!.getBoolean(VALIDATE_IN_LOCAL, false)

        window.decorView.rootView.apply {
            btnStart.setOnClickListener {
                /*editPhone.text.toString().trim().let { phone ->
                    if (phone.isNotEmpty()) {
                        phoneLogin(phone)
                    } else {
                        MyUtils.showToast(this@InputPhoneActivity, R.string.please_input_phone)
                    }
                }*/
                startVerificationNumber()
            }

            editPhone.apply {
                if (phoneTemp.isNotEmpty()) setText(phoneTemp)
                textWatcher(
                        afterTextChanged = {
                            phoneTemp = it.toString()
                        }
                )
            }


        }


    }

    var phoneE164: String? = ""
    /*private fun phoneLogin(phone: String) {
        phoneE164 = PhoneUtils.getE164FormattedMobileNumber(phone, PhoneUtils.getDefaultCountryNameCode())
        if (!phoneE164!!.isBlankOrEmpty()) {
            //xac thuc sdt da ton tai tren server truoc, neu da ton tai thi dang nhap, chua thi moi xac thuc va dang ky
            val number = PhoneNumber("", phoneE164!!, "VN")
            val uiManager = ThemeUIManager(R.style.AppLoginTheme_Custom)

            val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                    LoginType.PHONE,
                    AccountKitActivity.ResponseType.TOKEN
            ).apply {
                setInitialPhoneNumber(number)
                setUIManager(uiManager)
            }.build()

            val intent = Intent(this@InputPhoneActivity, AccountKitActivity::class.java).apply {
                putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder)
            }
            startActivityForResult(intent, APP_REQUEST_CODE)

            progressBar.visibility = View.VISIBLE
        }
    }*/
    private fun phoneLogin(phone: String) {
        phoneE164 = PhoneUtils.getE164FormattedMobileNumber(phone, PhoneUtils.getDefaultCountryNameCode())
        if (phoneE164 != null && !phoneE164!!.isNullOrEmpty()) {
            //xac thuc sdt da ton tai tren server truoc, neu da ton tai thi dang nhap, chua thi moi xac thuc va dang ky
            /*val number = PhoneNumber("", phoneE164!!, "VN")
            val uiManager = ThemeUIManager(R.style.AppLoginTheme_Custom)

            val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                    LoginType.PHONE,
                    if (isValidateInLocal) AccountKitActivity.ResponseType.TOKEN else AccountKitActivity.ResponseType.CODE
            ).apply {
                setInitialPhoneNumber(number)
                setUIManager(uiManager)
            }.build()

            val intent = Intent(this@InputPhoneActivity, AccountKitActivity::class.java).apply {
                putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder)
            }

            if (isValidateInLocal) {
                startActivityForResult(intent, APP_REQUEST_CODE_LOCAL)
            } else {
                startActivityForResult(intent, APP_REQUEST_CODE_SERVER)
            }
            progressBar.visibility = View.VISIBLE
            */

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == APP_REQUEST_CODE_SERVER) {
            /*val loginResult = intent!!.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            when {
                loginResult.error != null -> messageDialog(loginResult.error.toString())
                loginResult.wasCancelled() -> progressBar!!.visibility = View.GONE
                else ->
                     {
                    //AQBuYuYyLfW5uhgR8CffEaaEdf7m-drNm-OPaXz3B3WPMvq2So-IOA6nTrymFDZ9Uov0mEFzcxmn6OKleoKoofyp46K1LGqBNHPLGtAA0q2l6jv78zv-a_9-tkZvzp-7CfPT_7RNnVXp7AbXGlswc0wxfXsazgl_9VAJSuybqQOfqGiJnti4RyetMZljWlh0McnUBAZNedfaLFXhXQuhZZLPyoIy5Jva8ybzYCymH-2c0TkrsOjUdkoYVkDVUx_j-Qc
                    val code = loginResult.getAuthorizationCode()
                    //tra authorization code ve man hinh yeu cau
                    var data = Intent()
                    data.putExtra(UserMBN.AUTHORIZATION_CODE, code)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }

            }*/
        } else if (requestCode == APP_REQUEST_CODE_LOCAL) {
            /*val loginResult = intent!!.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            when {
                loginResult.error != null -> messageDialog(loginResult.error.toString())
                loginResult.wasCancelled() -> progressBar!!.visibility = View.GONE
                else ->
                    AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
                        override fun onSuccess(account: Account?) {
                            account?.run {
                                //val rawPhone = account.phoneNumber.rawPhoneNumber
                                //val phone = account.phoneNumber.phoneNumber
                                val phoneE164 = phoneNumber.toString()
                                phoneE164.takeUnless { it.isBlankOrEmpty() }?.let {
                                    //tra sdt ve man hinh yeu cau
                                    var data = Intent()
                                    data.putExtra(UserMBN.PHONE, phoneE164)
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                }
                            }
                        }

                        override fun onError(accountKitError: AccountKitError) {

                        }
                    })

            }*/
        }

        //Firebase auth
        /*val loadingDialog = MaterialAlertDialogBuilder(this@InputPhoneActivity)
                .setTitle(R.string.logging_in)
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create()*/


        if (requestCode == APP_REQUEST_CODE && intent != null) {
            val response = IdpResponse.fromResultIntent(intent)
            if (response == null) {
                messageDialog { R.string.error_verification }
                return
            }


            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val loadingDialog = MyUtils.createDialogLoading(this@InputPhoneActivity, R.string.reading_info)
                    loadingDialog.show()
                    user.getIdToken(true).addOnCompleteListener {

                        loadingDialog.dismiss()
                        if (isValidateInLocal) {
                            //verify in local
                            val user2: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            if (user2 != null) {
                                val phone = user2.phoneNumber //chuan E164 +84912345678
                                var data = Intent()
                                data.putExtra(User.PHONE, phone)
                                setResult(Activity.RESULT_OK, data)
                                finish()
                            }
                        } else {
                            //verify in server
                            var phone:String? = null
                            val user2: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            if (user2 != null) {
                                phone = user2.phoneNumber //chuan E164 +84912345678
                            }
                            val token = it.result!!.token
                            if (!token.isNullOrEmpty()) {
                                var data = Intent()
                                data.putExtra(User.AUTHORIZATION_CODE, token)
                                data.putExtra(User.PHONE, phone)
                                setResult(Activity.RESULT_OK, data)
                                finish()
                            }

                        }


                    }
                }

            } else {
                if (response.error?.errorCode == ErrorCodes.NO_NETWORK){
                    messageDialog { R.string.khongCoInternet }
                }
            }
        }

    }


    companion object {
        var APP_REQUEST_CODE_LOCAL = 88
        var APP_REQUEST_CODE_SERVER = 99
        var APP_REQUEST_CODE = 100


        //true: xac thuc local, false: xac thuc tren server
        const val VALIDATE_IN_LOCAL = "VALIDATE_IN_LOCAL"

    }


    //FIREBASE AUTH//////////////////////////////////////////////////////////////////////////////
    fun startVerificationNumber() {
        /*val number = editPhone.text.toString().trim()
        if (number.isNotEmpty()) {
            phoneE164 = PhoneUtils.getE164FormattedMobileNumber(number, PhoneUtils.getDefaultCountryNameCode())
            if (phoneE164 != null && !phoneE164!!.isBlankOrEmpty()) {
                val phone = phoneE164

            }
        } else {
            MyUtils.showToast(baseContext, R.string.please_input_phone)
        }*/
        val idpConfig = AuthUI.IdpConfig.PhoneBuilder()
                .setDefaultCountryIso("vn")//if (languageSelected == "vi") "vn" else "en"
//                        .setDefaultNumber(phone!!)
                .build()

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
//                        .setTheme(R.style.GreenTheme)
                        .setAvailableProviders(listOf(idpConfig))
                        .setIsSmartLockEnabled(false)
                        .build(),
                APP_REQUEST_CODE
        )
    }


    //FIREBASE AUTH//////////////////////////////////////////////////////////////////////////////

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

}