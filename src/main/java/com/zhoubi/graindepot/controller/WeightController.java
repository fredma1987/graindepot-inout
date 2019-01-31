package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.bean.InoutBean;
import com.zhoubi.graindepot.biz.InoutBiz;
import org.springframework.beans.BeanUtils;
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
@RequestMapping("weight")
public class WeightController extends BaseController{
    @Autowired
    private InoutBiz inoutBiz;
    @ModelAttribute("ctx")
    public void getAccount(Model model,HttpServletRequest request) {
        String ctx = request.getContextPath();
        if(ctx.endsWith("/"))
            ctx.substring(0,ctx.length()-1);
        model.addAttribute("ctx",ctx);
    }
    @RequestMapping(value="/toWeight",method = RequestMethod.GET)
    public String toRegister(Model model, HttpServletRequest request, HttpServletResponse response){
        return "weight";
    }
    @GetMapping("newPageList")
    @ResponseBody
    public PagerModel newPageList(int start, int length) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e=new PagerModel();
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
