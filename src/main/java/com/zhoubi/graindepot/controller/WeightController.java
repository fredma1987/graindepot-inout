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
import java.math.BigDecimal;
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
     * @param inout
     * @param type  1:称毛重 2：称皮重
     * @return
     */
    @PostMapping("editWeightInout")
    @ResponseBody
    public JsonResult editWeightInout(HttpServletRequest request, Inout inout, Integer type) {
        //判断的当前登记流水号状态能否进行称毛或者称皮重
        Inout item = inoutBiz.selectById(inout.getBillid());
        if (type == 1 && (item.getInspectstate() == null || item.getInspectstate() == 0)) {
            return new JsonResult("请检验完成后进行称毛重", false);
        }
        if (type == 2 && (item.getValuebinstate() == null || item.getValuebinstate() == 0)) {
            return new JsonResult("请值仓后进行称皮重", false);
        }

        //根据登记流水号对应的billid存在于l_inout_insp表中
        Double waterdeduweight = null, impudeduweight = null, bulkdensitydeduweight = null, yrkdeduweight = null, ukdeduweight = null, otmsdeduweight = null, zjmldeduweight = null, cmldeduweight = null, gwcmdeduweight = null, hhldeduweight = null, wdeduweightx = null, ideduweightx = null, odeduweightx = null;
        Double netweight = inout.getNetweight();
        if (type == 2) {
            //计算各种增扣量
            InoutInsp inoutInsp = inspectBiz.selectById(inout.getBillid());
            waterdeduweight = multiply(netweight, inoutInsp.getWaterdedurate());
            impudeduweight = multiply(netweight, inoutInsp.getImpudedurate());
            bulkdensitydeduweight = multiply(netweight, inoutInsp.getBulkdensitydedurate());
            yrkdeduweight = multiply(netweight, inoutInsp.getYrkdedurate());
            ukdeduweight = multiply(netweight, inoutInsp.getUkdedurate());
            otmsdeduweight = multiply(netweight, inoutInsp.getOtmsdedurate());
            zjmldeduweight = multiply(netweight, inoutInsp.getZjmldedurate());
            cmldeduweight = multiply(netweight, inoutInsp.getCmldedurate());
            gwcmdeduweight = multiply(netweight, inoutInsp.getGwcmdedurate());
            hhldeduweight = multiply(netweight, inoutInsp.getHhldedurate());
            wdeduweightx = multiply(netweight, inoutInsp.getWdeduratex());
            ideduweightx = multiply(netweight, inoutInsp.getIdeduratex());
            odeduweightx = multiply(netweight, inoutInsp.getOdeduratex());

        }
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
        param.put("netweight", netweight);
        if (type == 1) {
            param.put("gwopid", user.getUserid());
            param.put("gwtime", new Date());
            param.put("gwstate", 1);
            param.put("billstage", 4);
        }
        if (type == 2) {
            param.put("tareoptid", user.getUserid());
            param.put("taretime", new Date());
            param.put("tarestate", 1);
            param.put("billstage", 6);
            param.put("totalincdedu", plus(waterdeduweight, impudeduweight, bulkdensitydeduweight, yrkdeduweight
                    , ukdeduweight, otmsdeduweight, zjmldeduweight, cmldeduweight, gwcmdeduweight
                    , hhldeduweight));
            param.put("extradedu", plus(wdeduweightx, ideduweightx, odeduweightx
                    , inout.getAdddeduweightx()));
        }
        inoutBiz.updateMap(param);
        //更新b_inout_insp数据
        param.clear();
        param.put("Where_billid", inout.getBillid());
        param.put("wdeduratex", inout.getWdeduratex());
        param.put("ideduratex", inout.getIdeduratex());
        param.put("adddeduweightx", inout.getAdddeduweightx());
        param.put("waterdeduweight", waterdeduweight);
        param.put("impudeduweight", impudeduweight);
        param.put("bulkdensitydeduweight", bulkdensitydeduweight);
        param.put("yrkdeduweight", yrkdeduweight);
        param.put("ukdeduweight", ukdeduweight);
        param.put("otmsdeduweight", otmsdeduweight);
        param.put("zjmldeduweight", zjmldeduweight);
        param.put("cmldeduweight", cmldeduweight);
        param.put("gwcmdeduweight", gwcmdeduweight);
        param.put("hhldeduweight", hhldeduweight);
        param.put("wdeduweightx", wdeduweightx);
        param.put("ideduweightx", ideduweightx);
        param.put("odeduweightx", odeduweightx);
        inspectBiz.updateMap(param);
        return new JsonResult("保存成功", true);
    }

    // 1.1*200 =>2.200  null*200=>null 保留三位小数
    private static Double multiply(Double a, Double b) {
        if (a == null || b == null) {
            return null;
        } else {
            BigDecimal c = new BigDecimal(a * (b / 100));
            BigDecimal r = c.setScale(10, BigDecimal.ROUND_HALF_UP);
            double o = r.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return o;
        }
    }

    private static Double plus(Double... as) {
        double r = 0.0;
        for (Double a : as) {
            if (a != null) {
                r += a;
            }
        }
        return r;
    }

    public static void main(String[] args) {
        Double a = 2.5;
        Double b = 2.63;
        System.out.println(multiply(a, b));
        System.out.println("===");
    }

}
