package com.zhoubi.graindepot.mapper;

import com.zhoubi.graindepot.bean.*;

import java.util.List;
import java.util.Map;

/**
 * Created by zhanghao on 2019/1/16.
 */
public interface SelectorInoutMapper {
    List<PlanfileInplan> getPlanfileInplanListByGrainid(Map map);

    List<Storage> storageByMap(Map map);

    List<Contract> contractByMap(Map map);
}
