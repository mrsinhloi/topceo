package com.myxteam.phone_verification

/**
 * GỞI LẠI THÊM 1 LẦN, NẾU KHÔNG ĐƯỢC THÌ ĐĂNG KÝ BẰNG EMAIL
 * COUNTDOWN-> RESEND-> COUNTDOWN->SIGNUP
 */
enum class SendCodeState {
    COUNTDOWN, RESEND, SIGNUP
}