package com.myxteam.phone_verification

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myxteam.phone_verification.databinding.ActivityMh01InputPhoneBinding

class MH01_Input_Phone : AppCompatActivity() {
    companion object {
        const val PHONE_NUMBER = "PHONE_NUMBER"
        const val AUTHORIZATION_CODE = "AUTHORIZATION_CODE"
        const val IS_VALIDATE_IN_LOCAL = "IS_VALIDATE_IN_LOCAL"
        const val IS_FORGET_PASSWORD = "IS_FORGET_PASSWORD"

        const val IS_REQUEST_VERIFY_BY_EMAIL = "IS_REQUEST_VERIFY_BY_EMAIL"

    }


    lateinit var context: Context
    lateinit var binding: ActivityMh01InputPhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        binding = ActivityMh01InputPhoneBinding.inflate(layoutInflater)


        //toolbar
        setSupportActionBar(binding.mToolbar)
        binding.mToolbar.setNavigationOnClickListener { finish() }

        //views
        binding.ccp.registerPhoneNumberTextView(binding.phoneNumberEdt)
        binding.readBtn.setOnClickListener {
//            val buffer = StringBuffer()
//            buffer.append(binding.ccp.phoneNumber).append("\n")
//            buffer.append(binding.ccp.fullNumber).append("\n")
            /*buffer.append(binding.ccp.number).append("\n") //+84938xxxxxx
            buffer.append(binding.ccp.fullNumberWithPlus).append("\n") //+840938xxxxxx
            buffer.append(binding.ccp.isValid).append("\n")
            binding.logsTv.text = buffer.toString()*/

            if (binding.ccp.isValid) {
                val number = binding.ccp.number

                if (isForgetPassword) {//tra ve so dien thoai de kiem tra ton tai
                    val data = Intent()
                    data.putExtra(PHONE_NUMBER, number)
                    setResult(RESULT_OK, data)
                    finish()
                } else {//vao nhap code
                    //verify code
                    MH02_Input_Code.start(context, number, isValidateInLocal, isForgetPassword)
                    finish()
                }


            } else {
//                Toast.makeText(context, R.string.phone_invalid, Toast.LENGTH_SHORT).show()
                simpleAlert(R.string.phone_invalid)
            }

        }


        setContentView(binding.root)
        init()
    }

    //INIT//////////////////////////////////////////////////////////////////////////////////////////
    var isValidateInLocal = false
    var isForgetPassword = false
    private fun init() {
        val b: Bundle? = intent.extras
        if (b != null) {
            isValidateInLocal = b.getBoolean(IS_VALIDATE_IN_LOCAL, false)
            isForgetPassword = b.getBoolean(IS_FORGET_PASSWORD, false)
        }

    }
    //INIT//////////////////////////////////////////////////////////////////////////////////////////


}