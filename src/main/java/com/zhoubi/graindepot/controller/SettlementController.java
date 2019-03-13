package com.zhoubi.graindepot.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.OutpayBiz;
import com.zhoubi.graindepot.biz.OutpayDetailBiz;
import com.zhoubi.graindepot.biz.PrereceBiz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("settlement")
public class SettlementController extends BaseController{
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private PrereceBiz prereceBiz;
    @Autowired
    private OutpayBiz outpayBiz;
    @Autowired
    private OutpayDetailBiz outpayDetailBiz;

    @ModelAttribute("ctx")
    public void getAccount(Model model,HttpServletRequest request) {
        String ctx = request.getContextPath();
        if(ctx.endsWith("/"))
            ctx.substring(0,ctx.length()-1);
        model.addAttribute("ctx",ctx);
    }
    @RequestMapping(value="/toInSettlement",method = RequestMethod.GET)
    public String toInSettlement(Model model, HttpServletRequest request, HttpServletResponse response){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "收购结算");
        return "in/settlement";
    }
    @RequestMapping(value="/toOutSettlement",method = RequestMethod.GET)
    public String toOutSettlement(Model model, HttpServletRequest request, HttpServletResponse response){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "销售结算");
        return "out/settlement";
    }
    @PostMapping("settmentPageList")
    @ResponseBody
    public PagerModel settmentPageList(int start, int length
            ,Integer traderid,Integer storageid,Integer contractid) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e=new PagerModel();
        if (traderid==null||storageid==null||contractid==null) {
            return e;
        }

        e.putWhere("notbilltype", 0);
        e.putWhere("notpaidstate", 1);
        e.putWhere("traderid", traderid);
        e.putWhere("storageid", storageid);
        e.putWhere("contractid", contractid);
        //称皮称毛已完成
        e.putWhere("gwstate",1);
        e.putWhere("tarestate",1);
        e.addOrder("billcode desc");
        e.setStart(start);
        e.setLength(length);
        if(currentUser.getGraindepotid()!=0) {
            e.putWhere("graindepotid", currentUser.getGraindepotid());
        }
        PagerModel<Inout> result=inoutBiz.selectListByPage(e);
        return result;
    }


    @PostMapping("editSettlement")
    @ResponseBody
    public JsonResult editSettlement(Inout inout) {
        //判断当前单据有无质检和称重完毕
        Integer billid=inout.getBillid();
        Inout item = inoutBiz.selectById(billid);
        Integer inspectstate=item.getInspectstate();//有无检验
        Integer gwstate=item.getGwstate();//有无称毛重
        Integer tarestate=item.getTarestate();//有无称毛重
        if (inspectstate==null||inspectstate==0) {
          return   new JsonResult("单据未完成检验无法结算", false);
        }
        if (gwstate==null||gwstate==0) {
            return  new JsonResult("单据未完成称毛重无法结算", false);
        }
        if (tarestate==null||tarestate==0) {
            return  new JsonResult("单据未完成称皮重无法结算", false);
        }


        BaseUser user=getCurrentUser();
        Map param=new HashMap();
        param.put("Where_billid",inout.getBillid());
        param.put("extradedu",inout.getExtradedu());
        param.put("paidweight",inout.getPaidweight());
        param.put("price3",inout.getPrice());
        param.put("amount",inout.getAmount());
        param.put("billstage",7);
        param.put("paidoptid",user.getUserid());
        param.put("paidtime",new Date());
        param.put("paiddate",new Date());
        param.put("settlemothod",inout.getSettlemothod());
        param.put("paidstate",1);
        param.put("updatetime",new Date());
        inoutBiz.updateMap(param);
        return new JsonResult("单据保存成功", true);

    }

    @GetMapping("prerece/one/byMap")
    @ResponseBody
    public JsonResult prerece_byMap(Integer traderid,Integer storageid) {
        Map param=new HashMap();
        param.put("traderid",traderid);
        param.put("storageid",storageid);
        List<Prerece> list = prereceBiz.selectList(param);
        if (list!=null&&list.size()>0) {
            return new JsonResult(list.get(0),true);
        }else {
            return new JsonResult(new Prerece(),true);
        }

    }
    @GetMapping("prerece/leftAmount")
    @ResponseBody
    public JsonResult leftAmount(Integer traderid) {
        Map param=new HashMap();
        param.put("traderid",traderid);
        Double leftAmount = prereceBiz.getLeftAmount(param);
        return new JsonResult(leftAmount,true);
    }

    @PostMapping("editOutSettlement")
    @ResponseBody
    public JsonResult editOutSettlement(Outpay outpay) {
        BaseUser user=getCurrentUser();
        synchronized (user.getGraindepotid()+""){
            outpay.setBilldate(new Date());
            String maxBillcode = prereceBiz.getMaxBillcode(user.getGraindepotid());
            if (StringUtils.isNotEmpty(maxBillcode)) {
                //能找到当天最大的单据号
                String[] maxBillcodes = maxBillcode.split("-");
                outpay.setBillcode(maxBillcodes[0] + "-" + maxBillcodes[1]
                        + "-" + maxBillcodes[2] + "-" + String.format("%04d", Integer.parseInt(maxBillcodes[3]) + 1));
            } else {
                //不能能找到当天最大的单据号
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String format = sdf.format(new Date());
                outpay.setBillcode(format + "-0001");
            }
            outpay.setBillstate(1);
            outpay.setGroupid(user.getGroupid());
            outpay.setCompanyid(user.getCompanyid());
            outpay.setGraindepotid(user.getGraindepotid());
            outpay.setCreateuserid(user.getUserid());
            outpay.setCreatetime(new Date());
            outpayBiz.insert(outpay);
        }
        return new JsonResult(outpay,"保存成功", true);

    }

    @PostMapping("editOutpayDetail")
    @ResponseBody
    public JsonResult editOutpayDetail(String json) {
        BaseUser user=getCurrentUser();
        List<OutpayDetail> outpayDetails = JSONArray.parseArray(json, OutpayDetail.class);
        outpayDetailBiz.insertList(outpayDetails);
        //修改b_inout的值
        for (OutpayDetail outpayDetail : outpayDetails) {
            Map param=new HashMap();
            param.put("Where_billid",outpayDetail.getBillid());
            param.put("price",outpayDetail.getPrice());
            param.put("amount",outpayDetail.getAmount());
            param.put("paidstate",1);
            param.put("paiddate",new Date());
            param.put("paidtime",new Date());
            param.put("paidoptid",user.getUserid());
            inoutBiz.updateMap(param);
        }
        return new JsonResult("结算成功",true);

    }

    @PostMapping("/setBackicflag")
    @ResponseBody
    public JsonResult setBackicflag(Integer billid,Integer backicflag,String backreason) {
        Map param = new HashMap();
        if (billid==null||backicflag==null) {
            return new JsonResult("更新退卡字段失败",false);
        }else {
            param.put("Where_billid",billid);
            param.put("backicflag",backicflag);
            param.put("backreason",backreason);
            param.put("backictime",new Date());
            inoutBiz.updateMap(param);
            return new JsonResult("更新退卡字段成功", true);
        }

    }

}
