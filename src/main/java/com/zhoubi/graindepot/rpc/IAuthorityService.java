package com.zhoubi.graindepot.rpc;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@FeignClient("graindepot-base")
@RequestMapping("rpc")
public interface IAuthorityService {
    @RequestMapping(value = "/authority/isAllow", method = RequestMethod.GET)
    @ResponseBody
    public boolean authority_isAllow(@RequestParam(value = "authoritycode") String authoritycod);


}
