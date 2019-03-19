package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Lodop;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.biz.LodopBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("lodop")
public class LodopController extends BaseController {
    @Autowired
    private LodopBiz lodopBiz;

    @GetMapping("toLodop")
    public String toLodop(Model model) {
        BaseUser user = getCurrentUser();
        UserAddress ua = getUserAddress();
        model.addAttribute("user", user);
        model.addAttribute("userAddress", ua);
        return "lodop/list";
    }

    @GetMapping("toEdit")
    public String toEdit(Model model, Integer id) {
        String title = "套打模板";
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        Lodop item = new Lodop();
        if (id != null) {
            item = lodopBiz.selectById(id);
        }
        model.addAttribute("item", item);
        return "lodop/edit";
    }

    @GetMapping("/list/page")
    @ResponseBody
    public PagerModel lodopPageList(int start, int length, String graindepotname) {
        UserAddress ua = getUserAddress();
        PagerModel<Lodop> e = new PagerModel();
        e.setStart(start);
        e.setLength(length);
        if (StringUtils.isNotEmpty(graindepotname)) {
            e.putWhere("graindepotname", "%"+graindepotname+"%");
        }

        PagerModel<Lodop> result = lodopBiz.selectListByPage(e);
        return result;
    }


    @PostMapping("/edit")
    @ResponseBody
    public JsonResult lodopEdit(Lodop item) {
        String contentstr=item.getContentstr();
        byte[] content=contentstr.getBytes();
        item.setContent(content);
        if (item.getLodopid() == null) {
            //新增
            item.setCreatetime(new Date());
            lodopBiz.insert(item);
            return new JsonResult("添加成功", true);
        } else {
            //修改
            item.setUpdatetime(new Date());
            lodopBiz.update(item);
            return new JsonResult("修改成功", true);
        }

    }

    @PostMapping("/del")
    @ResponseBody
    public JsonResult lodopDel(String ids) {
        if (StringUtils.isNotEmpty(ids)) {
            Map map = new HashMap();
            map.put("Where_IdsStr", ids);
            lodopBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }


    //单据模板
    @GetMapping("list/{lodoptype}")
    @ResponseBody
    public List<Lodop> lodop_list(@PathVariable(name = "lodoptype") String lodoptype){
        BaseUser user=getCurrentUser();
        UserAddress ua=getUserAddress();
        Map param=new HashMap();
        param.put("graindepotid",ua.getGraindepotid());
        param.put("groupid",ua.getGroupid());
        param.put("lodoptype",lodoptype);
        List<Lodop> list = lodopBiz.selectList(param);
        return list;
    }

    //单据模板
    @GetMapping("one/{lodopid}")
    @ResponseBody
    public JsonResult lodop_one(@PathVariable(name = "lodopid") Integer lodopid){
        Lodop lodop = lodopBiz.selectById(lodopid);
        return new JsonResult(lodop,true);
    }


}
