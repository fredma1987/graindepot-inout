package com.zhoubi.graindepot.biz;import com.zhoubi.graindepot.bean.Video;import com.zhoubi.graindepot.mapper.VideoMapper;import com.zhoubi.graindepot.base.BaseMapper;import com.zhoubi.graindepot.base.BaseService;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;@Servicepublic class VideoBiz extends BaseService<Video>  {	@Autowired	private VideoMapper VideoMapper;	@Override
	protected BaseMapper<Video> getMapper() {		return VideoMapper;	}}