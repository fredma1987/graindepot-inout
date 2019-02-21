package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.GbGrainInspectItem;
import com.zhoubi.graindepot.bean.InspectItem;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.GbGrainInspectItemBiz;
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
@RequestMapping("gbGrainInspectItem")
public class GbGrainInspectItemController extends BaseController{
    @Autowired
    private GbGrainInspectItemBiz gbGrainInspectItemBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toGbGrainInspectItem")
    public String toGbGrainInspectItem(Model model){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "gbGrainInspectItem/list";
    }
    @GetMapping("toEdit")
    public String toEdit(Model model,Integer id){
        String title = "粮食品种检验国际标准";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        GbGrainInspectItem item = new GbGrainInspectItem();
        if (id != null) {
            item = gbGrainInspectItemBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "gbGrainInspectItem/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel gbGrainInspectItemPageList(int start, int length, Integer grainid) {
        UserAddress ua=getUserAddress();
        PagerModel<GbGrainInspectItem> e = new PagerModel();
        e.addOrder("grainid asc,orderno desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("grainid", grainid);
        PagerModel<GbGrainInspectItem> result = gbGrainInspectItemBiz.selectListByPage(e);
        return result;
    }
    //获取所有的检验标准
    @GetMapping("/inspectItem/list")
    @ResponseBody
    public List<InspectItem> inspectItemList(){
        Map param=new HashMap();
        List list = inspectItemBiz.selectList(param);
        return list;
    }

    @PostMapping("/edit")
    @ResponseBody
    public JsonResult gbGrainInspectItemEdit(GbGrainInspectItem item) throws ParseException {

        if (item.getKeyid()==null) {
            //新增
            gbGrainInspectItemBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            gbGrainInspectItemBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult gbGrainInspectItemDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            gbGrainInspectItemBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }

    @PostMapping("/checkRepeat")
    @ResponseBody
    public String checkRepeat(Integer grainid,Integer inspectitemid, Integer keyid) {
        UserAddress ua=getUserAddress();
        Map map = new HashMap();
        map.put("grainid", grainid);
        map.put("inspectitemid", inspectitemid);
        map.put("keyid", keyid);
        int result = gbGrainInspectItemBiz.checkRepeat(map);
        if (result == 0) {
            return "{\"valid\":true}";
        } else {
            return "{\"valid\":false}";
        }

    }
}
