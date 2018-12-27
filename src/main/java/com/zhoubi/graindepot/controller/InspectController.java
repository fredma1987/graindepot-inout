package com.zhoubi.graindepot.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("inspect")
public class InspectController extends BaseController{
    @ModelAttribute("ctx")
    public void getAccount(Model model,HttpServletRequest request) {
        String ctx = request.getContextPath();
        if(ctx.endsWith("/"))
            ctx.substring(0,ctx.length()-1);
        model.addAttribute("ctx",ctx);
    }
    @RequestMapping(value="/toInspect",method = RequestMethod.GET)
    public String toRegister(Model model, HttpServletRequest request, HttpServletResponse response){
        return "inspect";
    }
}
