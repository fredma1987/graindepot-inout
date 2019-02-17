package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.GrainInspectItemBiz;
import com.zhoubi.graindepot.biz.GrainRankBiz;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.InspectBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("inspect")
public class InspectController extends BaseController {
    @Autowired
    InspectBiz inspectBiz;
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private GrainInspectItemBiz grainInspectItemBiz;
    @Autowired
    private GrainRankBiz grainRankBiz;


    @ModelAttribute("ctx")
    public void getAccount(Model model, HttpServletRequest request) {
        String ctx = request.getContextPath();
        if (ctx.endsWith("/"))
            ctx.substring(0, ctx.length() - 1);

        model.addAttribute("ctx", ctx);
    }

    @RequestMapping(value = "/toInInspect", method = RequestMethod.GET)
    public String toRegister(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "in/inspect";
    }

    @PostMapping("selectById")
    @ResponseBody
    public InoutInsp selectById(Integer billid) {
        return inspectBiz.selectById(billid);
    }


    //根据登记流水号来查询姓名等信息
    @PostMapping("/inout/byBillcode")
    @ResponseBody
    public List<Inout> inout_byBillcode(String billcode, String billdate) {
        if (StringUtils.isEmpty(billcode)) {
            return new ArrayList<Inout>();
        }
        UserAddress ua = getUserAddress();
        //判断是否已存在，通过身份证号和库点id
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        String likeBillcode;
        if (StringUtils.isEmpty(billdate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowStr = sdf.format(new Date());
            likeBillcode = nowStr + "-%" + billcode + "%";
        } else {
            likeBillcode = billdate + "-%" + billcode + "%";
        }
        param.put("billcode", likeBillcode);
        List<Inout> result = inoutBiz.listByBillcodeAndGraindepotid(param);
        return result;
    }

    //根据粮食品种id查询出质检标准
    @GetMapping("grainInspectItem/byGrainid")
    @ResponseBody
    public JsonResult grainInspectItemByGrainid(Integer grainid) {
        // UserAddress ua=getUserAddress();
        Map param = new HashMap();
        param.put("grainid", grainid);
        List<GrainInspectItem> result = grainInspectItemBiz.listByGrainid(param);
        return new JsonResult(result, "获取成功", true);
    }

    //获取该品种的等级划分标准
    @GetMapping("grainRank/byGrainid")
    @ResponseBody
    public JsonResult grainRankByGrainid(Integer grainid) {
        // UserAddress ua=getUserAddress();
        Map param = new HashMap();
        param.put("grainid", grainid);
        GrainRank result = grainRankBiz.oneByGrainid(param);
        return new JsonResult(result, "获取成功", true);
    }

    @PostMapping("editInoutInsp")
    @ResponseBody
    public JsonResult editInoutInsp(InoutInsp inoutInsp) {
        //根据登记流水号对应的billid存在于l_inout_insp表中
        UserAddress ua = getUserAddress();
        BaseUser user = getCurrentUser();
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        param.put("billcode", inoutInsp.getBillcode());
        InoutInsp item = inspectBiz.oneByBillcode(param);
        param.put("billtype", 1);
        Inout inout = inoutBiz.selectOne(param);
        if (inout == null) {
            return new JsonResult("当前登记流水号不存在", false);
        }
        if (item != null) {
            //更新
            inoutInsp.setBillid(item.getBillid());
            inspectBiz.updateInspect(inoutInsp);
        } else {
            //插入
            inoutInsp.setBillid(inout.getBillid());
            inspectBiz.insert(inoutInsp);
        }
        //更新b_inout数据
        param.clear();
        param.put("Where_billid", inout.getBillid());
        param.put("storageid", inoutInsp.getStorageid());
        param.put("grainid", inoutInsp.getGrainid());
        param.put("grainattrid", inoutInsp.getGrainattrid());
        param.put("producingyear", inoutInsp.getProducingyear());
        param.put("grade", inoutInsp.getGrade());
        param.put("inplanid", inoutInsp.getInplanid());
        param.put("contractid", inoutInsp.getContractid());
        param.put("billstage", 3);
        param.put("samplingoptid", user.getUserid());
        param.put("samplingtime", new Date());
        param.put("samplingstate", 1);
        param.put("inspectoptid", user.getUserid());
        param.put("inspecttime", new Date());
        param.put("inspectstate", 1);
        inoutBiz.updateMap(param);
        return new JsonResult("保存成功", true);
    }

    //根据登记流水号来查询入库登记信息
    @GetMapping("/inout/oneByBillcode")
    @ResponseBody
    public JsonResult oneByBillcode(String billcode) {
        if (StringUtils.isEmpty(billcode)) {
            Inout item = new Inout();
            return new JsonResult(item, true);
        }
        UserAddress ua = getUserAddress();
        //判断是否已存在，通过身份证号和库点id
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        param.put("billcode", billcode.trim());
        //param.put("graindepotid", 1);
        // param.put("billcode", "2019-02-17-0001");
        Inout result = inoutBiz.oneByBillcodeAndGraindepotid(param);
        return new JsonResult(result, true);
    }

}
