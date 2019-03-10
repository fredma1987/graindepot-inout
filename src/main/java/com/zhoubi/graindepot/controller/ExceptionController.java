package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.InoutBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//单据异常处理类，终止单据和退卡操作
@Controller
@RequestMapping("exception")
public class ExceptionController extends BaseController {
    @Autowired
    private InoutBiz inoutBiz;


    //查询出所有的出入库单据(显示正常单据 和 补单)
    @GetMapping("inout/list/page")
    @ResponseBody
    public PagerModel<Inout> inout_list(int start, int length, Integer inoutflag
            , String billdate, String billcode,Integer backicflag,Integer regstate
            , String sellmanname,Integer stopflag) {
        UserAddress ua = getUserAddress();
        PagerModel<Inout> e = new PagerModel();
        e.addOrder("billdate desc,billcode desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("graindepotid", ua.getGraindepotid());
        if (inoutflag!=null) {
            e.putWhere("inoutflag", inoutflag);
        }
        if (StringUtils.isNotEmpty(billdate)){
            e.putWhere("billdate", billdate);
        }
        if (backicflag!=null) {
            e.putWhere("backicflag",backicflag);
        }
        if (stopflag!=null) {
            e.putWhere("stopflag",stopflag);
        }
        if (regstate!=null) {
            e.putWhere("regstate",regstate);
        }
        if (StringUtils.isNotEmpty(sellmanname)) {
            e.putWhere("sellmanname", "%" + sellmanname + "%");
        }
        if (StringUtils.isNotEmpty(billcode)) {
            e.putWhere("billcode", "%" + billcode + "%");
        }
        PagerModel<Inout> result = inoutBiz.selectListByPage(e, "selectExceptionInoutPageList", "selectExceptionInoutPageCount");
        return result;
    }

    @PostMapping("setInoutStop")
    @ResponseBody
    public JsonResult setInoutStop(Integer billid,String stopreason){
        Map param=new HashMap();
        param.put("Where_billid",billid);
        param.put("stopflag",1);
        param.put("stopreason",stopreason);
        inoutBiz.updateMap(param);
        return new JsonResult("终止成功",true);
    }

    @PostMapping("setInoutBackIC")
    @ResponseBody
    public JsonResult setInoutBackIC(String billcode,String backreason){
        UserAddress ua=getUserAddress();
        Map param=new HashMap();
        param.put("Where_graindepotid",ua.getGraindepotid());
        param.put("Where_billcode",billcode);
        param.put("backicflag",1);
        param.put("backreason",backreason);
        inoutBiz.updateMap(param);
        return new JsonResult("终止成功",true);
    }


}
