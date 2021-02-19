package com.topceo.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.topceo.R
import com.topceo.shopping.ShoppingActivity
import com.workchat.core.chat.RecentChatType
import com.workchat.core.chat.RecentChat_Fragment
import com.workchat.core.chat.SearchUserChatFromRestApiActivity
import com.workchat.core.search.MH09_SearchActivity
import kotlinx.android.synthetic.main.activity_main_chat.*

class MainChatActivity : AppCompatActivity() {

    private lateinit var context: Context
    private val isForward = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)
        context = this

        /////////////////////////////////////////////////////////////////////////////////////////
        //#1
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //#2
        val f1 = RecentChat_Fragment.newInstance(RecentChatType.ALL, isForward, false)
        f1.setCanSearch(false)

        //#3
        fragmentTransaction.add(R.id.fragment_container, f1)

        //#4
        fragmentTransaction.commit()
        /////////////////////////////////////////////////////////////////////////////////////////

        //buttons
        backImg.setOnClickListener {
            finish()
        }

        shopImg.setOnClickListener {
            startActivity(Intent(context, ShoppingActivity::class.java))
        }

        addChatImg.setOnClickListener {
            val intent = Intent(context, SearchUserChatFromRestApiActivity::class.java)
            startActivity(intent)
        }

        //search
        searchTxt.setOnClickListener {
            val searchActivity = Intent(context, MH09_SearchActivity::class.java)
            searchActivity.putExtra(MH09_SearchActivity.SHOW_TAB_ALL, false)
            searchActivity.putExtra(MH09_SearchActivity.SHOW_TAB_CONTACT, false)
            startActivity(searchActivity)
        }



    }
}
