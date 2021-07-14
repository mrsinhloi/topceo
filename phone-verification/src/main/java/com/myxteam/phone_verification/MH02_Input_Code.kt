package com.myxteam.phone_verification

import `in`.aabhasjindal.otptextview.OTPListener
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myxteam.phone_verification.databinding.ActivityMh02InputCodeBinding
import java.util.concurrent.TimeUnit

class MH02_Input_Code : AppCompatActivity() {
    companion object {
        const val TAG = "TAG_MH02_Input_Code"
        const val TIMEOUT_SECONDS = 30L

        fun start(context: Context, phone: String, isValidateInLocal: Boolean, isForgetPassword:Boolean) {
            val intent = Intent(context, MH02_Input_Code::class.java)
            intent.putExtra(MH01_Input_Phone.PHONE_NUMBER, phone)
            intent.putExtra(MH01_Input_Phone.IS_VALIDATE_IN_LOCAL, isValidateInLocal)
            intent.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, isForgetPassword)

            intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT //forward ket qua setResult
            context.startActivity(intent)
        }
    }

    var number: String? = ""
    lateinit var binding: ActivityMh02InputCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMh02InputCodeBinding.inflate(layoutInflater)
        binding.closeIv.setOnClickListener { finish() }

        number = intent.extras?.getString(MH01_Input_Phone.PHONE_NUMBER, "")
        binding.phoneTv.text = number

        //request code
        number?.let { initPhoneAuth(it) }

        //otp
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                //val otp = binding.otpView.otp

            }

            override fun onOTPComplete(otp: String) {
//                Toast.makeText(context, "The OTP is $otp", Toast.LENGTH_SHORT).show()
//                binding.otpView.showError()
                verifyPhoneNumberWithCode(storedVerificationId, otp)
            }

        }


        setContentView(binding.root)
        init()
        //start timer
        setState(SendCodeState.COUNTDOWN)

        binding.otpView.requestFocusOTP()
        showKeyboard()
    }


    var timer = object : CountDownTimer(TIMEOUT_SECONDS * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.state1Tv.text =
                getString(R.string.resend_code_after_x, millisUntilFinished / 1000)
        }

        override fun onFinish() {
            if (currentState == SendCodeState.COUNTDOWN) {//count down lan 1 -> đổi lại count down 1 lần
//                setState(SendCodeState.RESEND)
                setState(SendCodeState.SIGNUP)
            } /*else if (currentState == SendCodeState.RESEND) {//count down lan 2
                setState(SendCodeState.SIGNUP)
            }*/
        }

    }

    var currentState = SendCodeState.COUNTDOWN
    fun setState(state: SendCodeState) {
        currentState = state
        when (state) {
            SendCodeState.COUNTDOWN -> {
                binding.state1Tv.visibility = View.VISIBLE
                binding.state2Tv.visibility = View.GONE
                binding.state3Tv.visibility = View.GONE

                //START TIMER 30S
                timer.start()
            }
            SendCodeState.RESEND -> {
                binding.state1Tv.visibility = View.GONE
                binding.state2Tv.visibility = View.VISIBLE
                binding.state3Tv.visibility = View.GONE
                binding.state2Tv.setOnClickListener {
                    //RESEND CODE
                    number?.let { it1 -> resendVerificationCode(it1, resendToken) }

                    //START TIMER 30S
                    binding.state1Tv.visibility = View.VISIBLE
                    binding.state2Tv.visibility = View.GONE
                    binding.state3Tv.visibility = View.GONE
                    timer.start()
                }
            }
            SendCodeState.SIGNUP -> {
                binding.state1Tv.visibility = View.GONE
                binding.state2Tv.visibility = View.GONE
                binding.state3Tv.visibility = View.VISIBLE

                if(isForgetPassword){
//                    binding.state3Tv.text = getText(R.string.forget_password_by_email)
                    binding.state3Tv.setOnClickListener {
                        // forget password BY EMAIL
                        var data = Intent()
                        data.putExtra(MH01_Input_Phone.PHONE_NUMBER, number)
                        data.putExtra(MH01_Input_Phone.IS_REQUEST_VERIFY_BY_EMAIL, true)
                        data.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, true)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    }
                }else{
//                    binding.state3Tv.text = getText(R.string.sign_up_with_email)
                    binding.state3Tv.setOnClickListener {
                        // SIGNUP BY EMAIL
                        var data = Intent()
                        data.putExtra(MH01_Input_Phone.PHONE_NUMBER, number)
                        data.putExtra(MH01_Input_Phone.IS_REQUEST_VERIFY_BY_EMAIL, true)
                        data.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, false)
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    }
                }

            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private var storedVerificationId: String? = ""
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    fun initPhoneAuth(number: String) {
        auth = Firebase.auth
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    simpleAlert(R.string.invalid_request)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    simpleAlert(R.string.the_sms_quota_for_the_project_has_been_exceeded)
                }

                // Show a message and update the UI
                //Ko nhan dc sms thi tien hanh dang ky
                setState(SendCodeState.SIGNUP)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

            }
        }
        // [END phone_auth_callbacks]

        //yc goi sms
        startPhoneNumberVerification(number)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        if(!verificationId.isNullOrEmpty()){
            // [START verify_with_code]
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential)
        }
    }

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    //xac thuc thanh cong
//                    toast(R.string.ok)

                    //read token of user
                    if (user != null) {
                        val dialog = createDialogLoading(R.string.reading_info)
                        dialog.show()

                        user.getIdToken(true).addOnCompleteListener {
                            dialog.dismiss()
                            if (isValidateInLocal) {
                                //verify in local
                                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    val phone = user.phoneNumber //chuan E164 +84912345678
                                    var data = Intent()
                                    data.putExtra(MH01_Input_Phone.PHONE_NUMBER, phone)
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                }
                            } else {
                                //verify in server
                                val token = it.result?.token
                                if (!token.isNullOrEmpty()) {
                                    var data = Intent()
                                    data.putExtra(MH01_Input_Phone.AUTHORIZATION_CODE, token)
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                }

                            }
                        }
                    }

                    //return data test
                    /*val data = Intent()
                    data.putExtra("AUTHORIZATION_CODE", "kakakakaka")
                    setResult(RESULT_OK, data)
                    finish()*/

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        binding.otpView.showError()
                        toast(R.string.the_verification_code_entered_was_invalid)
                    }
                    // Update UI

                }
            }
    }
    // [END sign_in_with_phone]

    //INIT//////////////////////////////////////////////////////////////////////////////////////////
    var isValidateInLocal = false
    var isForgetPassword = false
    private fun init() {
        val b: Bundle? = intent.extras
        if (b != null) {
            isValidateInLocal = b.getBoolean(MH01_Input_Phone.IS_VALIDATE_IN_LOCAL, false)
            isForgetPassword = b.getBoolean(MH01_Input_Phone.IS_FORGET_PASSWORD, false)

        }

    }
    //INIT//////////////////////////////////////////////////////////////////////////////////////////
}