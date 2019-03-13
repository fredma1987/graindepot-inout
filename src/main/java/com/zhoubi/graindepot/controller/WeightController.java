package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.GqinspectBiz;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.InspectBiz;
import com.zhoubi.graindepot.rpc.IAuthorityService;
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
    @Autowired
    private GqinspectBiz gqinspectBiz;
    @Autowired
    private IAuthorityService iAuthorityService;


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
        if (item.getInspectstate() == null || item.getInspectstate() == 0) {
            return new JsonResult("请检验完成后进行称毛重", false);
        }
        //根据登记流水号对应的billid存在于l_inout_insp表中
        Double waterdeduweight = null, impudeduweight = null, bulkdensitydeduweight = null, yrkdeduweight = null, ukdeduweight = null, otmsdeduweight = null, zjmldeduweight = null, cmldeduweight = null, gwcmdeduweight = null, hhldeduweight = null, wdeduweightx = null, ideduweightx = null, odeduweightx = null;
        Double netweight = inout.getNetweight();
        if (netweight!=null) {
            //计算各种增扣量
            InoutInsp inoutInsp = inspectBiz.selectById(inout.getBillid());
            waterdeduweight = multiply(netweight, inoutInsp.getWaterdedurate(),3);
            impudeduweight = multiply(netweight, inoutInsp.getImpudedurate(),3);
            bulkdensitydeduweight = multiply(netweight, inoutInsp.getBulkdensitydedurate(),3);
            yrkdeduweight = multiply(netweight, inoutInsp.getYrkdedurate(),3);
            ukdeduweight = multiply(netweight, inoutInsp.getUkdedurate(),3);
            otmsdeduweight = multiply(netweight, inoutInsp.getOtmsdedurate(),3);
            zjmldeduweight = multiply(netweight, inoutInsp.getZjmldedurate(),3);
            cmldeduweight = multiply(netweight, inoutInsp.getCmldedurate(),3);
            gwcmdeduweight = multiply(netweight, inoutInsp.getGwcmdedurate(),3);
            hhldeduweight = multiply(netweight, inoutInsp.getHhldedurate(),3);
            wdeduweightx = multiply(netweight, inoutInsp.getWdeduratex(),3);
            ideduweightx = multiply(netweight, inoutInsp.getIdeduratex(),3);
            odeduweightx = multiply(netweight, inoutInsp.getOdeduratex(),3);

        }
        BaseUser user = getCurrentUser();
        Map param = new HashMap();
        //更新b_inout数据
        param.clear();
        param.put("Where_billid", inout.getBillid());
        param.put("traderid", inout.getTraderid());
        param.put("storageid", inout.getStorageid());
        param.put("grainid", inout.getGrainid());
        param.put("grainattrid", inout.getGrainattrid());
        param.put("grossweight", inout.getGrossweight());
        param.put("tareweight", inout.getTareweight());
        param.put("netweight", netweight);
        if (type!=null&&type == 1) {
            param.put("gwopid", user.getUserid());
            param.put("gwtime", new Date());
            param.put("gwstate", 1);
            param.put("billstage", 4);
        }
        if (type!=null&&type == 2) {
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

    //获取出库检验的值
    @GetMapping("gqinspect/one")
    @ResponseBody
    public JsonResult gqinspect_one(Integer storageid, Integer grainid) {
        Map param = new HashMap();
        param.put("storageid", storageid);
        param.put("grainid", grainid);
        param.put("insptype", 2);
        List<Gqinspect> list = gqinspectBiz.selectList(param);
        if (list != null && list.size() > 0) {
            return new JsonResult(list.get(0), true);
        } else {
            return new JsonResult(null, "无数据", false);
        }

    }


    /**
     * 出库称重
     *
     * @param inout
     * @param type  1:未全部称重完成 2：全部称重完成
     * @return
     */
    @PostMapping("editWeightOut")
    @ResponseBody
    public JsonResult editWeightOut(HttpServletRequest request
            , Inout inout, InoutInsp inoutInsp, Integer type) {
        //根据登记流水号对应的billid存在于l_inout_insp表中
        Double waterdeduweight = null, impudeduweight = null,
                bulkdensitydeduweight = null, yrkdeduweight = null,
                ukdeduweight = null, otmsdeduweight = null,
                zjmldeduweight = null, cmldeduweight = null,
                gwcmdeduweight = null, hhldeduweight = null,
                wdeduweightx = null, ideduweightx = null,
                odeduweightx = null, paidweight = null;
        Double netweight = inout.getNetweight();
        if (netweight != null) {
            //计算各种增扣量
            waterdeduweight = multiply(netweight, inoutInsp.getWaterdedurate(),3);
            impudeduweight = multiply(netweight, inoutInsp.getImpudedurate(),3);
            bulkdensitydeduweight = multiply(netweight, inoutInsp.getBulkdensitydedurate(),3);
            yrkdeduweight = multiply(netweight, inoutInsp.getYrkdedurate(),3);
            ukdeduweight = multiply(netweight, inoutInsp.getUkdedurate(),3);
            otmsdeduweight = multiply(netweight, inoutInsp.getOtmsdedurate(),3);
            zjmldeduweight = multiply(netweight, inoutInsp.getZjmldedurate(),3);
            cmldeduweight = multiply(netweight, inoutInsp.getCmldedurate(),3);
            gwcmdeduweight = multiply(netweight, inoutInsp.getGwcmdedurate(),3);
            hhldeduweight = multiply(netweight, inoutInsp.getHhldedurate(),3);
            wdeduweightx = multiply(netweight, inoutInsp.getWdeduratex(),3);
            ideduweightx = multiply(netweight, inoutInsp.getIdeduratex(),3);
            odeduweightx = multiply(netweight, inoutInsp.getOdeduratex(),3);
        }
        BaseUser user = getCurrentUser();
        Map param = new HashMap();
        //更新b_inout数据
        param.clear();
        param.put("Where_billid", inout.getBillid());
        param.put("traderid", inout.getTraderid());
        param.put("storageid", inout.getStorageid());
        param.put("grainid", inout.getGrainid());
        param.put("grainattrid", inout.getGrainattrid());
        param.put("grossweight", inout.getGrossweight());
        param.put("tareweight", inout.getTareweight());
        param.put("netweight", netweight);
        param.put("price", inout.getPrice());
        param.put("grade", inout.getGrade());
        if (type != null && type == 1) {
            param.put("gwopid", user.getUserid());
            param.put("gwtime", new Date());
            param.put("gwstate", 1);
            param.put("billstage", 4);
        }
        if (type != null && type == 2) {
            param.put("tareoptid", user.getUserid());
            param.put("taretime", new Date());
            param.put("tarestate", 1);
            param.put("billstage", 6);
        }
        param.put("totalincdedu", plus(waterdeduweight, impudeduweight, bulkdensitydeduweight, yrkdeduweight
                , ukdeduweight, otmsdeduweight, zjmldeduweight, cmldeduweight, gwcmdeduweight
                , hhldeduweight));
        param.put("extradedu", plus(wdeduweightx, ideduweightx, odeduweightx
                , inout.getAdddeduweightx()));
        if (netweight != null) {
            paidweight = reduce(netweight, waterdeduweight, impudeduweight
                    , bulkdensitydeduweight, yrkdeduweight, ukdeduweight, otmsdeduweight, zjmldeduweight
                    , cmldeduweight, gwcmdeduweight, hhldeduweight, wdeduweightx, ideduweightx, odeduweightx
                    , inout.getAdddeduweightx());
            param.put("paidweight", paidweight);
            param.put("amount", multiply(paidweight,inout.getPrice()*100,2));

        }
        inoutBiz.updateMap(param);
        //插入b_inout_insp数据
        inoutInsp.setBillid(inout.getBillid());
        inoutInsp.setWaterdeduweight(waterdeduweight);
        inoutInsp.setImpudeduweight(impudeduweight);
        inoutInsp.setBulkdensitydeduweight(bulkdensitydeduweight);
        inoutInsp.setYrkdeduweight(yrkdeduweight);
        inoutInsp.setUkdeduweight(ukdeduweight);
        inoutInsp.setOtmsdeduweight(otmsdeduweight);
        inoutInsp.setZjmldeduweight(zjmldeduweight);
        inoutInsp.setCmldeduweight(cmldeduweight);
        inoutInsp.setGwcmdeduweight(gwcmdeduweight);
        inoutInsp.setHhldeduweight(hhldeduweight);
        inoutInsp.setWdeduweightx(wdeduweightx);
        inoutInsp.setIdeduweightx(ideduweightx);
        inoutInsp.setOdeduweightx(odeduweightx);
        InoutInsp insp = inspectBiz.selectById(inout.getBillid());
        if (insp == null) {
            inspectBiz.insert(inoutInsp);
        } else {
            inspectBiz.update(inoutInsp);
        }


        param.clear();

        inspectBiz.updateMap(param);
        return new JsonResult("保存成功", true);
    }

    @GetMapping("isAllow")
    @ResponseBody
    public  boolean isAllow(){
        boolean b = iAuthorityService.authority_isAllow("1001");
        return b;
    }

    // 1.1*200 =>2.200  null*200=>null 保留两位小数
    private static Double multiply(Double a, Double b,Integer scale) {
        if (a == null || b == null) {
            return null;
        } else {
            BigDecimal c = new BigDecimal(a * (b / 100));
            BigDecimal r = c.setScale(10, BigDecimal.ROUND_HALF_UP);
            double o = r.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
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

    private static Double reduce(Double r, Double... as) {
        for (Double a : as) {
            if (a != null) {
                r -= a;
            }
        }
        return r;
    }
}
