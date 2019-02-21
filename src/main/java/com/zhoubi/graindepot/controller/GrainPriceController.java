package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.GrainPrice;
import com.zhoubi.graindepot.bean.InspectItem;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.GrainPriceBiz;
import com.zhoubi.graindepot.biz.InspectItemBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("grainPrice")
public class GrainPriceController extends BaseController{
    @Autowired
    private GrainPriceBiz grainPriceBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toGrainPrice")
    public String toGrainPrice(Model model){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "grainPrice/list";
    }
    @GetMapping("toEdit")
    public String toEdit(Model model,Integer id){
        String title = "粮食品种收购价格";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        GrainPrice item = new GrainPrice();
        if (id != null) {
            item = grainPriceBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "grainPrice/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel ggrainPricePageList(int start, int length, Integer grainid) {
        UserAddress ua=getUserAddress();
        PagerModel<GrainPrice> e = new PagerModel();
        e.addOrder("grainid asc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("grainid", grainid);
        e.putWhere("graindepotid", ua.getGraindepotid());
        PagerModel<GrainPrice> result = grainPriceBiz.selectListByPage(e);
        return result;
    }
    @PostMapping("/edit")
    @ResponseBody
    public JsonResult grainPriceEdit(GrainPrice item){
        UserAddress ua=getUserAddress();
        if (item.getKeyid()==null) {
            //新增
            item.setGraindepotid(ua.getGraindepotid());
            grainPriceBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            grainPriceBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult grainPriceDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            grainPriceBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }

    @PostMapping("/checkRepeat")
    @ResponseBody
    public String checkRepeat(Integer grainid, Integer keyid) {
        UserAddress ua=getUserAddress();
        Map map = new HashMap();
        map.put("grainid", grainid);
        map.put("keyid", keyid);
        map.put("graindepotid", ua.getGraindepotid());
        int result = grainPriceBiz.checkRepeat(map);
        if (result == 0) {
            return "{\"valid\":true}";
        } else {
            return "{\"valid\":false}";
        }

    }
}
