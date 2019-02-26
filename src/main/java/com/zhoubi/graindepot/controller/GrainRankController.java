package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.*;
import com.zhoubi.graindepot.biz.GrainRankBiz;
import com.zhoubi.graindepot.biz.InspectItemBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("grainRank")
public class GrainRankController extends BaseController{
    @Autowired
    private GrainRankBiz grainRankBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toGrainRank")
    public String toGrainRank(Model model){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "grainRank/list";
    }
    @GetMapping("toEdit")
    public String toEdit(Model model,Integer id){
        String title = "粮食品种等级标准";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        GrainRank item = new GrainRank();
        if (id != null) {
            item = grainRankBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "grainRank/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel ggrainRankPageList(int start, int length, Integer grainid) {
        UserAddress ua=getUserAddress();
        PagerModel<GrainRank> e = new PagerModel();
        e.addOrder("grainid asc,orderno desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("grainid", grainid);
        e.putWhere("graindepotid", ua.getGraindepotid());
        PagerModel<GrainRank> result = grainRankBiz.selectListByPage(e);
        return result;
    }

    @PostMapping("/edit")
    @ResponseBody
    public JsonResult grainRankEdit(GrainRank item){
        UserAddress ua=getUserAddress();
        item.setGraindepotid(ua.getGraindepotid());
        if (item.getKeyid()==null) {
            //新增
            grainRankBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            grainRankBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult grainRankDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            grainRankBiz.deleteMap(map);
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
        int result = grainRankBiz.checkRepeat(map);
        if (result == 0) {
            return "{\"valid\":true}";
        } else {
            return "{\"valid\":false}";
        }

    }
}
