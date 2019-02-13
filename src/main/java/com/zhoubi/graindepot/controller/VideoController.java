package com.zhoubi.graindepot.controller;

import com.github.pagehelper.StringUtil;
import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Video;
import com.zhoubi.graindepot.biz.VideoBiz;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/12/012.
 */
@RestController("")
public class VideoController extends BaseController{
    @Autowired
    private VideoBiz videoBiz;
    @GetMapping("video/list/page")
    public PagerModel videoPageList(int start, int length, String videoname) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Video> e =new PagerModel<Video>();
        e.setStart(start);
        e.setLength(length);
        if (StringUtils.isNotEmpty(videoname)) {
            e.putWhere("videoname", "%" + videoname + "%");
        }
        e.putWhere("graindepotid",currentUser.getGraindepotid());
        e.addOrder("channel");
        PagerModel<Video> result=videoBiz.selectListByPage(e);
        return result;
    }
    @GetMapping("video/shibieList")
    @ResponseBody
    public List<Video> shibieList(){
        Map map=new HashMap();
        map.put("isshibie", 1);
        List<Video> list=videoBiz.selectList(map);
        return list;
    }
    @GetMapping("video/shouchuList")
    @ResponseBody
    public List<Video> shouchuList(){
        Map map=new HashMap();
        map.put("isshouchu",1);
        List<Video> list=videoBiz.selectList(map);
        return list;
    }
    @PostMapping("video/edit")
    public JsonResult videoEdit(Video video){
        if(video.getVideoid()==0||video.getVideoid()==null){
            videoBiz.insert(video);
        }else{
            videoBiz.update(video);
        }
        return new JsonResult("修改成功", true);
    }
    @PostMapping("video/del")
    public JsonResult videoDel(String ids){
        if(StringUtil.isEmpty(ids)){
            Map map =new HashMap();
            map.put("Where_IdsStr", ids);
            videoBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }

}
