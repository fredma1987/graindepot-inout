package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.InoutBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("settlement")
public class SettlementController extends BaseController{
    @Autowired
    private InoutBiz inoutBiz;
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
    public PagerModel settmentPageList(int start, int length,@RequestParam Map param) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e=new PagerModel();
        for(Object key:param.keySet()){
            if(!key.toString().contains("[")){
                String value = param.get(key).toString();//
               e.putWhere(key.toString(), value);
           }
        }
        e.putWhere("billtype", 1);
        e.putWhere("billstages", "1,2,3,4,5,6");
        e.addOrder("billcode desc");
        e.setStart(start);
        e.setLength(length);
        if(currentUser.getGraindepotid()!=0) {
            e.putWhere("graindepotid", currentUser.getGraindepotid());
        }
        PagerModel<Inout> result=inoutBiz.selectListByPage(e);
        return result;
    }
}
