package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.GbGrainPrice;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.GbGrainPriceBiz;
import com.zhoubi.graindepot.biz.InspectItemBiz;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
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
import java.util.Map;

@Controller
@RequestMapping("gbGrainPrice")
public class GbGrainPriceController extends BaseController {
    @Autowired
    private GbGrainPriceBiz gbGrainPriceBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toGbGrainPrice")
    public  String toGbGrainPrice(Model model) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "gbGrainPrice/list";
    }

    @GetMapping("toEdit")
    public String toEdit(Model model, Integer id) {
        String title = "粮食品种收购价格";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        GbGrainPrice item = new GbGrainPrice();
        if (id != null) {
            item = gbGrainPriceBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "gbGrainPrice/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel ggbGrainPricePageList(int start, int length, Integer grainid) {
        UserAddress ua = getUserAddress();
        PagerModel<GbGrainPrice> e = new PagerModel();
        e.addOrder("grainid asc,year desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("grainid", grainid);
        e.putWhere("graindepotid", ua.getGraindepotid());
        PagerModel<GbGrainPrice> result = gbGrainPriceBiz.selectListByPage(e);
        return result;
    }

    @PostMapping("/edit")
    @ResponseBody
    public JsonResult gbGrainPriceEdit(GbGrainPrice item) {
        if (item.getKeyid() == null) {
            //新增
            item.setYear(Integer.valueOf(DateFormatUtils.format(new Date(),"yyyy")));
            gbGrainPriceBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            gbGrainPriceBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult gbGrainPriceDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            gbGrainPriceBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }

    @PostMapping("/checkRepeat")
    @ResponseBody
    public String checkRepeat(Integer grainid, Integer keyid, Integer year) {
        Map map = new HashMap();
        map.put("grainid", grainid);
        map.put("keyid", keyid);
        if (year == null) {
            year = Integer.valueOf(DateFormatUtils.format(new Date(), "yyyy"));
        }
        map.put("year", year);
        int result = gbGrainPriceBiz.checkRepeat(map);
        if (result == 0) {
            return "{\"valid\":true}";
        } else {
            return "{\"valid\":false}";
        }

    }
}
