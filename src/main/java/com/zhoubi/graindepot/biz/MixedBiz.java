package com.zhoubi.graindepot.biz;

import com.zhoubi.graindepot.bean.Blanklist;
import com.zhoubi.graindepot.bean.Trader;
import com.zhoubi.graindepot.mapper.MixedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MixedBiz {
    @Autowired
    private MixedMapper mixedMapper;

    public List<Blanklist> blanklistIsIdCardExist(Map map){
        return mixedMapper.blanklistIsIdCardExist(map);
    }
    public  List<Blanklist> blanklistIsTraderExist(Map map){
        return mixedMapper.blanklistIsTraderExist(map);
    }
}
