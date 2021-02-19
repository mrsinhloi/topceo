package com.topceo.login.workchat.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.topceo.R
import com.topceo.accountkit.PhoneUtils
import com.topceo.db.TinyDB
import com.topceo.objects.other.User
import com.topceo.utils.MyUtils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sonhvp.utilities.listeners.textWatcher
import com.topceo.login.weteam.BaseActivity
import kotlinx.android.synthetic.main.wc_mh01_input_phone_number.*
import kotlinx.android.synthetic.main.wc_mh01_input_phone_number.view.*
import kotlinx.coroutines.launch


class InputPhoneActivityWc : BaseActivity() {


    override val fragContainer: Int = 0
    lateinit var db: TinyDB

    private var phoneTemp = ""
    var isValidateInLocal: Boolean = false
    var isForgetPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = TinyDB(this)
        init()
    }

    private fun init() {
        //UI
        setContentView(R.layout.wc_mh01_input_phone_number)

        val b: Bundle? = intent.extras
        if (b != null) {
            isValidateInLocal = b.getBoolean(VALIDATE_IN_LOCAL, false)
            isForgetPassword = b.getBoolean(IS_FORGET_PASSWORD, false)
            if (isForgetPassword) {
                linearInputPhone.visibility = View.VISIBLE
            }
        }

        window.decorView.rootView.apply {
            btnStart.setOnClickListener {
                if (isForgetPassword) {
                    editPhone.text.toString().trim().let { phone ->
                        if (phone.isNotEmpty()) {
                            checkMobileExisted(phone)
                        } else {
                            MyUtils.showToast(this@InputPhoneActivityWc, R.string.please_input_phone)
                        }
                    }
                } else {
                    startVerificationNumber("")
                }
            }

            editPhone.apply {
                if (phoneTemp.isNotEmpty()) setText(phoneTemp)
                textWatcher(
                        afterTextChanged = {
                            phoneTemp = it.toString()
                        }
                )
            }

            group1.visibility = View.GONE
            /*group1.apply {
                setOnCheckedChangeListener(null)
                if (languageSelected.equals(LocaleHelper.LANGUAGE_TIENG_VIET, ignoreCase = true)) {
                    check(R.id.radio1)
                } else {
                    check(R.id.radio2)
                }
                setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        R.id.radio1 -> {
                            if (!languageSelected.equals(LocaleHelper.LANGUAGE_TIENG_VIET, true)) {
                                languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET
                                db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected)
                                LocaleHelper.setLocale(this@InputPhoneActivity, languageSelected)
                                init()
                            }
                        }
                        R.id.radio2 -> {
                            if (!languageSelected.equals(LocaleHelper.LANGUAGE_ENGLISH, ignoreCase = true)) {
                                languageSelected = LocaleHelper.LANGUAGE_ENGLISH
                                db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected)
                                LocaleHelper.setLocale(this@InputPhoneActivity, languageSelected)
                                init()
                            }
                        }
                    }
                }
            }*/
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
        if (phoneE164 != null && !phoneE164!!.isNullOrBlank()) {
            startVerificationNumber(phoneE164!!)

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

        } else {
            MyUtils.showAlertDialog(this, R.string.phone_invalid)
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
                    val loadingDialog = MyUtils.createDialogLoading(this@InputPhoneActivityWc, R.string.reading_info)
                    loadingDialog.show()
                    user.getIdToken(true).addOnCompleteListener {

                        loadingDialog.dismiss()
                        if (isValidateInLocal) {
                            //verify in local
                            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                val phone = user.phoneNumber //chuan E164 +84912345678
                                var data = Intent()
                                data.putExtra(User.PHONE, phone)
                                setResult(Activity.RESULT_OK, data)
                                finish()
                            }
                        } else {
                            //verify in server
                            if (it.result != null) {
                                val token = it.result!!.token
                                if (!token.isNullOrEmpty()) {
                                    var data = Intent()
                                    data.putExtra(User.AUTHORIZATION_CODE, token)
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                }
                            }

                        }


                    }
                }

            } else {
                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) launch {
                    messageDialog { R.string.nointernet }
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
        const val IS_FORGET_PASSWORD = "IS_FORGET_PASSWORD"

    }


    //FIREBASE AUTH//////////////////////////////////////////////////////////////////////////////
    fun startVerificationNumber(number: String) {
        /*val number = editPhone.text.toString().trim()
        if (number.isNotEmpty()) {
            phoneE164 = PhoneUtils.getE164FormattedMobileNumber(number, PhoneUtils.getDefaultCountryNameCode())
            if (phoneE164 != null && !phoneE164!!.isBlankOrEmpty()) {
                val phone = phoneE164

            }
        } else {
            MyUtils.showToast(baseContext, R.string.please_input_phone)
        }*/

        val api = GoogleApiAvailability.getInstance()
        val code = api.isGooglePlayServicesAvailable(this)
        if (code == ConnectionResult.SUCCESS) {
            // The SafetyNet Attestation API is available.
            if (!TextUtils.isEmpty(number)) {
                var idpConfig = AuthUI.IdpConfig.PhoneBuilder()
                        .setDefaultNumber("vn", number)
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
            } else {
                var idpConfig = AuthUI.IdpConfig.PhoneBuilder()
                        .setDefaultCountryIso("vn")
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
        } else {
            // Prompt user to update Google Play services.
            if(api.isUserResolvableError(code)) {
                //prompt the dialog to update google play
                api.getErrorDialog(this, code, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            }
        }


    }
    val PLAY_SERVICES_RESOLUTION_REQUEST = 123

    //FIREBASE AUTH//////////////////////////////////////////////////////////////////////////////
    private fun checkMobileExisted(phone: String) {
        /*if (!TextUtils.isEmpty(phone)) {
            if (MyUtils.checkInternetConnection(this)) {
                ProgressUtils.show(this)
                MyApplication.getInstance().apiManagerMyXteam.checkMobileExisted(phone, object : Callback<JsonObject?> {
                    override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                        val obj = response.body()
                        if (obj != null) {
                            val result = ParserWc.parseJson(obj.toString(), Boolean::class.java, false)
                            if (result != null) {
                                //Thanh cong thi tra ve UserMBN -> qua man hinh nhap thong tin
                                if (result.errorCode == ReturnResult.SUCCESS) { //da ton tai thi vao
                                    val exist = result.data as Boolean
                                    if (exist) {
//                                        phoneLogin(phone)
                                        startVerificationNumber(phone)
                                    } else {
                                        val text = getString(R.string.phone_not_exists_please_chose_another_phone, phone)
                                        MyUtils.showAlertDialog(this@InputPhoneActivity, text)
                                    }
                                } else {
                                    //tb loi
                                    MyUtils.showAlertDialog(this@InputPhoneActivity, result.message)
                                }
                            }
                        }
                        ProgressUtils.hide()
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        ProgressUtils.hide()
                    }
                })
            } else {
                MyUtils.showThongBao(this)
            }
        }*/
    }

}