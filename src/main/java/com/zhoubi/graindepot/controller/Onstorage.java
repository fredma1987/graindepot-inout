package com.zhoubi.graindepot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2019/1/30/030.
 */
@Controller
@RequestMapping("onstorage")
public class Onstorage extends BaseController{
    @GetMapping("toInitOnstorage")
    public String toInitOnstorage(){
        return "initonstorage";
    }
}
