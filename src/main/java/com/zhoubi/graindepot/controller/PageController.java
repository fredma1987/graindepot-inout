package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.bean.Prerece;
import com.zhoubi.graindepot.bean.Video;
import com.zhoubi.graindepot.biz.PrereceBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2019/2/16/016.
 */
@Controller
@RequestMapping("page")
public class PageController extends BaseController {
    @Autowired
    private PrereceBiz prereceBiz;
    @GetMapping("/prerece/list")
    public String prerece(Model model){
        String title="预收款";
        model.addAttribute("title",title);
        String path="/prerece/list";
        return path;
    }
    @GetMapping("/prerece/edit")
    public String toEdit(Model model,Integer id){
        String title="编辑预收款";
        Prerece item=new Prerece();
        if(id!=null) {
            item = prereceBiz.selectById(id);
        }
        model.addAttribute("title",title);
        model.addAttribute("item",item);
        model.addAttribute("id",id);
        String path="/video/edit";
        return path;
    }
    @GetMapping("/prerece/detail/{id}")
    public String toDetail(Model model,@PathVariable int id){
        String title="预收款详情";
        Prerece item=prereceBiz.selectById(id);
        model.addAttribute("title",title);
        model.addAttribute("item",item);
        String path="/video/detail";
        return path;
    }
}
