package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.GrainInspectItem;
import com.zhoubi.graindepot.bean.InspectItem;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.GrainInspectItemBiz;
import com.zhoubi.graindepot.biz.InspectItemBiz;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("grainInspectItem")
public class GrainInspectItemController  extends BaseController{
    @Autowired
    private GrainInspectItemBiz grainInspectItemBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toGrainInspectItem")
    public String toGrainInspectItem(Model model){
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "grainInspectItem/list";
    }
    @GetMapping("toEdit")
    public String toEdit(Model model,Integer id){
        String title = "粮食品种检验标准";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        GrainInspectItem item = new GrainInspectItem();
        if (id != null) {
            item = grainInspectItemBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "grainInspectItem/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel grainInspectItemPageList(int start, int length, Integer grainid) {
        PagerModel<GrainInspectItem> e = new PagerModel();
        e.addOrder("grainid asc,orderno desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("grainid", grainid);
        PagerModel<GrainInspectItem> result = grainInspectItemBiz.selectListByPage(e);
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
    public JsonResult grainInspectItemEdit(GrainInspectItem item) throws ParseException {

        if (item.getKeyid()==null) {
            //新增
            grainInspectItemBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            grainInspectItemBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    public JsonResult grainInspectItemDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            grainInspectItemBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }
}
