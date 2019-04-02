package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.InoutBiz;
import com.zhoubi.graindepot.biz.MixedBiz;
import com.zhoubi.graindepot.biz.TempfileBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("inout")
public class InoutController extends BaseController {
    @Autowired
    private InoutBiz inoutBiz;
    @Autowired
    private TempfileBiz tempfileBiz;
    @Autowired
    private MixedBiz mixedBiz;

    //出入库dialog列表
    @RequestMapping(value = "/toDialogInout", method = RequestMethod.GET)
    public String toDialogInout(Model model, HttpServletRequest request
            , HttpServletResponse response, Integer inoutflag,Integer type) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("inoutflag", inoutflag);
        model.addAttribute("type", type);//1:从登记页跳转 2:从检验 3:称毛重或者称皮重 4:结算
        model.addAttribute("title", "出入库列表");
        return "inout/dialogList";
    }

    //出入库列表
    @RequestMapping(value = "/toInout", method = RequestMethod.GET)
    public String toInout(Model model,Integer inoutflag) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        model.addAttribute("inoutflag", inoutflag);
        model.addAttribute("title", "出入库列表");
        return "inout/list";
    }

    @GetMapping("/detail/{id}")
    public String toDetail(Model model, @PathVariable int id) {
        String title = "单据详情";
        Inout item = inoutBiz.selectDetailById(id);
        model.addAttribute("title", title);
        model.addAttribute("item", item);
        String path = "/inout/detail";
        return path;
    }

    //查询出所有的出入库单据(显示正常单据 和 补单)
    @GetMapping("list/page")
    @ResponseBody
    public PagerModel<Inout> inout_list(int start, int length, Integer inoutflag
            , String billdate, String billcode, String sellmanname,Integer billtype) {
        UserAddress ua = getUserAddress();
        PagerModel<Inout> e = new PagerModel();
        e.addOrder("billdate desc,billcode desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("graindepotid", ua.getGraindepotid());
        e.putWhere("inoutflag", inoutflag);
        e.putWhere("billdate", billdate);
        e.putWhere("billtype", billtype);
        if (StringUtils.isNotEmpty(sellmanname)) {
            e.putWhere("sellmanname", "%" + sellmanname + "%");
        }
        if (StringUtils.isNotEmpty(billcode)) {
            e.putWhere("billcode", "%" + billcode + "%");
        }
        PagerModel<Inout> result = inoutBiz.selectListByPage(e, "selectInoutPageList", "selectInoutPageCount");
        return result;
    }

    //根据登记流水号来查询姓名等信息
    @PostMapping("/byBillcode")
    @ResponseBody
    public List<Inout> inout_byBillcode(String billcode, String billdate) {
        if (StringUtils.isEmpty(billcode)) {
            return new ArrayList<Inout>();
        }
        UserAddress ua = getUserAddress();
        Map param = new HashMap();
        param.put("graindepotid", ua.getGraindepotid());
        param.put("billdate", billdate);
        String likeBillcode;
        likeBillcode = billdate + "-%" + billcode + "%";
        param.put("billcode", likeBillcode);
        List<Inout> result = inoutBiz.listByBillcodeAndGraindepotid(param);
        return result;
    }

    //根据billcode获取billid
    @GetMapping("billid/byBillcode")
    @ResponseBody
    public JsonResult getBillid(String billcode) {
        Inout inout = new Inout();
        if (StringUtils.isNotEmpty(billcode)) {
            UserAddress ua = getUserAddress();
            Map map = new HashMap();
            map.put("graindepotid", ua.getGraindepotid());
            map.put("billcode", billcode);
            inout = inoutBiz.selectOne(map);
            if (inout==null) {
                inout = new Inout();
            }
        }
        return new JsonResult(inout.getBillid(), true);
    }

    /**
     * @param billid
     * @param flag   1:前一单 2：当前单  3:后一单
     * @return
     */
    @GetMapping("findOneIn")
    @ResponseBody
    public JsonResult findOneIn(Integer billid, Integer flag,Integer billtype) {
        if (billtype==null) {
            billtype=1;
        }
        Inout inout = null;
        if (flag == 2 && billid != null) {
            inout = inoutBiz.selectById(billid);
        }
        if (flag != 2) {
            UserAddress ua = getUserAddress();
            Map param = new HashMap();
            param.put("inoutflag", 1);
            param.put("graindepotid", ua.getGraindepotid());
            param.put("billtype", billtype);
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
     * @param billid
     * @param flag   1:前一单 2：当前单  3:后一单
     * @return
     */
    @GetMapping("findOneOut")
    @ResponseBody
    public JsonResult findOneOut(Integer billid, Integer flag,Integer billtype) {
        if (billtype==null) {
            billtype=1;
        }
        Inout inout = null;
        if (flag == 2 && billid != null) {
            inout = inoutBiz.selectById(billid);
        }
        if (flag != 2) {
            UserAddress ua = getUserAddress();
            Map param = new HashMap();
            param.put("inoutflag", -1);
            param.put("graindepotid", ua.getGraindepotid());
            param.put("billtype", billtype);
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


    //身份证是否列入黑名单
    @RequestMapping(value = "/blanklist/idcard/isExist", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult blanklist_idcard_isExist(Model model,String idcard) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Map map=new HashMap();
        map.put("graindepotid",ua.getGraindepotid());
        map.put("idcard",idcard);
        List<Blanklist> blanklists = mixedBiz.blanklistIsIdCardExist(map);
        if (blanklists.size()>0) {
            return new JsonResult(blanklists.get(0),"已存在",true);
        }else {
            return new JsonResult("不存在",false);
        }
    }

    //单位是否列入黑名单
    @RequestMapping(value = "/blanklist/trader/isExist", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult blanklist_trader_isExist(Model model,String tradername) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        Map map=new HashMap();
        map.put("graindepotid",ua.getGraindepotid());
        map.put("tradername",tradername);
        List<Blanklist> blanklists = mixedBiz.blanklistIsTraderExist(map);
        if (blanklists.size()>0) {
            return new JsonResult(blanklists.get(0),"已存在",true);
        }else {
            return new JsonResult("不存在",false);
        }
    }


}
