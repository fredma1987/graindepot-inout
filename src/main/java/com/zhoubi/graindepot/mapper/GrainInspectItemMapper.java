package com.zhoubi.graindepot.mapper;import com.zhoubi.graindepot.bean.GrainInspectItem;import com.zhoubi.graindepot.base.BaseMapper;import java.util.List;import java.util.Map;public interface GrainInspectItemMapper extends BaseMapper<GrainInspectItem> {    List<GrainInspectItem> listByGrainid(Map param);}