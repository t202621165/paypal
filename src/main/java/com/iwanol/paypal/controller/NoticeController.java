package com.iwanol.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.Notice;
import com.iwanol.paypal.service.NoticeService;
import com.iwanol.paypal.until.PageData;

@RestController
@RequestMapping(value = "notice", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('notice.html')")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	@PostMapping("/list")
	public Map<String, Object> list(PageData pageData, Notice notice) {
		return noticeService.findEntityBySpec(pageData, notice);
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('notice/add')")
	public JSONObject add(Notice notice) {
		return noticeService.addNotice(notice);
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('notice/delete')")
	public JSONObject delete(Notice notice) {
		return noticeService.deleteNotice(notice);
	}
	
	@PostMapping("/state")
	@PreAuthorize("hasAuthority('notice/state')")
	public JSONObject state(Notice notice) {
		return noticeService.updateState(notice);
	}
}
