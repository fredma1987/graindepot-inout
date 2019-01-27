package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.biz.InoutBiz;
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
@RequestMapping("register")
public class RegisterController extends BaseController{
    @Autowired
    private InoutBiz inoutBiz;
    @ModelAttribute("ctx")
    public void getAccount(Model model,HttpServletRequest request) {
        String ctx = request.getContextPath();
        if(ctx.endsWith("/"))
            ctx.substring(0,ctx.length()-1);
        model.addAttribute("ctx",ctx);
    }
    @RequestMapping(value="/toRegister",method = RequestMethod.GET)
    public String toRegister(Model model, HttpServletRequest request, HttpServletResponse response){
        return "register";
    }
    @PostMapping("/edit")
    @ResponseBody
    public JsonResult edit(Inout inout){
        if(inout.getBillid()==null){
            inoutBiz.insert(inout);
            return new JsonResult("添加成功", true);
        }else{
            inoutBiz.update(inout);
            return new JsonResult("修改成功", true);
        }
    }
}
