package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Tempfile;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.TempfileBiz;
import com.zhoubi.graindepot.biz.InspectItemBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("tempfile")
public class TempfileController extends BaseController {
    @Autowired
    private TempfileBiz tempfileBiz;
    @Autowired
    private InspectItemBiz inspectItemBiz;

    @GetMapping("toTempfile")
    public  String toTempfile(Model model) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "tempfile/list";
    }

    @GetMapping("toEdit")
    public String toEdit(Model model, Integer id) {
        String title = "粮食品种收购价格";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        Tempfile item = new Tempfile();
        if (id != null) {
            item = tempfileBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "tempfile/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel tempfilePageList(int start, int length) {
        UserAddress ua = getUserAddress();
        PagerModel<Tempfile> e = new PagerModel();
        e.addOrder("createtime  desc");
        e.setStart(start);
        e.setLength(length);
        e.putWhere("groupid", ua.getGroupid());
        e.putWhere("graindepotid", ua.getGraindepotid());
        PagerModel<Tempfile> result = tempfileBiz.selectListByPage(e);
        return result;
    }

    @PostMapping("/edit")
    @ResponseBody
    public JsonResult tempfileEdit(Tempfile item) {
        UserAddress ua=getUserAddress();
        BaseUser user=getCurrentUser();
        if (item.getTempid() == null) {
            //新增
            item.setGroupid(ua.getGroupid());
            item.setCompanyid(ua.getCompanyid());
            item.setGraindepotid(ua.getGraindepotid());
            item.setCreatetime(new Date());
            item.setCreateuser(user.getUsername());
            tempfileBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            item.setUpdateuser(user.getUsername());
            item.setUpdatetime(new Date());
            tempfileBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult tempfileDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            tempfileBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }

    @PostMapping("/checkRepeat")
    @ResponseBody
    public String checkRepeat(Integer grainid, Integer keyid, Integer year) {
        /*Map map = new HashMap();
        map.put("grainid", grainid);
        map.put("keyid", keyid);
        if (year == null) {
            year = Integer.valueOf(DateFormatUtils.format(new Date(), "yyyy"));
        }
        map.put("year", year);
        int result = tempfileBiz.checkRepeat(map);
        if (result == 0) {
            return "{\"valid\":true}";
        } else {
            return "{\"valid\":false}";
        }*/
        return null;

    }

    //单据模板
    @GetMapping("list")
    @ResponseBody
    public List<Tempfile> tempfile_list(String tempcode){
        BaseUser user=getCurrentUser();
        UserAddress ua=getUserAddress();
        Map param=new HashMap();
        param.put("graindepotid",ua.getGraindepotid());
        param.put("groupid",ua.getGroupid());
        param.put("tempcode",tempcode);
        List<Tempfile> list = tempfileBiz.selectList(param);
        return list;
    }

    //单据模板
    @GetMapping("one/{tempid}")
    @ResponseBody
    public JsonResult tempfile_one(@PathVariable Integer tempid){
        Tempfile result = tempfileBiz.selectById(tempid);
        return new JsonResult(result,true);
    }
}
