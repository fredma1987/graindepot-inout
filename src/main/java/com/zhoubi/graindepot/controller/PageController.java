package com.zhoubi.graindepot.controller;

import com.zhoubi.graindepot.bean.Prerece;
import com.zhoubi.graindepot.bean.Tempfile;
import com.zhoubi.graindepot.bean.UserAddress;
import com.zhoubi.graindepot.bean.Video;
import com.zhoubi.graindepot.biz.PrereceBiz;
import com.zhoubi.graindepot.biz.TempfileBiz;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2019/2/16/016.
 */
@Controller
@RequestMapping("page")
public class PageController extends BaseController {
    @Autowired
    private PrereceBiz prereceBiz;
    @Autowired
    private TempfileBiz tempfileBiz;

    @GetMapping("/prerece")
    public String prerece(Model model) {
        String title = "预收款";
        model.addAttribute("title", title);
        String path = "/prerece/list";
        return path;
    }

    @GetMapping("/prerece/edit")
    public String toEdit(Model model, Integer id) {
        String title = "编辑预收款";
        Prerece item = new Prerece();
        if (id != null) {
            item = prereceBiz.selectById(id);
        }
        model.addAttribute("title", title);
        model.addAttribute("item", item);
        model.addAttribute("id", id);
        String path = "/prerece/edit";
        return path;
    }

    @GetMapping("/prerece/detail/{id}")
    public String toDetail(Model model, @PathVariable int id) {
        String title = "预收款详情";
        Prerece item = prereceBiz.selectById(id);
        model.addAttribute("title", title);
        model.addAttribute("item", item);
        String path = "/prerece/detail";
        return path;
    }

    //----------------------------------ic卡信息---------------------------------------------
    //ic卡信息主页
    @GetMapping("/iccard")
    public String to_iccard(Model model) {
        String title = "ic卡信息";
        model.addAttribute("title", title);
        String path = "iccard/detail";
        return path;
    }

    //----------------------------------单据异常处理---------------------------------------------
    //单据异常处理主页
    @GetMapping("/exception")
    public String to_exception(Model model, Integer inoutflag) {
        String title = "单据异常处理";
        model.addAttribute("title", title);
        model.addAttribute("inoutflag", inoutflag);
        String path = "exception/list";
        return path;
    }

    //----------------------------------打印模板---------------------------------------------
    @GetMapping("/tempfile")
    public String tempfile(Model model) {
        String title = "打印模板";
        model.addAttribute("title", title);
        String path = "/tempfile/list";
        return path;
    }

    @GetMapping("/tempfile/edit")
    public String to_tempfile_edit(Model model, Integer id) {
        String title = "编辑打印模板";
        Tempfile item = new Tempfile();
        if (id != null) {
            item = tempfileBiz.selectById(id);
        }
        model.addAttribute("title", title);
        model.addAttribute("item", item);
        model.addAttribute("id", id);
        String path = "/tempfile/edit";
        return path;
    }

    @GetMapping("/tempfile/detail/{id}")
    public String to_tempfile_detail(Model model, @PathVariable int id) {
        String title = "打印模板详情";
        Tempfile item = tempfileBiz.selectById(id);
        model.addAttribute("title", title);
        model.addAttribute("item", item);
        String path = "/tempfile/detail";
        return path;
    }

    //----------------------------------入库日报表---------------------------------------------
    @GetMapping("/dayin")
    public String dayin(Model model) {
        String title = "入库日报表";
        UserAddress ua=getUserAddress();
        model.addAttribute("title", title);
        model.addAttribute("userAddress", ua);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("billdate", sdf.format(new Date()));
        String path = "/report/dayin";
        return path;
    }
    //----------------------------------入库日范围报表---------------------------------------------
    @GetMapping("/dayrangein")
    public String dayrangein(Model model) {
        String title = "入库日范围报表";
        UserAddress ua=getUserAddress();
        model.addAttribute("title", title);
        model.addAttribute("userAddress", ua);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstdayOfmonth = calendar.getTime();
        model.addAttribute("billdateEnd", sdf.format(new Date()));
        model.addAttribute("billdateStart", sdf.format(firstdayOfmonth));
        String path = "/report/dayrangein";
        return path;
    }
    //----------------------------------出库日范围报表---------------------------------------------
    @GetMapping("/dayrangeout")
    public String dayrangeout(Model model) {
        String title = "出库日范围报表";
        UserAddress ua=getUserAddress();
        model.addAttribute("title", title);
        model.addAttribute("userAddress", ua);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstdayOfmonth = calendar.getTime();
        model.addAttribute("billdateEnd", sdf.format(new Date()));
        model.addAttribute("billdateStart", sdf.format(firstdayOfmonth));
        String path = "/report/dayrangeout";
        return path;
    }
    //----------------------------------实时库存---------------------------------------------
    @GetMapping("/currentOnstorage")
    public String currentOnstorage(Model model) {
        String title = "实时库存报表";
        UserAddress ua=getUserAddress();
        model.addAttribute("title", title);
        model.addAttribute("userAddress", ua);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstdayOfmonth = calendar.getTime();
        model.addAttribute("billdateEnd", sdf.format(new Date()));
        model.addAttribute("billdateStart", sdf.format(firstdayOfmonth));
        String path = "/report/currentOnstorage";
        return path;
    }
}
