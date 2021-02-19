package com.topceo.language

import android.content.Context
import com.topceo.db.TinyDB

object LocalizationUtil {

    const val SELECTED_LANGUAGE_KEY = "SELECTED_LANGUAGE_KEY"
    const val LANGUAGE_VI = "vi"
    const val LANGUAGE_EN = "en"

    fun isVietnamese(context: Context): Boolean {
        val db = TinyDB(context)
        val languageSelected = db.getString(SELECTED_LANGUAGE_KEY, LANGUAGE_VI)
        return languageSelected == LANGUAGE_VI
    }

    /*fun applyLanguageContext(context: Context): Context {
        var mContext = context
        try {

            val db = TinyDB(context)
            val language = db.getString(SELECTED_LANGUAGE_KEY, LANGUAGE_VI)


            val locale = Locale(language)
            val resources: Resources = context.resources
            val configuration = context.resources.configuration

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)

                val localeList = LocaleList(locale) // 2
                LocaleList.setDefault(localeList) // 3
                configuration.setLocales(localeList) // 4
                mContext = context.createConfigurationContext(configuration)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale)
                mContext = context.createConfigurationContext(configuration) // 6
            } else {
                configuration.locale = locale
                resources.updateConfiguration(configuration, resources.displayMetrics) // 7
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        return mContext
    }*/

}