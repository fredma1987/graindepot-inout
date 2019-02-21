package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.GbGrainRank;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.GbGrainRankBiz;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("gbGrainRank")
public class GbGrainRankController extends BaseController{
    @Autowired
    private GbGrainRankBiz gbGrainRankBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toGbGrainRank")
    public String toGbGrainRank(Model model){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "gbGrainRank/list";
    }
    @GetMapping("toEdit")
    public String toEdit(Model model,Integer id){
        String title = "粮食品种等级划分国标";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        GbGrainRank item = new GbGrainRank();
        if (id != null) {
            item = gbGrainRankBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "gbGrainRank/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel ggbGrainRankPageList(int start, int length, Integer grainid) {
        UserAddress ua=getUserAddress();
        PagerModel<GbGrainRank> e = new PagerModel();
        e.addOrder("grainid asc,orderno desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("grainid", grainid);
        e.putWhere("graindepotid", ua.getGraindepotid());
        PagerModel<GbGrainRank> result = gbGrainRankBiz.selectListByPage(e);
        return result;
    }

    @PostMapping("/edit")
    @ResponseBody
    public JsonResult gbGrainRankEdit(GbGrainRank item){

        if (item.getKeyid()==null) {
            //新增
            gbGrainRankBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            gbGrainRankBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult gbGrainRankDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            gbGrainRankBiz.deleteMap(map);
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
        int result = gbGrainRankBiz.checkRepeat(map);
        if (result == 0) {
            return "{\"valid\":true}";
        } else {
            return "{\"valid\":false}";
        }

    }
}
