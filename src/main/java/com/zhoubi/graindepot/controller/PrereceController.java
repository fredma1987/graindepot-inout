package com.zhoubi.graindepot.controller;

import com.github.pagehelper.StringUtil;
import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Prerece;
import com.zhoubi.graindepot.biz.PrereceBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/12/012.
 */
@RestController("")
public class PrereceController extends BaseController{
    @Autowired
    private PrereceBiz prereceBiz;
    @GetMapping("prerece/list/page")
    public PagerModel prerece(int start, int length, Integer traderID) {
        BaseUser currentUser = getCurrentUser();
        PagerModel<Prerece> e =new PagerModel<Prerece>();
        e.setStart(start);
        e.setLength(length);
        if (traderID!=null) {
            e.putWhere("traderid", traderID);
        }
        e.putWhere("graindepotid",currentUser.getGraindepotid());
        e.addOrder("billdate desc");
        PagerModel<Prerece> result=prereceBiz.selectListByPage(e);
        return result;
    }
    @PostMapping("prerece/edit")
    public JsonResult prereceEdit(Prerece prerece){
        if(prerece.getKeyid()==null||prerece.getKeyid()==0){
            prereceBiz.insert(prerece);
            return new JsonResult("新增成功", true);
        }else{
            prereceBiz.update(prerece);
        }
        return new JsonResult("修改成功", true);
    }
    @PostMapping("prerece/del")
    public JsonResult prereceDel(String ids){
        if(StringUtil.isNotEmpty(ids)){
            Map map =new HashMap();
            map.put("Where_IdsStr", ids);
            prereceBiz.deleteMap(map);
        }
        return new JsonResult("删除成功", true);
    }

}
