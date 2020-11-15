package com.bihe0832.android.test.base

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.os.StrictMode
import com.bihe0832.android.app.router.APPFactoryRouter
import com.bihe0832.android.app.router.openWebPage
import com.bihe0832.android.framework.ui.list.CommonListActivity
import com.bihe0832.android.framework.ui.list.CommonListLiveData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.text.DebugTools
import com.bihe0832.android.lib.text.InputDialogCompletedCallback
import com.bihe0832.android.test.R
import com.bihe0832.android.test.base.item.TestItemData

abstract class BaseTestActivity : CommonListActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        }
    }

    open fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestItemData("test"))
        }
    }

    val mDataList by lazy {
        ArrayList<CardBaseModule>().apply {
            addAll(getDataList())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(resources.getColor(R.color.white))
    }

    override fun getDataLiveData(): CommonListLiveData {
        return object : CommonListLiveData() {
            override fun fetchData() {
                postValue(mDataList)
            }

            override fun clearData() {
                mDataList.clear()
            }

            override fun loadMore() {

            }

            override fun hasMore(): Boolean {
                return false
            }

            override fun canRefresh(): Boolean {
                return false
            }

            override fun getEmptyText(): String {
                return ""
            }
        }
    }

    fun showInputDialog(titleName: String, msg: String, defaultValue: String, listener: InputDialogCompletedCallback) {
        DebugTools.showInputDialog(this, titleName, msg, defaultValue, listener)
    }

    protected fun openWeb(url: String) {
        APPFactoryRouter.openWebPage(url)
    }

}