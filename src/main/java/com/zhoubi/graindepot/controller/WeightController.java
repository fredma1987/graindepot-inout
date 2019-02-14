package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.bean.InoutBean;
import com.zhoubi.graindepot.bean.Video;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.VideoBiz;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("weight")
public class WeightController extends BaseController{
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private VideoBiz videoBiz;
    @ModelAttribute("ctx")
    public void getAccount(Model model,HttpServletRequest request) {
        String ctx = request.getContextPath();
        if(ctx.endsWith("/"))
            ctx.substring(0,ctx.length()-1);
        model.addAttribute("ctx",ctx);
    }
    @RequestMapping(value="/toInWeight",method = RequestMethod.GET)
    public String toInWeight(Model model, HttpServletRequest request, HttpServletResponse response){
        BaseUser user = getCurrentUser();
        List<Video> videolist=videoBiz.selectWeightVideoList(user.getGraindepotid());
        model.addAttribute("videolist", videolist);
        return "in/weight";
    }
    @RequestMapping(value="/toOutWeight",method = RequestMethod.GET)
    public String toOutWeight(Model model, HttpServletRequest request, HttpServletResponse response){
        return "out/weight";
    }
    @GetMapping("newInPageList")
    @ResponseBody
    public PagerModel newInPageList(int start, int length) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e=new PagerModel();
        e.putWhere("inoutflag",1);
        e.putWhere("billtype",1);
        e.putWhere("billstages","1,2,3,4,5,6");
        e.addOrder("billcode desc");
        e.setStart(start);
        e.setLength(length);
        if(currentUser.getGraindepotid()!=0) {
            e.putWhere("graindepotid", currentUser.getGraindepotid());
        }
        PagerModel<Inout> result=inoutBiz.selectListByPage(e);
        return result;
    }
    @GetMapping("newOutPageList")
    @ResponseBody
    public PagerModel newOutPageList(int start, int length) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e=new PagerModel();
        e.putWhere("inoutflag", -1);
        e.putWhere("billtype", 1);
        e.putWhere("billstages","1,2,3,4,5,6");
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
