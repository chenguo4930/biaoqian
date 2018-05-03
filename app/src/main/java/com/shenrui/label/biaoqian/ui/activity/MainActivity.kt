package com.shenrui.label.biaoqian.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.luckongo.tthd.mvp.model.bean.SubStation
import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.constrant.AllSubStation.Companion.subStation
import com.shenrui.label.biaoqian.database.BookSqliteOpenHelper
import com.shenrui.label.biaoqian.database.SubStationDatabase
import com.shenrui.label.biaoqian.database.SubStationTable
import com.shenrui.label.biaoqian.utils.DataBaseUtil
import kotlinx.android.synthetic.main.activity_main.*
import me.weyye.hipermission.HiPermission
import me.weyye.hipermission.PermissionCallback
import me.weyye.hipermission.PermissionItem
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListener()
        getPermission()
        initData()
    }

    /**
     * 读取本地变电站数据库
     */
    private fun initData() {

        subStation = SubStationDatabase.use {
            select(SubStationTable.TABLE_NAME).parseList(
                    rowParser { sub_name: String, volLevel_id: Int, province_id: Int,
                                city_id: Int, sub_short_name: String, db_path: String ->
                        (
                                SubStation(sub_name, volLevel_id, province_id, city_id, sub_short_name, db_path))
                    })
        }
        Log.e("------", "--------------subStation.size = ${subStation?.size}")
        subStation?.forEach {
            Log.e("------", "--------------读取数据库it=$it")
        }
    }

    /**
     * 初始监听器
     */
    private fun initListener() {
        btn_open.setOnClickListener {
            startActivity(Intent(this, BiaoQianActivity::class.java))
        }
        btn_input.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            try {
                startActivityForResult(intent, REQUEST_CODE)
            } catch (ex: android.content.ActivityNotFoundException) {
                toast("请安装文件管理器")
            }
        }
    }

    private fun getPermission() {
        val permissionItems = ArrayList<PermissionItem>()
        permissionItems.add(PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入存储", R.drawable.permission_ic_camera))
        permissionItems.add(PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储", R.drawable.permission_ic_camera))
        permissionItems.add(PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "查找位置", R.drawable.permission_ic_camera))
        permissionItems.add(PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, "查找路径", R.drawable.permission_ic_camera))
        HiPermission.create(this)
                .permissions(permissionItems)
                .checkMutiPermission(object : PermissionCallback {
                    override fun onFinish() {

                    }

                    override fun onDeny(permission: String?, position: Int) {
                        toast("拒绝读取权限，无法获取数据库，请开启权限")
                    }

                    override fun onGuarantee(permission: String?, position: Int) {

                    }

                    override fun onClose() {

                    }

                })

    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            val uri = data.data
            var path: String? = null
            if ("file".equals(uri.scheme, ignoreCase = true)) {//使用第三方应用打开
                path = uri.path
                tv_file_path.text = path
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show()
                return
            }
            path = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                getPath(this, uri)
            } else {//4.4以下下系统调用方法
                getRealPathFromURI(uri)
            }
            tv_file_path.text = path
            if (path == null) {
                toast("没有找到文件路劲")
                return
            }
            //如果是“.db”结尾，则是数据库
            if (path.trim().endsWith(".db")) {
                readDB(path)
            } else {
                toast("你选择的文件不是\".db\"数据库文件")
            }
        }
    }

    /**
     * 读取数据库
     */
    private fun readDB(filePath: String) {
        val progressBar = ProgressDialog(this)
        progressBar.run {
            setMessage("正在读取数据库")
            setCanceledOnTouchOutside(false)
            show()
        }

        val dbName = filePath.split("/").last()
        val helper = BookSqliteOpenHelper(this, filePath, dbName)

        Observable.create(Observable.OnSubscribe<String> {
            val dbPath = helper.createDataBase()
            it.onNext(dbPath)
            it.onCompleted()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<String>() {
                    override fun onCompleted() {
                        progressBar.dismiss()
                        toast("成功读取数据库")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        toast("读取数据库失败，请检查数据库是否存在")
                    }

                    override fun onNext(dbPath: String) {
                        if (dbPath == "null") {
                            toast("数据库打开失败")
                        } else {
                            addToDb(dbPath)
                        }
                    }
                })
    }

    /**
     * 添加到数据库
     */
    private fun addToDb(dbPath: String) {
        val subStation = DataBaseUtil.getSubstation(dbPath)
        Log.e("----------", "----------subSation----${subStation.toString()}")
        SubStationDatabase.use {
            insert(SubStationTable.TABLE_NAME,
                    SubStationTable.SUB_NAME to subStation!!.sub_name,
                    SubStationTable.VOLLEVEL_ID to subStation.volLevel_id,
                    SubStationTable.PROVINCE_ID to subStation.province_id,
                    SubStationTable.CITY_ID to subStation.city_id,
                    SubStationTable.SUB_SHORT_NAME to subStation.sub_short_name,
                    SubStationTable.DB_PATH to subStation.db_path)
            Log.e("---------", "------------插入数据库成功")
        }

        initData()
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (null != cursor && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
            cursor.close()
        }
        return res
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi", "ObsoleteSdkInt")
    private fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.getAuthority()
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.getAuthority()
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.getAuthority()
    }

    /**
     *  将指定文件写入SD卡,说明一下,应用程序的数据库是存放到/data/data/包名/databases 下面
     */
    @Throws(IOException::class)
    private fun toSDWriteFile(fileName: String): String {
        // 获取assets下的数据库文件流
        val inputStream = this.baseContext.assets.open(fileName)

        // 获取应用包名
        val sPackage = this.packageName

        var mSaveFile: File? = File("/data/data/$sPackage/databases/")

        if (!mSaveFile!!.exists()) {
            mSaveFile.mkdirs()
        }
        val localFile = mSaveFile.absolutePath + "/" + fileName

        mSaveFile = File(localFile)

        if (mSaveFile.exists()) {
            mSaveFile.delete()
        }
        mSaveFile.createNewFile()

        val fos = FileOutputStream(mSaveFile, true)

        val buffer = ByteArray(400000)
        var count = inputStream.read(buffer)
        while (count > 0) {
            fos.write(buffer, 0, count)
            count = inputStream.read(buffer)
        }
        mSaveFile = null
        fos.close()
        inputStream.close()

        return localFile
    }
}

