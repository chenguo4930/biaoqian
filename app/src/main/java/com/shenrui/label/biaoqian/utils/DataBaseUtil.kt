package com.shenrui.label.biaoqian.utils

import android.database.sqlite.SQLiteDatabase
import com.luckongo.tthd.mvp.model.bean.Device
import com.luckongo.tthd.mvp.model.bean.Panel
import com.luckongo.tthd.mvp.model.bean.Region
import com.luckongo.tthd.mvp.model.bean.SubStation

/**
 * 读取数据库的工具类
 * Created by chengguo on 18-3-13.
 */
class DataBaseUtil {
    companion object {

        /**
         * 读取变电站数据
         */
        fun getSubstation(dbPath: String): SubStation? {

            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("SubStation", null, null, null, null, null, null)
            var subStation: SubStation? = null
            while (cursor.moveToNext()) {
                val sub_name = cursor.getString(cursor.getColumnIndex("sub_name"))
                val volLevel_id = cursor.getInt(cursor.getColumnIndex("volLevel_id"))
                val provice_id = cursor.getInt(cursor.getColumnIndex("province_id"))
                val city_id = cursor.getInt(cursor.getColumnIndex("city_id"))
                val sub_short_name = cursor.getString(cursor.getColumnIndex("sub_short_name"))
                subStation = SubStation(sub_name, volLevel_id, provice_id, city_id, sub_short_name, dbPath)
            }
            cursor.close()
            return subStation
        }

        /**
         * 读取区域的数据
         */
        fun getRegioin(dbPath: String): ArrayList<Region> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Region", null, null, null,
                    null, null, null)
            val regionList = ArrayList<Region>()
            while (cursor.moveToNext()) {
                val region_id = cursor.getInt(cursor.getColumnIndex("region_id"))
                val region_name = cursor.getString(cursor.getColumnIndex("region_name"))
                val region_code = cursor.getString(cursor.getColumnIndex("region_code"))
                regionList.add(Region(region_id, region_name, region_code))
            }
            cursor.close()
            return regionList
        }

        /**
         * 读取屏柜的数据
         */
        fun getPanel(dbPath: String): ArrayList<Panel> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Panel", null, null, null,
                    null, null, null)
            val panleList = ArrayList<Panel>()
            while (cursor.moveToNext()) {
                val panel_id = cursor.getInt(cursor.getColumnIndex("panel_id"))
                val panel_name = cursor.getString(cursor.getColumnIndex("panel_name"))
                val panel_code = cursor.getString(cursor.getColumnIndex("panel_code"))
                val region_id = cursor.getInt(cursor.getColumnIndex("region_id"))
                panleList.add(Panel(panel_id, panel_name, panel_code, region_id))
            }
            cursor.close()
            return panleList
        }

        /**
         * 读取区域设备的数据
         */
        fun getDevice(dbPath: String): ArrayList<Device> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Device", null, null, null,
                    null, null, null)
            val deviceList = ArrayList<Device>()
            while (cursor.moveToNext()) {
                val device_id = cursor.getInt(cursor.getColumnIndex("device_id"))
                val device_desc = cursor.getString(cursor.getColumnIndex("device_desc"))
                val device_iedname = cursor.getString(cursor.getColumnIndex("device_iedname"))
                val device_code = cursor.getString(cursor.getColumnIndex("device_code"))
                val panel_id = cursor.getInt(cursor.getColumnIndex("panel_id"))
                val model_id = cursor.getInt(cursor.getColumnIndex("model_id"))
                deviceList.add(Device(device_id, device_desc, device_iedname, device_code, panel_id, model_id))
            }
            cursor.close()
            return deviceList
        }
    }
}