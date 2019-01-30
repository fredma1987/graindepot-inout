package com.zhoubi.graindepot.biz;

import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.mapper.SelectorInoutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhanghao on 2019/1/16.
 */
@Service
public class SelectorInoutBiz {
    @Autowired
    private SelectorInoutMapper mapper;

    public List<PlanfileInplan> getPlanfileInplanListByGrainid(Map map) {
        return mapper.getPlanfileInplanListByGrainid(map);
    }

    public List<Storage> storageByMap(Map map) {
        return mapper.storageByMap(map);
    }

    public List<Contract> contractByMap(Map map) {
        return mapper.contractByMap(map);
    }
}
