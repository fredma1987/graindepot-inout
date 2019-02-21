package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Inout;
import com.zhoubi.graindepot.bean.InoutInsp;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.IndividualBiz;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.InspectBiz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/1/30/030.
 */
@Controller
@RequestMapping("onstorage")
public class OnstorageController extends BaseController {
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private IndividualBiz individualBiz;
    @Autowired
    private InspectBiz inspectBiz;


    @GetMapping("toInitOnstorage")
    public String toInitOnstorage(Model model) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "in/initonstorage";
    }

    //保存初始库存单
    @PostMapping("/editInitonstrage")
    @ResponseBody
    public JsonResult editInitonstrage(Inout inout) {
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
                inout.setBilltype(0);
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
            inout.setRegtime(new Date());
            inout.setRegstate(0);
            inoutBiz.updateInitonstorage(inout);
            Inout io = inoutBiz.selectById(inout.getBillid());
            return new JsonResult(io, "修改成功", true);
        }
    }

    /**
     * 入库单查询
     *
     * @param billid
     * @param flag   1:前一单 3:后一单
     * @return
     */
    @GetMapping("findInitonstorage")
    @ResponseBody
    public JsonResult findInitonstorage(Integer billid, Integer flag) {
        UserAddress ua = getUserAddress();
        Inout inout = null;
        Map param = new HashMap();
        param.put("inoutflag", 1);
        param.put("graindepotid", ua.getGraindepotid());
        param.put("billtype", 0);
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

    //通过初始库存单页面保存质检信息
    @PostMapping("editInoutInsp")
    @ResponseBody
    public JsonResult editInspect(InoutInsp inoutInsp) {
        //判断当前billid对应的入库登记是否存在
        Map param = new HashMap();
        param.put("billid", inoutInsp.getBillid());
        List list = inspectBiz.selectList(param);
        if (list.size() > 0) {
            //更新
            inspectBiz.update(inoutInsp);
            return new JsonResult("修改成功", true);
        } else {
            inspectBiz.insert(inoutInsp);
            return new JsonResult("添加成功", true);
        }
    }

}
