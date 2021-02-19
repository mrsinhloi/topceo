package com.topceo.socialspost.facebook

import android.content.Context
import com.topceo.socialspost.common.EasyPreferences
import java.util.*

class Page(val id: String, val name: String, val access_token: String) {


    companion object {
        const val LIST_PAGE_ID = "LIST_PAGE_ID"

        fun getArrayName(list: ArrayList<Page>): Array<String?> {
            var array = arrayOf<String?>()
            if (list.size > 0) {
                array = arrayOfNulls<String?>(list.size)
                for (i in 0 until list.size) {
                    val item = list.get(i)
                    array[i] = item.name
                }
            }
            return array
        }

        fun saveNameSelected(list: ArrayList<Page>?, checkedItems: BooleanArray?, context: Context) {
            //luu danh sach id cua page
            //2 list phai tuong dong nhau ve size
            var ids = ""
            if (checkedItems != null && list != null && list.size == checkedItems.size) {
                for (i in 0 until list.size) {
                    if (checkedItems[i]) {
                        if (i == 0) {
                            ids = list.get(i).id
                        } else {
                            ids += ",${list.get(i).id}"
                        }
                    }
                }
            }
            val prefs = EasyPreferences.defaultPrefs(context)
            prefs.edit().putString(LIST_PAGE_ID, ids).apply()
        }

        fun getPageSelected(list: ArrayList<Page>, context: Context): ArrayList<Page> {
            var selecteds = ArrayList<Page>()

            val prefs = EasyPreferences.defaultPrefs(context)
            val ids = prefs.getString(LIST_PAGE_ID, "")
            if (!ids.isNullOrEmpty()) {
                val idList = ids.split(",")
                if (idList.isNotEmpty()) {
                    for (i in 0 until list.size) {
                        val page = list.get(i)
                        if (idList.contains(page.id)) {
                            selecteds.add(page)
                        }
                    }
                }
            }
            return selecteds
        }

        fun getCheckedItems(list: ArrayList<Page>, context: Context): BooleanArray? {
            val prefs = EasyPreferences.defaultPrefs(context)
            val ids = prefs.getString(LIST_PAGE_ID, "")
            if (!ids.isNullOrEmpty()) {
                val listId = ids.split(",")
                if (listId.size > 0) {
                    var checkedItems = BooleanArray(list.size)

                    for (i in 0 until list.size) {
                        val page = list.get(i)
                        checkedItems[i] = listId.contains(page.id)
                    }

                    return checkedItems
                }
            }

            return null
        }

        fun getArrayNameSample(): Array<String?> {
            return arrayOf("One", "Two Two Two Two Two Two Two Two Two Two Two Two Two Two Two Two Two Two Two Two ", "Three ThreeThreeThreeThreeThreeThreeThreeThreeThreeThreeThreeThreeThreeThree", "Four", "Five", "Six")
        }
    }
}