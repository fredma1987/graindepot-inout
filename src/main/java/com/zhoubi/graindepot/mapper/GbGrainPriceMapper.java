package com.zhoubi.graindepot.mapper;import com.zhoubi.graindepot.bean.GbGrainPrice;import com.zhoubi.graindepot.base.BaseMapper;import java.util.List;import java.util.Map;public interface GbGrainPriceMapper extends BaseMapper<GbGrainPrice> {    GbGrainPrice getLatestOne(Map param);}