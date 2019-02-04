package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.bean.InoutInsp;
import com.zhoubi.graindepot.biz.InspectBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("inspect")
public class InspectController extends BaseController{
    @Autowired
    InspectBiz inspectBiz;
    @ModelAttribute("ctx")
    public void getAccount(Model model,HttpServletRequest request) {
        String ctx = request.getContextPath();
        if(ctx.endsWith("/"))
            ctx.substring(0,ctx.length()-1);

        model.addAttribute("ctx",ctx);
    }
    @RequestMapping(value="/toInInspect",method = RequestMethod.GET)
    public String toRegister(Model model, HttpServletRequest request, HttpServletResponse response){
        return "in/inspect";
    }
    @PostMapping("selectById")
    @ResponseBody
    public InoutInsp selectById(Integer billid){
        return inspectBiz.selectById(billid);
    }
}
