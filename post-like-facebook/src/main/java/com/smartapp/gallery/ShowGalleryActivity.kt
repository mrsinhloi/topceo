package com.smartapp.gallery

import android.Manifest
import android.app.DownloadManager
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.smartapp.post_like_facebook.R
import com.smartapp.swipeback.SwipeBackActivity
import com.smartapp.swipeback.SwipeBackLayout
import kotlinx.android.synthetic.main.activity_view_images.*
import kotlinx.coroutines.NonCancellable.cancel
import java.util.*

class ShowGalleryActivity : SwipeBackActivity() {
    companion object {
        const val LIST_PATH: String = "LIST_PATH"
        const val POSITION_SELECTED: String = "POSITION_SELECTED"
    }

    var paths: java.util.ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //android O fix bug orientation
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }

        setContentView(R.layout.activity_view_images)

        setDragEdge(SwipeBackLayout.DragEdge.TOP)
        imgBack.setOnClickListener { finish() }
        imgSave.setOnClickListener { save() }

        viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setTitlePage(position + 1)
                positionSelected = position
            }

        })

        if (intent != null) {
            paths = intent.getStringArrayListExtra(LIST_PATH) as ArrayList<String>
            if (paths != null && paths.size > 0) {
                val adapter = ShowGalleryAdapter(this, paths, object : OnClickImage {
                    override fun onClicked() {
                        relativeMenu.visibility = if (relativeMenu.visibility == View.GONE) View.VISIBLE else View.GONE
                    }

                })
                viewPager2.adapter = adapter

                val positionInit = intent.getIntExtra(POSITION_SELECTED, 0)
                if (positionInit >= 0) {
                    positionSelected = positionInit
                    setTitlePage(positionInit + 1) //vd:1/9
                    viewPager2.setCurrentItem(positionInit)
                }
            }
        }

    }

    private fun setTitlePage(page: Int) {
        var number = page
        if (paths.size == 0) number = 0
        txtTitle.text = "$number/${paths.size}"
    }


    var positionSelected = 0
    fun save() {
        if (paths.size > 0) {
            val path = paths[positionSelected]
            val isTrue: Boolean = checkPermission()
            if (isTrue) {
                val dialog = AlertDialog.Builder(this)
                dialog.setMessage(R.string.confirm_download)
                dialog.setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                        val name: String = getFileNameFromUrl(path)
                        downloadImage(path, name)
                    }

                })
                dialog.setNegativeButton(R.string.cancel, null)
                dialog.create().show()

            } else {
                requestPermission(PERMISSION_REQUEST_STORAGE)
            }
        }
    }

    private val PERMISSION_REQUEST_STORAGE = 108
    var arrPermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun checkPermission(): Boolean {
        var result = true
        for (i in arrPermissions.indices) {
            val grant = ContextCompat.checkSelfPermission(applicationContext, arrPermissions[i])
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false
                break
            }
        }
        return result
    }

    private fun requestPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrPermissions, requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_STORAGE) {

        }
    }


    fun getFileNameFromUrl(urlString: String): String {
        var urlString = urlString.replace("\\", "/")
        var name = urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?".toRegex()).toTypedArray()[0].split("#".toRegex()).toTypedArray()[0]
        if (name.contains("%20")) {
            name = name.replace("%20", " ")
        }
        return name
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //http://www.codexpedia.com/android/android-downloadmanager-example/
    fun downloadImage(url1: String, outputFileName1: String) {
        var url = url1
        var outputFileName = outputFileName1
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(outputFileName)) {
            try {
                //replace blank space
                url = url.replace(" ".toRegex(), "%20")
                outputFileName = outputFileName.replace(" ".toRegex(), "_")
                val request = DownloadManager.Request(Uri.parse(url))
                request.setDescription("Download image to device")
                request.setTitle("Download $outputFileName")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.allowScanningByMediaScanner()
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, outputFileName)//getString(R.string.app_name)
                request.setVisibleInDownloadsUi(true)
                request.setMimeType("image/*")

                //Log.d("MainActivity: ", "download folder>>>>" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

                // get download service and enqueue file
                val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)


                Toast.makeText(this, R.string.file_downloading_in_app_bar, Toast.LENGTH_SHORT).show()
                /*val dialog = AlertDialog.Builder(this)
                dialog.setMessage(R.string.file_downloading_in_app_bar)
                dialog.setPositiveButton(R.string.ok, null)
                dialog.create().show()*/

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
