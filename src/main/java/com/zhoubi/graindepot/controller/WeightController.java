package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.InspectBiz;
import com.zhoubi.graindepot.rpc.IVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("weight")
public class WeightController extends BaseController {
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private IVideoService videoService;
    @Autowired
    private InspectBiz inspectBiz;

    @ModelAttribute("ctx")
    public void getAccount(Model model, HttpServletRequest request) {
        String ctx = request.getContextPath();
        if (ctx.endsWith("/"))
            ctx.substring(0, ctx.length() - 1);
        model.addAttribute("ctx", ctx);
    }

    @RequestMapping(value = "/toInWeight", method = RequestMethod.GET)
    public String toInWeight(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        List<Video> videolist = videoService.selectWeightVideoList(user.getGraindepotid());
        model.addAttribute("videolist", videolist);
        model.addAttribute("title", "入库检斤");
        return "in/weight";
    }

    @RequestMapping(value = "/toOutWeight", method = RequestMethod.GET)
    public String toOutWeight(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        List<Video> videolist = videoService.selectWeightVideoList(user.getGraindepotid());
        model.addAttribute("videolist", videolist);
        model.addAttribute("title", "出库检斤");
        return "out/weight";
    }

    @GetMapping("newInPageList")
    @ResponseBody
    public PagerModel newInPageList(int start, int length) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e = new PagerModel();
        e.putWhere("inoutflag", 1);
        e.putWhere("billtype", 1);
        e.putWhere("billstages", "1,2,3,4,5,6");
        e.addOrder("billcode desc");
        e.setStart(start);
        e.setLength(length);
        if (currentUser.getGraindepotid() != 0) {
            e.putWhere("graindepotid", currentUser.getGraindepotid());
        }
        PagerModel<Inout> result = inoutBiz.selectListByPage(e);
        return result;
    }

    @GetMapping("newOutPageList")
    @ResponseBody
    public PagerModel newOutPageList(int start, int length) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Inout> e = new PagerModel();
        e.putWhere("inoutflag", -1);
        e.putWhere("billtype", 1);
        e.putWhere("billstages", "1,2,3,4,5,6");
        e.addOrder("billcode desc");
        e.setStart(start);
        e.setLength(length);
        if (currentUser.getGraindepotid() != 0) {
            e.putWhere("graindepotid", currentUser.getGraindepotid());
        }
        PagerModel<Inout> result = inoutBiz.selectListByPage(e);
        return result;
    }

    /**
     * @param billid
     * @param flag   1:前一单 2：当前单  3:后一单
     * @return
     */
    @GetMapping("inout/findOneIn")
    @ResponseBody
    public JsonResult findOneIn(Integer billid, Integer flag) {
        Inout inout = null;
        if (flag == 2 && billid != null) {
            inout = inoutBiz.selectById(billid);
        } else {
            UserAddress ua = getUserAddress();
            Map param = new HashMap();
            param.put("inoutflag", 1);
            param.put("graindepotid", ua.getGraindepotid());
            param.put("billtype", 1);
            if (billid != null) {
                param.put("billid", billid);
                if (flag == 1) {
                    inout = inoutBiz.selectBeforeOne(param);
                } else if (flag == 3) {
                    inout = inoutBiz.selectAfterOne(param);
                }
            } else {
                inout = inoutBiz.findMaxBill(param);
            }
        }
        if (inout != null) {
            return new JsonResult(inout, "查询成功", true);
        } else {
            return new JsonResult(null, "未查询到相关记录", false);
        }

    }

    /**
     * @param inout
     * @param type  1:称毛重 2：称皮重
     * @return
     */
    @PostMapping("editWeightInout")
    @ResponseBody
    public JsonResult editWeightInout(HttpServletRequest request,Inout inout, Integer type) {
        //判断的当前登记流水号状态能否进行称毛或者称皮重
        Inout item = inoutBiz.selectById(inout.getBillid());
        if (type == 1 && item.getBillstage() < 3) {
            return new JsonResult("当前单据处在"
                    +Constant.Billstage.getText(item.getBillstage())+"阶段，请检验完成后进行称毛重",false);
        }
        if (type == 2 && item.getBillstage() < 5) {
            return new JsonResult("当前单据处在"
                    +Constant.Billstage.getText(item.getBillstage())+"阶段，请值仓完成后进行称皮重",false);
        }

        //根据登记流水号对应的billid存在于l_inout_insp表中
        BaseUser user = getCurrentUser();
        Map param = new HashMap();
        //更新b_inout数据
        param.clear();
        param.put("Where_billid", inout.getBillid());
        param.put("storageid", inout.getStorageid());
        param.put("grainid", inout.getGrainid());
        param.put("grainattrid", inout.getGrainattrid());
        param.put("grossweight", inout.getGrossweight());
        param.put("tareweight", inout.getTareweight());
        param.put("netweight", inout.getNetweight());
        if (type==1) {
            param.put("gwopid", user.getUserid());
            param.put("gwtime", new Date());
            param.put("gwstate", 1);
            param.put("billstage", 4);
        }
        if (type==2) {
            param.put("tareoptid", user.getUserid());
            param.put("taretime", new Date());
            param.put("tarestate", 1);
            param.put("billstage", 6);
        }
        inoutBiz.updateMap(param);
        //更新b_inout_insp数据
        param.clear();
        param.put("Where_billid", inout.getBillid());
        param.put("wdeduratex", inout.getWdeduratex());
        param.put("ideduratex", inout.getIdeduratex());
        param.put("adddeduweightx", inout.getAdddeduweightx());
        inspectBiz.updateMap(param);
        return new JsonResult("保存成功", true);
    }
}
