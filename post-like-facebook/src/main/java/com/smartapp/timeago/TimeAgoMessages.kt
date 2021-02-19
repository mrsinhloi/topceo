package com.smartapp.timeago

import java.text.MessageFormat
import java.util.*

class TimeAgoMessages
/**
 * Instantiates a new time ago messages.
 */
private constructor() {
    /**
     * The resource bundle for holding the language messages.
     */
    private var bundle: ResourceBundle? = null

    /**
     * Gets the property value.
     *
     * @param property the property key
     * @return the property value
     */
    fun getPropertyValue(property: String): String {
        return bundle!!.getString(property)
    }

    /**
     * Gets the property value.
     *
     * @param property the property key
     * @param values   the property values
     * @return the property value
     */
    fun getPropertyValue(property: String, vararg values: Any): String {
        val propertyVal = getPropertyValue(property)
        return MessageFormat.format(propertyVal, *values)
    }

    /**
     * The Inner Class Builder for *TimeAgoMessages*.
     *
     * @author marlonlom
     */
    class Builder {

        /**
         * The inner bundle.
         */
        private var innerBundle: ResourceBundle? = null

        /**
         * Builds the TimeAgoMessages instance.
         *
         * @return the time ago messages instance.
         */
        fun build(): TimeAgoMessages {
            val resources = TimeAgoMessages()
            resources.bundle = this.innerBundle
            return resources
        }

        /**
         * Build messages with the default locale.
         *
         * @return the builder
         */
        fun defaultLocale(): Builder {
            this.innerBundle = ResourceBundle.getBundle(MESSAGES)
            return this
        }

        /**
         * Build messages with the selected locale.
         *
         * @param locale the locale
         * @return the builder
         */
        fun withLocale(locale: Locale): Builder {
            this.innerBundle = ResourceBundle.getBundle(MESSAGES, locale)
            return this
        }
    }

    companion object {

        /**
         * The property path for MESSAGES.
         */
        private const val MESSAGES = "com.smartapp.timeago.messages"
    }
}