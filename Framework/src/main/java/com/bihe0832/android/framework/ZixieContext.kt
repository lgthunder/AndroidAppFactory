package com.bihe0832.android.framework

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.bihe0832.android.lib.channel.ChannelTools
import com.bihe0832.android.lib.device.DeviceIDUtils
import com.bihe0832.android.lib.lifecycle.ActivityObserver
import com.bihe0832.android.lib.lifecycle.ApplicationObserver
import com.bihe0832.android.lib.lifecycle.LifecycleHelper
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.CommonDialog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.ui.toast.ToastUtil
import com.bihe0832.android.lib.utils.apk.APKUtils
import kotlin.system.exitProcess


/**
 * Created by hardyshi on 2017/6/27.
 *
 * 核心内容
 */
object ZixieContext {

    var screenWidth = 0
    var screenHeight = 0

    private var versionName = ""
    private var versionCode = 0L
    private var mDebug = true
    private var mOfficial = true
    private var mTag = "Tag_ZIXIE_1.0.0_1"

    @Synchronized
    fun init(application: Application, appIsDebug: Boolean, appIsOfficial: Boolean, appTag: String) {
        applicationContext = application.applicationContext
        mDebug = appIsDebug
        mOfficial = appIsOfficial
        mTag = appTag
        initModule({ ChannelTools.init(application, "DEBUG") }, false)
    }

    var applicationContext: Context? = null
        private set

    fun isDebug(): Boolean {
        return mDebug
    }

    fun isOfficial(): Boolean {
        return mOfficial
    }

    fun getVersionTag(): String {
        return mTag
    }

    val channelID: String
        get() = ChannelTools.getChannel()

    fun showDebugEditionToast() {
        if (!isOfficial()) {
            showToast("测试版本，请勿外泄~")
        }
    }

    //任何时候都弹
    fun showToast(msg: String) {
        ToastUtil.showShort(applicationContext, msg)
    }

    //任何时候都弹
    fun showLongToast(msg: String) {
        ToastUtil.showLong(applicationContext, msg)
    }

    //仅APP在前台弹
    fun showLongToastJustAPPFront(msg: String) {
        if (!ApplicationObserver.isAPPBackground()) {
            ToastUtil.showLong(applicationContext, msg)
        }
    }

    //仅APP在前台弹
    fun showToastJustAPPFront(msg: String) {
        if (!ApplicationObserver.isAPPBackground()) {
            ToastUtil.showShort(applicationContext, msg)
        }
    }

    //仅debug弹
    fun showDebug(msg: String) {
        if (isDebug()) {
            showToast(msg)
        }
    }

    fun showWaiting() {
        showToast("功能开发中，敬请期待~")
    }

    fun getVersionNameAndCode(): String {
        return getVersionName() + "." + getVersionCode()
    }

    fun getVersionName(): String {
        if (TextUtils.isEmpty(versionName)) {
            versionName = APKUtils.getAppVersionName(applicationContext)
        }
        return versionName
    }

    fun getVersionCode(): Long {
        if (versionCode < 1) {
            versionCode = APKUtils.getAppVersionCode(applicationContext)
        }
        return versionCode
    }

    fun isFirstStart(): Int {
        return LifecycleHelper.isFirstStart
    }

    fun getAPPLastVersionInstalledTime(): Long {
        return LifecycleHelper.getVersionInstalledTime()
    }

    fun getAPPInstalledTime(): Long {
        return LifecycleHelper.getAPPInstalledTime()
    }

    fun getAPPLastStartTime(): Long {
        return LifecycleHelper.getAPPLastStartTime()
    }

    fun getAPPUsedDays(): Int {
        return LifecycleHelper.getAPPUsedDays()
    }

    fun getAPPUsedTimes(): Int {
        return LifecycleHelper.getAPPUsedTimes()
    }

    fun getLogPath(): String {
        return if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(applicationContext!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            "${Environment.getExternalStorageDirectory().absolutePath}/zixie/Applog"
        } else {
            applicationContext!!.getExternalFilesDir("log").absolutePath
        }
    }

    fun getDeviceId(): String {
        return DeviceIDUtils.getAndroidId(applicationContext) ?: return ""
    }

    fun initModule(action: () -> Unit, canInitWithBackgroundThread: Boolean) {
        try {
            if (canInitWithBackgroundThread) {
                ThreadManager.getInstance().start { action() }
            } else {
                action()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 尽量使用页面归属的Activity，在没有页面的前提下，优先使用applicationContext，只有别无选择的场景，使用该方式
     */
    fun getCurrentActivity(): Activity? {
        return ActivityObserver.getCurrentActivity()
    }

    fun exitAPP() {
        ThreadManager.getInstance().start({
            ActivityObserver.getCurrentActivity()?.let { context ->
                (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)?.let {
                    it.killBackgroundProcesses(context.packageName)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                        for (appTask in it.appTasks) {
                            appTask.finishAndRemoveTask()
                        }
                        context.finishAndRemoveTask()
                    } else {
                        context.finish()
                    }
                }
            }

            ActivityObserver.getActivityList().forEach {
                it.finish()
            }

            android.os.Process.killProcess(android.os.Process.myPid())//获取PID
            exitProcess(1)//常规java、c#的标准退出法，返回值为0代表正常退出**
        }, 200L)
    }


    fun exitAPP(callbackListener: OnDialogListener?) {
        CommonDialog(getCurrentActivity()).apply {
            title = applicationContext!!.resources.getString(R.string.common_reminder_title)
            content = String.format(applicationContext!!.resources.getString(R.string.exist_msg), applicationContext!!.resources.getString(R.string.app_name))


            setCancelable(true)
            positive = applicationContext!!.resources.getString(R.string.comfirm)
            negative = applicationContext!!.resources.getString(R.string.cancel)
            setOnClickBottomListener(object : OnDialogListener {
                override fun onPositiveClick() {
                    callbackListener?.onPositiveClick()
                    dismiss()
                    exitAPP()
                }

                override fun onNegativeClick() {
                    callbackListener?.onNegativeClick()
                    dismiss()
                }

                override fun onCancel() {
                    callbackListener?.onCancel()
                }
            })
        }.show()
    }
}
