package com.shenrui.label.biaoqian.mvp.model.bean

import com.luckongo.tthd.mvp.model.bean.Panel

/**
 * 区域bean
 * Created by chengguo on 18-3-13.
 */
data class RegionBean(val region_id: Int, val region_name: String,
                      val region_code: String, val panel: List<Panel>)