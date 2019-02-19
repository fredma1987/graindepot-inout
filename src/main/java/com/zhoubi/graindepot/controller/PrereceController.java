package com.zhoubi.graindepot.controller;

import com.github.pagehelper.StringUtil;
import com.zhoubi.graindepot.base.JsonResult;
import com.zhoubi.graindepot.base.PagerModel;
import com.zhoubi.graindepot.bean.BaseUser;
import com.zhoubi.graindepot.bean.Prerece;
import com.zhoubi.graindepot.biz.PrereceBiz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public JsonResult prereceEdit(Prerece prerece) throws Exception {
        BaseUser user=getCurrentUser();
        if(prerece.getKeyid()==null||prerece.getKeyid()==0){
            prerece.setGroupid(user.getGroupid());
            prerece.setCompanyid(user.getCompanyid());
            prerece.setGraindepotid(user.getGraindepotid());
            prerece.setBilldate(new Date());
            String maxBillcode = prereceBiz.getMaxBillcode(user.getGraindepotid());
            if (StringUtils.isNotEmpty(maxBillcode)) {
                //能找到当天最大的单据号
                String[] maxBillcodes = maxBillcode.split("-");
                prerece.setBillcode(maxBillcodes[0] + "-" + maxBillcodes[1]
                        + "-" + maxBillcodes[2] + "-" + String.format("%04d", Integer.parseInt(maxBillcodes[3]) + 1));
            } else {
                //不能能找到当天最大的单据号
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String format = sdf.format(new Date());
                prerece.setBillcode(format + "-0001");
            }
            prerece.setBillstate((short)0);
            prerece.setCreateuserid(user.getUserid());
            prerece.setCreatetime(new Date());
            prereceBiz.insert(prerece);
            return new JsonResult("新增成功", true);
        }else{
            prerece.setUpdatetime(new Date());
            Map map = initMap(prerece);
            prereceBiz.updateMap(map);
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
