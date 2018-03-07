package com.luckongo.tthd.mvp.model.bean


/**
 *  sub_name : 变电站名称
 *  volLevel_id : 电压等级索引Id
 *  province_id : 省索引Id
 *  city_id : 市索引Id
 *  sub_short_name : 变电站名称拼音缩写
 */
data class SubStation(val sub_name: String, val volLevel_id: Int, val province_id: Int,
                      val city_id: Int, val sub_short_name: String)

/**
 * vol_id : 电压Id
 * vol_name : 电压等级
 *  vol_desc : 电压描述
 */
data class VolTage(val vol_id: Int, val vol_name: String, val vol_desc: String)

/**
 * area_id ：省Id
 * area_name : 省名称
 * fid : 省市从属Id
 * area_short_name : 省市拼音缩写
 */
data class Area(val area_id: Int, val area_name: String, val fid: Int, val area_short_name: String)

/**
 * region_id ：区域Id
 * region_name : 区域名称
 * region_code : 区域编号
 */
data class Region(val region_id: Int, val region_name: String, val region_code: String)

/**
 * panel_id : 屏柜Id
 * region_id ：所属区域Id
 */
data class Panel(val panel_id: Int, val panel_name: String, val panel_code: String, val region_id: Int)

/**
 * 装置Id
 * 装置描述
 * 装置IED名称
 * 装置编号
 * 所属区域Id
 * 所属模型Id
 */
data class Device(val device_id: Int, val device_desc: String, val device_iedname: String,
                  val device_code: String, val panel_id: Int, val model_id: Int)

/**
 * 交换机Id
 * 交换机名称
 * 交换机编号
 * 所属屏柜Id
 * 所说模型Id
 */
data class Switch(val switch_id: Int, val switch_name: String, val switch_code: String,
                  val panel_id: Int, val mode_id: Int)

/**
 * 模型Id
 * 模型名称
 * 模型类型（1：装置ICD，2：装置Excel， 3：交换机IPCD）
 */
data class Model(val model_id: Int, val mode_name: String, val model_type: Int)

/**
 * 装置模型Id
 * 装置端口名称
 * 端口类型（"FOC"、"Radio"、"100BaseT"）
 * 端口接口类型（"ST、"SC"、"LC"、"FC"、"MTRJ"、"RJ45"）
 * 收发类型（0: 收口 1: 发口  2: 收、发口）
 * 是否为校时口（0: 否  1: 是）
 * 端口作用描述
 */
data class DevicePort(val model_id: Int, val device_port: String, val port_type: String,
                      val port_plug: String, val rt_type: Int, val timetick: Int, val port_desc: String)

/**
 * 交换机Id
 * 交换机端口名称
 */
data class SwitchPort(val switch_id: Int, val switch_port: String)

/**
 * 光配端口Id
 * 所属屏柜Id
 * 光配编号
 * 光配层数
 * 光配端口名称
 * 光配端口类型（0:电口交换机类型 10:光口SC  11:光口FC  12:光口ST  13:光口LC）
 */
data class ODF(val odf_id: Int, val panel_id: Int, val odf_code: String,
               val odf_layer: String, val odf_port: String, val odf_port_type: Int)

/**
 * 光缆端口Id
 *  光缆编号
 *  纤芯编号
 *  纤芯颜色
 *  内连装置类型（1000:交换机   1001:装置）
 *  内连装置ID
 *  内连端口名称
 *  内连端口收发类型
 *  内连跳纤编号
 *  对侧光配ID
 */
data class ODFConnection(val odf_id: Int, val optical_cable_number: String, val optical_fiber_number: Int,
                         val optical_fiber_color: Int, val internal_device_type: Int,
                         val internal_device_id: Int, val internal_device_port: String,
                         val internal_rt_type: Int, val internal_optical_fiber_number: String,
                         val external_odf_id: Int)

/**
 *  尾纤Id
 *  尾缆编号
 *  尾缆纤芯编号
 *  尾缆纤芯颜色
 *   0:无色,1:蓝色,2:橙色
 *   3:绿色,4:棕色,5:灰色
 *   6:白色,7:红色,8:黑色
 *   9:黄色,10:紫色,11:青绿色,12:浅红色
 *  尾缆纤芯作用描述
 */
data class TailFiber(val tail_fiber_id: Int, val tail_cable_number: String, val tail_fiber_number: Int,
                     val tail_fiber_color: Int, val tail_fiber_desc: String)

/**
 * 装置Id
 * 本侧设备端口
 * 对侧设备ID
 * 对侧设备端口
 * 对侧设备类型（1000:交换机  1001:装置）
 * 接入装置发送口的纤芯ID
 * 接入装置接收口的纤芯ID
 */
data class DeviceConnection(val from_id: Int, val from_port: String, val to_id: Int,
                            val to_port: Int, val to_dev_type: String,
                            val tail_fiber_tx_id: Int, val tail_fiber_rx_id: Int)

/**
 * 交换机ID
 * 本侧设备端口
 * 对侧设备ID
 * 对侧设备端口
 * 对侧设备类型（1000:交换机  1001:装置）
 * 接入交换机发送口的纤芯ID
 * 接入交换机接收口的纤芯ID
 */
data class SwitchConnection(val from_id: Int, val from_port: String, val to_id: Int,
                            val to_port: String, val to_dev_type: String,
                            val tail_fiber_tx_id: Int, val tail_fiber_rx_id: Int)

/**
 * 模型数据id
 * 模型id
 * 输入输出类型
 * 数据索引地址
 * 数据描述
 */
data class GSEModel(val id: Int, val model_id: Int, val io_type: Int,
                    val gse_addr: String, val gse_desc: String)

/**
 * Excel模型数据id
 * Excel模型id
 * 输入输出类型
 * 数据索引地址
 * 数据描述
 */
data class ExcelGSEModel(val excel_id: Int, val excel_model_id: Int, val excel_io_type: Int,
                         val excel_gse_addr: String, val excel_gse_desc: String)

/**
 * 模型数据id
 * 模型id
 * 数据名称
 * 数据描述
 * 数据类型（0:SV  1:GOOSE）
 * 连接方式(0:网连  1:直连)
 * 接收数据索引地址
 * 接收数据描述
 * 发送数据索引地址
 * 发送数据描述
 * 虚端子描述
 * 接收端口
 */
data class Inputs(val model_id_from: Int, val from_index: Int, val model_id_to: Int,
                  val to_index: Int, val is_goose: Int, val p2p: Int, val in_addr: String,
                  val desc_to: String, val out_addr: String, val desc_from: String,
                  val input_desc: String, val port_to: String)

























