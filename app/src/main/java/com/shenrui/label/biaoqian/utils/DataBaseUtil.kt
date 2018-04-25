package com.shenrui.label.biaoqian.utils

import android.database.sqlite.SQLiteDatabase
import com.luckongo.tthd.mvp.model.bean.*

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
         * 读取屏柜的数据
         */
        fun getPanelByCode(dbPath: String, code: String): ArrayList<Panel> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Panel", null, "panel_code == ? ", arrayOf(code),
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

        /**
         * 读取区域交换机的数据
         */
        fun getSwitch(dbPath: String): ArrayList<Switch> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Switch", null, null, null,
                    null, null, null)
            val switchList = ArrayList<Switch>()
            while (cursor.moveToNext()) {
                val switch_id = cursor.getInt(cursor.getColumnIndex("switch_id"))
                val switch_name = cursor.getString(cursor.getColumnIndex("switch_name"))
                val switch_code = cursor.getString(cursor.getColumnIndex("switch_code"))
                val panel_id = cursor.getInt(cursor.getColumnIndex("panel_id"))
                val model_id = cursor.getInt(cursor.getColumnIndex("model_id"))

                switchList.add(Switch(switch_id, switch_name, switch_code, panel_id, model_id))
            }
            cursor.close()
            return switchList
        }

        /**
         *
         */
        fun getModel(dbPath: String): ArrayList<Model> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Model", null, null, null,
                    null, null, null)
            val modelList = ArrayList<Model>()
            while (cursor.moveToNext()) {
                val model_id = cursor.getInt(cursor.getColumnIndex("model_id"))
                val model_name = cursor.getString(cursor.getColumnIndex("model_name"))
                val model_type = cursor.getInt(cursor.getColumnIndex("model_type"))

                modelList.add(Model(model_id, model_name, model_type))
            }
            cursor.close()
            return modelList
        }

        /**
         *
         */
        fun getDevicePort(dbPath: String): ArrayList<DevicePort> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("DevicePort", null, null, null,
                    null, null, null)
            val devicePortList = ArrayList<DevicePort>()
            while (cursor.moveToNext()) {
                val model_id = cursor.getInt(cursor.getColumnIndex("model_id"))
                val device_port = cursor.getString(cursor.getColumnIndex("device_port"))
                val port_type = cursor.getString(cursor.getColumnIndex("port_type"))
                val port_plug = cursor.getString(cursor.getColumnIndex("port_plug"))
                val rt_type = cursor.getInt(cursor.getColumnIndex("rt_type"))
                val timetick = cursor.getInt(cursor.getColumnIndex("timetick"))
                val port_desc = cursor.getString(cursor.getColumnIndex("port_desc"))

                devicePortList.add(DevicePort(model_id, device_port, port_type, port_plug,
                        rt_type, timetick, port_desc))
            }
            cursor.close()
            return devicePortList
        }


        /**
         *
         */
        fun getSwitchPort(dbPath: String): ArrayList<SwitchPort> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("SwitchPort", null, null, null,
                    null, null, null)
            val switchPortList = ArrayList<SwitchPort>()
            while (cursor.moveToNext()) {
                val switch_id = cursor.getInt(cursor.getColumnIndex("switch_id"))
                val switch_port = cursor.getString(cursor.getColumnIndex("switch_port"))

                switchPortList.add(SwitchPort(switch_id, switch_port))
            }
            cursor.close()
            return switchPortList
        }

        /**
         * 查找odf
         */
        fun getODF(dbPath: String): ArrayList<ODF> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("ODF", null, null, null,
                    null, null, null)
            val oDFList = ArrayList<ODF>()
            while (cursor.moveToNext()) {
                val odf_id = cursor.getInt(cursor.getColumnIndex("odf_id"))
                val panel_id = cursor.getInt(cursor.getColumnIndex("panel_id"))
                val odf_code = cursor.getString(cursor.getColumnIndex("odf_code"))
                val odf_layer = cursor.getString(cursor.getColumnIndex("odf_layer"))
                val odf_port = cursor.getString(cursor.getColumnIndex("odf_port"))
                val odf_port_type = cursor.getInt(cursor.getColumnIndex("odf_port_type"))

                oDFList.add(ODF(odf_id, panel_id, odf_code, odf_layer, odf_port, odf_port_type))
            }
            cursor.close()
            return oDFList
        }

        /**
         *
         */
        fun getODFConnection(dbPath: String): ArrayList<ODFConnection> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("ODFConnection", null, null, null,
                    null, null, null)
            val oDFConnectionList = ArrayList<ODFConnection>()
            while (cursor.moveToNext()) {
                val odf_id = cursor.getInt(cursor.getColumnIndex("odf_id"))
                val optical_cable_number = cursor.getString(cursor.getColumnIndex("optical_cable_number"))
                val optical_fiber_number = cursor.getInt(cursor.getColumnIndex("optical_fiber_number"))
                val optical_fiber_color = cursor.getInt(cursor.getColumnIndex("optical_fiber_color"))
                val internal_device_type = cursor.getInt(cursor.getColumnIndex("internal_device_type"))
                val internal_device_id = cursor.getInt(cursor.getColumnIndex("internal_device_id"))
                val internal_device_port = cursor.getString(cursor.getColumnIndex("internal_device_port"))
                val internal_rt_type = cursor.getInt(cursor.getColumnIndex("internal_rt_type"))
                val internal_optical_fiber_number = cursor.getString(cursor.getColumnIndex("internal_optical_fiber_number"))
                val external_odf_id = cursor.getInt(cursor.getColumnIndex("external_odf_id"))

                oDFConnectionList.add(ODFConnection(odf_id, optical_cable_number, optical_fiber_number,
                        optical_fiber_color, internal_device_type, internal_device_id,
                        internal_device_port, internal_rt_type, internal_optical_fiber_number, external_odf_id))
            }
            cursor.close()
            return oDFConnectionList
        }

        /**
         *
         */
        fun getTailFiber(dbPath: String): ArrayList<TailFiber> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("TailFiber", null, null, null,
                    null, null, null)
            val tailFiberList = ArrayList<TailFiber>()
            while (cursor.moveToNext()) {
                val tail_fiber_id = cursor.getInt(cursor.getColumnIndex("tail_fiber_id"))
                val tail_cable_number = cursor.getString(cursor.getColumnIndex("tail_cable_number"))
                val tail_fiber_number = cursor.getInt(cursor.getColumnIndex("tail_fiber_number"))
                val tail_fiber_color = cursor.getInt(cursor.getColumnIndex("tail_fiber_color"))
                val tail_fiber_desc = cursor.getString(cursor.getColumnIndex("tail_fiber_desc"))

                tailFiberList.add(TailFiber(tail_fiber_id, tail_cable_number, tail_fiber_number,
                        tail_fiber_color, tail_fiber_desc))
            }
            cursor.close()
            return tailFiberList
        }

        /**
         *
         */
        fun getDeviceConnection(dbPath: String): ArrayList<DeviceConnection> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("DeviceConnection", null, null, null,
                    null, null, null)
            val deviceConnectionList = ArrayList<DeviceConnection>()
            while (cursor.moveToNext()) {
                val from_id = cursor.getInt(cursor.getColumnIndex("from_id"))
                val from_port = cursor.getString(cursor.getColumnIndex("from_port"))
                val to_id = cursor.getInt(cursor.getColumnIndex("to_id"))
                val to_port = cursor.getInt(cursor.getColumnIndex("to_port"))
                val to_dev_type = cursor.getString(cursor.getColumnIndex("to_dev_type"))
                val tail_fiber_tx_id = cursor.getInt(cursor.getColumnIndex("tail_fiber_tx_id"))
                val tail_fiber_rx_id = cursor.getInt(cursor.getColumnIndex("tail_fiber_rx_id"))

                deviceConnectionList.add(DeviceConnection(from_id, from_port, to_id, to_port,
                        to_dev_type, tail_fiber_tx_id, tail_fiber_rx_id))
            }
            cursor.close()
            return deviceConnectionList
        }

        /**
         *
         */
        fun getSwitchConnection(dbPath: String): ArrayList<SwitchConnection> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("SwitchConnection", null, null, null,
                    null, null, null)
            val switchConnectionList = ArrayList<SwitchConnection>()
            while (cursor.moveToNext()) {
                val from_id = cursor.getInt(cursor.getColumnIndex("from_id"))
                val from_port = cursor.getString(cursor.getColumnIndex("from_port"))
                val to_id = cursor.getInt(cursor.getColumnIndex("to_id"))
                val to_port = cursor.getString(cursor.getColumnIndex("to_port"))
                val to_dev_type = cursor.getString(cursor.getColumnIndex("to_dev_type"))
                val tail_fiber_tx_id = cursor.getInt(cursor.getColumnIndex("tail_fiber_tx_id"))
                val tail_fiber_rx_id = cursor.getInt(cursor.getColumnIndex("tail_fiber_rx_id"))

                switchConnectionList.add(SwitchConnection(from_id, from_port, to_id, to_port,
                        to_dev_type, tail_fiber_tx_id, tail_fiber_rx_id))
            }
            cursor.close()
            return switchConnectionList
        }

        /**
         *
         */
        fun getGSEModel(dbPath: String): ArrayList<GSEModel> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("GSEModel", null, null, null,
                    null, null, null)
            val gSEModelList = ArrayList<GSEModel>()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val model_id = cursor.getInt(cursor.getColumnIndex("model_id"))
                val io_type = cursor.getInt(cursor.getColumnIndex("io_type"))
                val gse_addr = cursor.getString(cursor.getColumnIndex("gse_addr"))
                val gse_desc = cursor.getString(cursor.getColumnIndex("gse_desc"))

                gSEModelList.add(GSEModel(id, model_id, io_type, gse_addr, gse_desc))
            }
            cursor.close()
            return gSEModelList
        }

        /**
         *
         */
        fun getExcelGSEModel(dbPath: String): ArrayList<ExcelGSEModel> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("ExcelGSEModel", null, null, null,
                    null, null, null)
            val excelGSEModelList = ArrayList<ExcelGSEModel>()
            while (cursor.moveToNext()) {
                val excel_id = cursor.getInt(cursor.getColumnIndex("excel_id"))
                val excel_model_id = cursor.getInt(cursor.getColumnIndex("excel_model_id"))
                val excel_io_type = cursor.getInt(cursor.getColumnIndex("excel_io_type"))
                val excel_gse_addr = cursor.getString(cursor.getColumnIndex("excel_gse_addr"))
                val excel_gse_desc = cursor.getString(cursor.getColumnIndex("excel_gse_desc"))
                excelGSEModelList.add(ExcelGSEModel(excel_id, excel_model_id, excel_io_type,
                        excel_gse_addr, excel_gse_desc))
            }
            cursor.close()
            return excelGSEModelList
        }

        /**
         *
         */
        fun getInputs(dbPath: String): ArrayList<Inputs> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Inputs", null, null, null,
                    null, null, null)
            val inputsList = ArrayList<Inputs>()
            while (cursor.moveToNext()) {
                val model_id_from = cursor.getInt(cursor.getColumnIndex("model_id_from"))
                val from_index = cursor.getInt(cursor.getColumnIndex("from_index"))
                val model_id_to = cursor.getInt(cursor.getColumnIndex("model_id_to"))
                val to_index = cursor.getInt(cursor.getColumnIndex("to_index"))
                val is_goose = cursor.getInt(cursor.getColumnIndex("is_goose"))
                val p2p = cursor.getInt(cursor.getColumnIndex("p2p"))
                val in_addr = cursor.getString(cursor.getColumnIndex("in_addr"))
                val desc_to = cursor.getString(cursor.getColumnIndex("desc_to"))
                val out_addr = cursor.getString(cursor.getColumnIndex("out_addr"))
                val desc_from = cursor.getString(cursor.getColumnIndex("desc_from"))
                val input_desc = cursor.getString(cursor.getColumnIndex("input_desc"))
                val port_to = cursor.getString(cursor.getColumnIndex("port_to"))

                inputsList.add(Inputs(model_id_from, from_index, model_id_to, to_index, is_goose,
                        p2p, in_addr, desc_to, out_addr, desc_from, input_desc, port_to))
            }
            cursor.close()
            return inputsList
        }


        /**
         * from: 输出设备
         * to:输入设备
         * portTo:输入设备的端口号
         */
        fun getInputsFilter(dbPath: String, from: String, to: String, portTo: String): ArrayList<Inputs> {
            val database = SQLiteDatabase.openOrCreateDatabase(dbPath, null)
            val cursor = database.query("Inputs", null, "model_id_from == ? and model_id_to == ? and port_to == ?",
                    arrayOf(from, to, portTo), null, null, null)
//            val cursor = database.query("Inputs", null, "model_id_from == ? and model_id_to == ? ",
//                    arrayOf(from, to), null, null, null)
            val inputsList = ArrayList<Inputs>()
            while (cursor.moveToNext()) {
                val model_id_from = cursor.getInt(cursor.getColumnIndex("model_id_from"))
                val from_index = cursor.getInt(cursor.getColumnIndex("from_index"))
                val model_id_to = cursor.getInt(cursor.getColumnIndex("model_id_to"))
                val to_index = cursor.getInt(cursor.getColumnIndex("to_index"))
                val is_goose = cursor.getInt(cursor.getColumnIndex("is_goose"))
                val p2p = cursor.getInt(cursor.getColumnIndex("p2p"))
                val in_addr = cursor.getString(cursor.getColumnIndex("in_addr"))
                val desc_to = cursor.getString(cursor.getColumnIndex("desc_to"))
                val out_addr = cursor.getString(cursor.getColumnIndex("out_addr"))
                val desc_from = cursor.getString(cursor.getColumnIndex("desc_from"))
                val input_desc = cursor.getString(cursor.getColumnIndex("input_desc"))
                val port_to = cursor.getString(cursor.getColumnIndex("port_to"))

                inputsList.add(Inputs(model_id_from, from_index, model_id_to, to_index, is_goose,
                        p2p, in_addr, desc_to, out_addr, desc_from, input_desc, port_to))
            }
            cursor.close()
            return inputsList
        }
    }
}