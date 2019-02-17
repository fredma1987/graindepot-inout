package com.zhoubi.graindepot.controller;


import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.IndividualBiz;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.rpc.IVideoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @ModelAttribute("ctx")
    public void getAccount(Model model, HttpServletRequest request) {
        String ctx = request.getContextPath();
        if (ctx.endsWith("/"))
            ctx.substring(0, ctx.length() - 1);
        model.addAttribute("ctx", ctx);
    }

    //入库登记
    @RequestMapping(value = "/toInRegister", method = RequestMethod.GET)
    public String toInRegister(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Video video=videoService.selectRegiterVideo(user.getGraindepotid());
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
        Video video=videoService.selectRegiterVideo(user.getGraindepotid());
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
    public String toOutSupple(Model model){
        model.addAttribute("title","出库补单");
        return "out/outsupple";
    }

    //保存更新入库单
    @PostMapping("/editIn")
    @ResponseBody
    public JsonResult editIn(Inout inout) {
        BaseUser user = getCurrentUser();
        if (inout.getBillid() == null) {
            inout.setBilldate(new Date());
            int graindepotid = user.getGraindepotid();
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

                inout.setBilltype(1);
                inout.setBillstate((short) 1);
                inout.setInoutflag((short) 1);
                inout.setBillstage((short) 1);
                inout.setRegopid(user.getUserid());
                inout.setRegtime(new Date());
                inout.setRegstate((short) 0);
                inout.setCreateuserid(user.getUserid());
                inout.setCreatetime(new Date());
                inoutBiz.insert(inout);
            }
            return new JsonResult(inout, "添加成功", true);
        } else {
            inout.setUpdatetime(new Date());
            inout.setRegtime(new Date());
            inout.setRegstate((short) 0);
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
        if (inout.getBillid() == null) {
            inout.setBilldate(new Date());
            int graindepotid = user.getGraindepotid();
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

                inout.setBilltype(1);
                inout.setBillstate((short) 1);
                inout.setInoutflag((short) -1);
                inout.setBillstage((short) 1);
                inout.setRegopid(user.getUserid());
                inout.setRegtime(new Date());
                inout.setRegstate((short) 0);
                inout.setCreateuserid(user.getUserid());
                inout.setCreatetime(new Date());
                inoutBiz.insert(inout);
            }
            return new JsonResult(inout, "添加成功", true);
        } else {
            inout.setUpdatetime(new Date());
            inout.setRegtime(new Date());
            inout.setRegstate((short) 0);
            inoutBiz.updateRegisterInout(inout);
            Inout io = inoutBiz.selectById(inout.getBillid());
            return new JsonResult(io, "修改成功", true);
        }
    }

    @PostMapping("/inout/updateByMap")
    @ResponseBody
    public JsonResult updateByMap(HttpServletRequest request) {
        Map param = request.getParameterMap();
        inoutBiz.updateMap(param);
        return new JsonResult("更新写卡字段成功", true);
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
            individualBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //更新 根据身份证和库点进行更新
            individualBiz.updateByIdcardAndGraindepotid(item);
            return new JsonResult("修改成功", true);
        }
    }

    /**
     * 入库单查询
     *
     * @param billcode
     * @return
     */
    @GetMapping("inout/oneIn")
    @ResponseBody
    public JsonResult inoutByBillcode(String billcode) {
        UserAddress ua = getUserAddress();
        /*String trueBillcode = null;
        if (StringUtils.isNotEmpty(billcode)) {
            String[] billcodes = billcode.split("-");
            String billcodePrefix = billcodes[0] + "-" + billcodes[1]
                    + "-" + billcodes[2] + "-";
            Integer num = Integer.parseInt(billcodes[3].trim());
            trueBillcode = billcodePrefix + String.format("%04d", num);
        } else {
            String maxBillcode = inoutBiz.getMaxBillcode(ua.getGraindepotid());
            trueBillcode = maxBillcode;
        }*/
        if (StringUtils.isEmpty(billcode)) {
            return new JsonResult(new Inout(),"未查询到相关记录", false);
        }
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        param.put("billcode", billcode);
        Inout inout = inoutBiz.selectOne(param);
        if (inout != null) {
            return new JsonResult(inout, "查询成功", true);
        } else {
            return new JsonResult(new Inout(), "未查询到相关记录", false);
        }

    }

    /**
     * 入库单查询
     *
     * @param billid
     * @param flag   1:前一单 3:后一单
     * @return
     */
    @GetMapping("inout/findOneIn")
    @ResponseBody
    public JsonResult findOneIn(Integer billid, Integer flag) {
        UserAddress ua = getUserAddress();
        Inout inout = null;
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
        if (inout != null) {
            return new JsonResult(inout, "查询成功", true);
        } else {
            return new JsonResult(null, "未查询到相关记录", false);
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

    public static void main(String[] args) {
        String test = "0007";
        Integer a = Integer.parseInt(test);
        // String format = String.format("%04d", a);
        System.out.println(a);
    }
}
