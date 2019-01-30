package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.SelectorInoutBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("selectorInout")
public class SelectorInoutController extends BaseController{
    @Autowired
    private SelectorInoutBiz selectorInoutBiz;

    @GetMapping("planfileInplan/byGrainid")
    public List<PlanfileInplan> planfileInplan_byGrainid(Integer grainid){
        UserAddress ua=getUserAddress();
        Map map=new HashMap();
        map.put("grainid",grainid);
        map.put("graindepotid",ua.getGraindepotid());
        List<PlanfileInplan> list = selectorInoutBiz.getPlanfileInplanListByGrainid(map);
        return list;

    }

    @GetMapping("storageByMap")
    public List<Storage> storageByMap(){
        UserAddress ua=getUserAddress();
        Map param=new HashMap();
        param.put("graindepotid",ua.getGraindepotid());
        List<Storage> storages = selectorInoutBiz.storageByMap(param);
        return storages;
    }
    @GetMapping("contractByMap")
    public List<Contract> contractByMap(Integer grainid){
        UserAddress ua=getUserAddress();
        Map param=new HashMap();
        param.put("graindepotid",ua.getGraindepotid());
        param.put("grainid",grainid);
        List<Contract> list = selectorInoutBiz.contractByMap(param);
        return list;
    }
}
