package com.zhoubi.graindepot.rpc;

import com.zhoubi.graindepot.bean.Video;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Administrator on 2019/2/16/016.
 */
@FeignClient("graindepot-video")
@RequestMapping("rpc")
public interface IVideoService {
    @RequestMapping(value = "/video/selectRegiterVideo", method = RequestMethod.GET)
    Video selectRegiterVideo(@RequestParam(value = "graindepotid") int graindepotid);
    @RequestMapping(value = "/video/selectWeightVideoList", method = RequestMethod.GET)
    List<Video> selectWeightVideoList(@RequestParam(value = "graindepotid") int graindepotid);
}
