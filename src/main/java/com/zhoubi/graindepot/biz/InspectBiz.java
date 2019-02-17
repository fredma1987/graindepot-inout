package com.zhoubi.graindepot.biz;

import com.zhoubi.graindepot.base.BaseMapper;
import com.zhoubi.graindepot.base.BaseService;
import com.zhoubi.graindepot.bean.InoutInsp;
import com.zhoubi.graindepot.mapper.InoutInspMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Administrator on 2019/1/31/031.
 */
@Service
public class InspectBiz extends BaseService<InoutInsp> {
    @Autowired
    InoutInspMapper InoutInspMapper;
    @Override
    protected BaseMapper<InoutInsp> getMapper() {
        return InoutInspMapper;
    }


    public InoutInsp oneByBillcode(Map param) {
        return InoutInspMapper.oneByBillcode(param);
    }

    public int updateInspect(InoutInsp inoutInsp) {
        return InoutInspMapper.updateInspect(inoutInsp);
    }
}
