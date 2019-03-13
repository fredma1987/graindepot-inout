package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.bean.Video;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.rpc.IVideoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//单据异常处理类，终止单据和退卡操作
@Controller
@RequestMapping("quickinout")
public class QuickinoutController extends BaseController {
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private IVideoService videoService;

    @GetMapping("/in")
    public String quickin(Model model) {
        String title = "快速入库";
        model.addAttribute("title", title);
        String path = "quickinout/in/index";
        return path;
    }
    //入库登记
    @RequestMapping(value = "in/toInRegister", method = RequestMethod.GET)
    public String toInRegister(Model model, HttpServletRequest request
            , HttpServletResponse response,String billcode) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Video video = videoService.selectRegiterVideo(user.getGraindepotid());
        model.addAttribute("video", video);
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("billcode", billcode);
        model.addAttribute("title", "入库登记");
        return "quickinout/in/register";
    }
    //扦样检验
    @RequestMapping(value = "in/toInInspect", method = RequestMethod.GET)
    public String toInInspect(Model model, HttpServletRequest request
            , HttpServletResponse response,String billcode) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("billcode", billcode);
        return "quickinout/in/inspect";
    }

    //入库检斤
    @RequestMapping(value = "in/toInWeight", method = RequestMethod.GET)
    public String toInWeight(Model model, HttpServletRequest request
            , HttpServletResponse response,String billcode) {
        BaseUser user = getCurrentUser();
        List<Video> videolist = videoService.selectWeightVideoList(user.getGraindepotid());
        model.addAttribute("videolist", videolist);
        model.addAttribute("billcode", billcode);
        model.addAttribute("title", "入库检斤");
        return "quickinout/in/weight";
    }


    @GetMapping("/out")
    public String quickout(Model model) {
        String title = "快速出库";
        model.addAttribute("title", title);
        String path = "quickinout/out/index";
        return path;
    }

    //出库登记
    @RequestMapping(value = "out/toOutRegister", method = RequestMethod.GET)
    public String toOutRegister(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Video video = videoService.selectRegiterVideo(user.getGraindepotid());
        model.addAttribute("video", video);
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "出库登记");
        return "quickinout/out/register";
    }

    //出库检斤
    @RequestMapping(value = "out/toOutWeight", method = RequestMethod.GET)
    public String toOutWeight(Model model, HttpServletRequest request
            , HttpServletResponse response,String billcode) {
        BaseUser user = getCurrentUser();
        List<Video> videolist = videoService.selectWeightVideoList(user.getGraindepotid());
        model.addAttribute("videolist", videolist);
        model.addAttribute("billcode", billcode);
        model.addAttribute("title", "出库检斤");
        return "quickinout/out/weight";
    }

}
