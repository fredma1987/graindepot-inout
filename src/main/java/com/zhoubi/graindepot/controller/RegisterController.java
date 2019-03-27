package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.IndividualBiz;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.InspectBiz;
import com.zhoubi.graindepot.rpc.IVideoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-12-19.
 */
@Controller
@RequestMapping("register")
public class RegisterController extends BaseController {
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private IVideoService videoService;
    @Autowired
    private IndividualBiz individualBiz;
    @Autowired
    private InspectBiz inspectBiz;

    @ModelAttribute("ctx")
    public void getAccount(Model model, HttpServletRequest request) {
        String ctx = request.getContextPath();
        if (ctx.endsWith("/"))
            ctx.substring(0, ctx.length() - 1);
        model.addAttribute("ctx", ctx);
    }

    //入库登记
    @RequestMapping(value = "/toInRegister", method = RequestMethod.GET)
    public String toInRegister(Model model, HttpServletRequest request
            , HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Video video = videoService.selectRegiterVideo(user.getGraindepotid());
        model.addAttribute("video", video);
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "入库登记");
        return "in/register";
    }

    //出库登记
    @RequestMapping(value = "/toOutRegister", method = RequestMethod.GET)
    public String toOutRegister(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Video video = videoService.selectRegiterVideo(user.getGraindepotid());
        model.addAttribute("video", video);
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "出库登记");
        return "out/register";
    }

    //补单入库单据
    @GetMapping("toInSupple")
    public String toInSupple(Model model) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "入库补单");
        return "in/insupple";
    }

    //补单出库单据
    @GetMapping("toOutSupple")
    public String toOutSupple(Model model) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("title", "出库补单");
        return "out/outsupple";
    }

    //保存更新入库单
    @PostMapping("/editIn")
    @ResponseBody
    public JsonResult editIn(Inout inout) {
        BaseUser user = getCurrentUser();
        UserAddress ua=getUserAddress();
        if (inout.getBillid() == null) {
            int graindepotid = ua.getGraindepotid();
            synchronized (graindepotid + "") {
                String maxBillcode = inoutBiz.getMaxBillcode(graindepotid);
                if (StringUtils.isNotEmpty(maxBillcode)) {
                    //能找到当天最大的单据号
                    String[] maxBillcodes = maxBillcode.split("-");
                    inout.setBillcode(maxBillcodes[0] + "-" + maxBillcodes[1]
                            + "-" + maxBillcodes[2] + "-" + String.format("%04d", Integer.parseInt(maxBillcodes[3]) + 1));
                } else {
                    //不能能找到当天最大的单据号
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String format = sdf.format(new Date());
                    inout.setBillcode(format + "-0001");
                }
                inout.setBilldate(new Date());
                inout.setGroupid(ua.getGroupid());
                inout.setCompanyid(ua.getCompanyid());
                inout.setGraindepotid(ua.getGraindepotid());
                inout.setBilltype(1);
                inout.setBillstate(1);
                inout.setInoutflag(1);
                inout.setBillstage(1);
                inout.setRegopid(user.getUserid());
                inout.setRegtime(new Date());
                inout.setRegstate(0);
                inout.setCreateuserid(user.getUserid());
                inout.setCreatetime(new Date());
                inoutBiz.insert(inout);
            }
            return new JsonResult(inout, "添加成功", true);
        } else {
            inout.setUpdatetime(new Date());
            inoutBiz.updateRegisterInout(inout);
            Inout io = inoutBiz.selectById(inout.getBillid());
            return new JsonResult(io, "修改成功", true);
        }
    }

    //保存更新出库单
    @PostMapping("/editOut")
    @ResponseBody
    public JsonResult editOut(Inout inout) {
        BaseUser user = getCurrentUser();
        UserAddress ua=getUserAddress();
        if (inout.getBillid() == null) {
            int graindepotid = ua.getGraindepotid();
            synchronized (graindepotid + "") {
                String maxBillcode = inoutBiz.getMaxBillcode(graindepotid);
                if (StringUtils.isNotEmpty(maxBillcode)) {
                    //能找到当天最大的单据号
                    String[] maxBillcodes = maxBillcode.split("-");
                    inout.setBillcode(maxBillcodes[0] + "-" + maxBillcodes[1]
                            + "-" + maxBillcodes[2] + "-" + String.format("%04d", Integer.parseInt(maxBillcodes[3]) + 1));
                } else {
                    //不能能找到当天最大的单据号
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String format = sdf.format(new Date());
                    inout.setBillcode(format + "-0001");
                }
                inout.setBilldate(new Date());
                inout.setGroupid(ua.getGroupid());
                inout.setCompanyid(ua.getCompanyid());
                inout.setGraindepotid(ua.getGraindepotid());
                inout.setBilltype(1);
                inout.setBillstate(1);
                inout.setInoutflag(-1);
                inout.setBillstage(1);
                inout.setRegopid(user.getUserid());
                inout.setRegtime(new Date());
                inout.setRegstate(0);
                inout.setCreateuserid(user.getUserid());
                inout.setCreatetime(new Date());
                inoutBiz.insert(inout);
            }
            return new JsonResult(inout, "添加成功", true);
        } else {
            inout.setUpdatetime(new Date());
            inoutBiz.updateRegisterInout(inout);
            Inout io = inoutBiz.selectById(inout.getBillid());
            return new JsonResult(io, "修改成功", true);
        }
    }

    @PostMapping("/inout/updateByMap")
    @ResponseBody
    public JsonResult updateByMap(Integer Where_billid,Integer regstate,String iccardnum) {
        Map param = new HashMap();
        if (Where_billid==null||regstate==null) {
            return new JsonResult("更新写卡字段失败",false);
        }else {
            param.put("Where_billid",Where_billid);
            param.put("regstate",regstate);
            param.put("iccardnum",iccardnum);
            param.put("backicflag",0);
            inoutBiz.updateMap(param);
            return new JsonResult("更新写卡字段成功", true);
        }

    }


    //将涉粮个人信息保存
    @PostMapping("/addIndividual")
    @ResponseBody
    public JsonResult addIndividual(Individual item) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        //判断是否已存在，通过身份证号和库点id
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        param.put("idcard", item.getIdcard());
        int count = individualBiz.checkRepeat(param);
        if (count == 0) {
            //新增
            item.setCreateuserid(user.getUserid());
            item.setCreatetime(new Date());
            item.setGraindepotid(ua.getGraindepotid());
            item.setGroupid(ua.getGroupid());
            item.setCompanyid(ua.getCompanyid());
            individualBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //更新 根据身份证和库点进行更新
            individualBiz.updateByIdcardAndGraindepotid(item);
            return new JsonResult("修改成功", true);
        }
    }

    /**
     * 出库单查询
     *
     * @param billid
     * @param flag   1:前一单 3:后一单
     * @return
     */
    @GetMapping("inout/findOneOut")
    @ResponseBody
    public JsonResult findOneOut(Integer billid, Integer flag) {
        UserAddress ua = getUserAddress();
        Inout inout = null;
        Map param = new HashMap();
        param.put("inoutflag", -1);
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
        if (inout != null) {
            return new JsonResult(inout, "查询成功", true);
        } else {
            return new JsonResult(null, "未查询到相关记录", false);
        }

    }

    //根据姓名和库点来模糊查询姓名
    @PostMapping("/individual/byName")
    @ResponseBody
    public List<Individual> individual_byName(String name) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        //判断是否已存在，通过身份证号和库点id
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        param.put("name", "%" + name + "%");
        List<Individual> result = individualBiz.listByNameAndGraindepotid(param);
        return result;
    }


    //保存更新入库单
    @PostMapping("/editInSupple")
    @ResponseBody
    public JsonResult editIn(Inout inout,InoutInsp inoutInsp) {
        BaseUser user = getCurrentUser();
        UserAddress ua=getUserAddress();
        if (inout.getBillid() == null) {
            int graindepotid = ua.getGraindepotid();
            synchronized (graindepotid + "") {
                String maxBillcode = inoutBiz.getMaxBillcode(graindepotid);
                if (StringUtils.isNotEmpty(maxBillcode)) {
                    //能找到当天最大的单据号
                    String[] maxBillcodes = maxBillcode.split("-");
                    inout.setBillcode(maxBillcodes[0] + "-" + maxBillcodes[1]
                            + "-" + maxBillcodes[2] + "-" + String.format("%04d", Integer.parseInt(maxBillcodes[3]) + 1));
                } else {
                    //不能能找到当天最大的单据号
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String format = sdf.format(new Date());
                    inout.setBillcode(format + "-0001");
                }
                inout.setBilldate(new Date());
                inout.setGroupid(ua.getGroupid());
                inout.setCompanyid(ua.getCompanyid());
                inout.setGraindepotid(ua.getGraindepotid());
                inout.setBilltype(2);
                inout.setBillstate(1);
                inout.setInoutflag(1);
                inout.setBillstage(7);
                inout.setRegopid(user.getUserid());
                inout.setRegtime(new Date());
                inout.setRegstate(0);
                inout.setSamplingoptid(user.getUserid());
                inout.setSamplingstate(1);
                inout.setSamplingtime(new Date());
                inout.setInspectoptid(user.getUserid());
                inout.setInspecttime(new Date());
                inout.setInspectstate(1);
                inout.setGwopid(user.getUserid());
                inout.setGwtime(new Date());
                inout.setGwstate(1);
                inout.setTarestate(1);
                inout.setTareoptid(user.getUserid());
                inout.setTaretime(new Date());
                inout.setValuebinoptid(user.getUserid());
                inout.setValuebinstarttime(new Date());
                inout.setValuebinendtime(new Date());
                inout.setValuebinstate(1);
                inout.setPaidoptid(user.getUserid());
                inout.setPaidtime(new Date());
                inout.setPaiddate(new Date());
                inout.setPaidstate(1);
                inout.setCreateuserid(user.getUserid());
                inout.setCreatetime(new Date());
                inoutBiz.insert(inout);
                //插入b_inout_insp
                inoutInsp.setBillid(inout.getBillid());
                Double waterdeduweight = null, impudeduweight = null, bulkdensitydeduweight = null, yrkdeduweight = null, ukdeduweight = null, otmsdeduweight = null, zjmldeduweight = null, cmldeduweight = null, gwcmdeduweight = null, hhldeduweight = null, wdeduweightx = null, ideduweightx = null, odeduweightx = null;
                Double netweight = inout.getNetweight();
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
                inoutInsp.setWaterdeduweight(waterdeduweight);
                inoutInsp.setImpudeduweight(impudeduweight);
                inoutInsp.setBulkdensitydeduweight(bulkdensitydeduweight);
                inoutInsp.setYrkdeduweight(yrkdeduweight);
                inoutInsp.setUkdeduweight(ukdeduweight);
                inoutInsp.setOtmsdeduweight(otmsdeduweight);
                inoutInsp.setZjmldeduweight(zjmldeduweight);
                inoutInsp.setCmldeduweight(cmldeduweight);
                inoutInsp.setGwcmdeduweight(cmldeduweight);
                inoutInsp.setHhldeduweight(hhldeduweight);
                inoutInsp.setGwcmdeduweight(gwcmdeduweight);
                inoutInsp.setHhldeduweight(hhldeduweight);
                inoutInsp.setWdeduweightx(wdeduweightx);
                inoutInsp.setIdeduweightx(ideduweightx);
                inoutInsp.setOdeduweightx(odeduweightx);
                inspectBiz.insert(inoutInsp);
            }
            return new JsonResult(inout, "添加成功", true);
        } else {
            inout.setUpdatetime(new Date());
            inout.setRegtime(new Date());
            inout.setRegstate(0);
            inoutBiz.updateInsupple(inout);
            //更新b_inout_insp
            inout.setBillid(inout.getBillid());
            Double waterdeduweight = null, impudeduweight = null, bulkdensitydeduweight = null, yrkdeduweight = null, ukdeduweight = null, otmsdeduweight = null, zjmldeduweight = null, cmldeduweight = null, gwcmdeduweight = null, hhldeduweight = null, wdeduweightx = null, ideduweightx = null, odeduweightx = null;
            Double netweight = inout.getNetweight();
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
            inoutInsp.setWaterdeduweight(waterdeduweight);
            inoutInsp.setImpudeduweight(impudeduweight);
            inoutInsp.setBulkdensitydeduweight(bulkdensitydeduweight);
            inoutInsp.setYrkdeduweight(yrkdeduweight);
            inoutInsp.setUkdeduweight(ukdeduweight);
            inoutInsp.setOtmsdeduweight(otmsdeduweight);
            inoutInsp.setZjmldeduweight(zjmldeduweight);
            inoutInsp.setCmldeduweight(cmldeduweight);
            inoutInsp.setGwcmdeduweight(cmldeduweight);
            inoutInsp.setHhldeduweight(hhldeduweight);
            inoutInsp.setGwcmdeduweight(gwcmdeduweight);
            inoutInsp.setHhldeduweight(hhldeduweight);
            inoutInsp.setWdeduweightx(wdeduweightx);
            inoutInsp.setIdeduweightx(ideduweightx);
            inoutInsp.setOdeduweightx(odeduweightx);
            inspectBiz.update(inoutInsp);
            Inout io = inoutBiz.selectById(inout.getBillid());
            return new JsonResult(io, "修改成功", true);
        }
    }

    //保存更新入库单
    @PostMapping("/editOutSupple")
    @ResponseBody
    public JsonResult editOut(Inout inout,InoutInsp inoutInsp) {
        BaseUser user = getCurrentUser();
        UserAddress ua=getUserAddress();
        if (inout.getBillid() == null) {
            int graindepotid = ua.getGraindepotid();
            synchronized (graindepotid + "") {
                String maxBillcode = inoutBiz.getMaxBillcode(graindepotid);
                if (StringUtils.isNotEmpty(maxBillcode)) {
                    //能找到当天最大的单据号
                    String[] maxBillcodes = maxBillcode.split("-");
                    inout.setBillcode(maxBillcodes[0] + "-" + maxBillcodes[1]
                            + "-" + maxBillcodes[2] + "-" + String.format("%04d", Integer.parseInt(maxBillcodes[3]) + 1));
                } else {
                    //不能能找到当天最大的单据号
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String format = sdf.format(new Date());
                    inout.setBillcode(format + "-0001");
                }
                inout.setBilldate(new Date());
                inout.setGroupid(ua.getGroupid());
                inout.setCompanyid(ua.getCompanyid());
                inout.setGraindepotid(ua.getGraindepotid());
                inout.setBilltype(2);
                inout.setBillstate(1);
                inout.setInoutflag(-1);
                inout.setBillstage(7);
                inout.setRegopid(user.getUserid());
                inout.setRegtime(new Date());
                inout.setRegstate(0);
                inout.setSamplingoptid(user.getUserid());
                inout.setSamplingstate(1);
                inout.setSamplingtime(new Date());
                inout.setInspectoptid(user.getUserid());
                inout.setInspecttime(new Date());
                inout.setInspectstate(1);
                inout.setGwopid(user.getUserid());
                inout.setGwtime(new Date());
                inout.setGwstate(1);
                inout.setTarestate(1);
                inout.setTareoptid(user.getUserid());
                inout.setTaretime(new Date());
                inout.setValuebinoptid(user.getUserid());
                inout.setValuebinstarttime(new Date());
                inout.setValuebinendtime(new Date());
                inout.setValuebinstate(1);
               /* inout.setPaidoptid(user.getUserid());
                inout.setPaidtime(new Date());
                inout.setPaiddate(new Date());
                inout.setPaidstate(1);*/
                inout.setCreateuserid(user.getUserid());
                inout.setCreatetime(new Date());
                inoutBiz.insert(inout);
                //插入b_inout_insp
                inoutInsp.setBillid(inout.getBillid());
                Double waterdeduweight = null, impudeduweight = null, bulkdensitydeduweight = null, yrkdeduweight = null, ukdeduweight = null, otmsdeduweight = null, zjmldeduweight = null, cmldeduweight = null, gwcmdeduweight = null, hhldeduweight = null, wdeduweightx = null, ideduweightx = null, odeduweightx = null;
                Double netweight = inout.getNetweight();
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
                inoutInsp.setWaterdeduweight(waterdeduweight);
                inoutInsp.setImpudeduweight(impudeduweight);
                inoutInsp.setBulkdensitydeduweight(bulkdensitydeduweight);
                inoutInsp.setYrkdeduweight(yrkdeduweight);
                inoutInsp.setUkdeduweight(ukdeduweight);
                inoutInsp.setOtmsdeduweight(otmsdeduweight);
                inoutInsp.setZjmldeduweight(zjmldeduweight);
                inoutInsp.setCmldeduweight(cmldeduweight);
                inoutInsp.setGwcmdeduweight(cmldeduweight);
                inoutInsp.setHhldeduweight(hhldeduweight);
                inoutInsp.setGwcmdeduweight(gwcmdeduweight);
                inoutInsp.setHhldeduweight(hhldeduweight);
                inoutInsp.setWdeduweightx(wdeduweightx);
                inoutInsp.setIdeduweightx(ideduweightx);
                inoutInsp.setOdeduweightx(odeduweightx);
                inspectBiz.insert(inoutInsp);
            }
            return new JsonResult(inout, "添加成功", true);
        } else {
            inout.setUpdatetime(new Date());
            inout.setRegtime(new Date());
            inout.setRegstate(0);
            inoutBiz.updateOutsupple(inout);
            //更新b_inout_insp
            inout.setBillid(inout.getBillid());
            Double waterdeduweight = null, impudeduweight = null, bulkdensitydeduweight = null, yrkdeduweight = null, ukdeduweight = null, otmsdeduweight = null, zjmldeduweight = null, cmldeduweight = null, gwcmdeduweight = null, hhldeduweight = null, wdeduweightx = null, ideduweightx = null, odeduweightx = null;
            Double netweight = inout.getNetweight();
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
            inoutInsp.setWaterdeduweight(waterdeduweight);
            inoutInsp.setImpudeduweight(impudeduweight);
            inoutInsp.setBulkdensitydeduweight(bulkdensitydeduweight);
            inoutInsp.setYrkdeduweight(yrkdeduweight);
            inoutInsp.setUkdeduweight(ukdeduweight);
            inoutInsp.setOtmsdeduweight(otmsdeduweight);
            inoutInsp.setZjmldeduweight(zjmldeduweight);
            inoutInsp.setCmldeduweight(cmldeduweight);
            inoutInsp.setGwcmdeduweight(cmldeduweight);
            inoutInsp.setHhldeduweight(hhldeduweight);
            inoutInsp.setGwcmdeduweight(gwcmdeduweight);
            inoutInsp.setHhldeduweight(hhldeduweight);
            inoutInsp.setWdeduweightx(wdeduweightx);
            inoutInsp.setIdeduweightx(ideduweightx);
            inoutInsp.setOdeduweightx(odeduweightx);
            inspectBiz.update(inoutInsp);
            Inout io = inoutBiz.selectById(inout.getBillid());
            return new JsonResult(io, "修改成功", true);
        }
    }

    @GetMapping("canWriteCard")
    @ResponseBody
    public JsonResult canWriteCard(String iccardnum){
        BaseUser user=getCurrentUser();
        Map param=new HashMap();
        param.put("graindepot",user.getGraindepotid());
        param.put("iccardnum",iccardnum);
        param.put("notbackicflag",1);
        List<Inout> list = inoutBiz.selectList(param);
        if (list.size()>0) {
            return new JsonResult("写卡失败(该ic卡还未退卡)",false);
        }else {
            return new JsonResult("ic卡已退卡",true);
        }

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

}
