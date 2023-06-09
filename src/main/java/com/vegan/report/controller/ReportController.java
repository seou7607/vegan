package com.vegan.report.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vegan.report.dto.ReportDTO;
import com.vegan.report.service.ReportService;

@Controller
public class ReportController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ReportService service;

	@RequestMapping(value = "/reportWrite.go")

	public String reportWrite(@RequestParam HashMap<String, Object> params, Model model) {

		model.addAttribute("params", params);

		logger.info("board_title" + params);

		return "reportWrite";
	}

	@RequestMapping(value = "/reportDetail.go")

	public String reportDetailGo(@RequestParam String report_id, Model model) {

		model.addAttribute("report_id", report_id);

		logger.info("report_id" + report_id);

		return "reportDetail";
	}

	@RequestMapping(value = "/reportSend.ajax")
	@ResponseBody
	public HashMap<String, String> reportSend(@RequestParam HashMap<String, String> params, HttpSession session,
			Model model) {

		logger.info("sad" + params);
		HashMap<String, String> data = new HashMap();
		int row = service.report(params);
		if (row == 1) {
			data.put("success", "1");
			model.addAttribute("msg", "신고접수가 완료되었습니다.");
		} else {
			data.put("success", "0");
		}
		return data;
	}

	@RequestMapping(value = "/admin.reportList")
	public String ReportList(Model model) {
		ArrayList<ReportDTO> reportList = service.reportList();
		logger.info("list cnt : " + reportList.size());
		model.addAttribute("reportList", reportList);
		return "reportList";
	}

	@RequestMapping(value = "/reportDetail.ajax")
	@ResponseBody
	public ReportDTO reportDetail(Model model, @RequestParam String report_id) {
		ReportDTO dto = service.reportDetail(report_id);

		model.addAttribute("reportDetail", dto);
		return dto;
	}

	@RequestMapping(value = "/reportStatus.ajax")
	@ResponseBody
	public HashMap<String, String> reportStatus(Model model, @RequestParam String report_id,
			@RequestParam String status, @RequestParam String adminId) {
		logger.info("params:" + report_id + status + adminId);
		HashMap<String, String> data = new HashMap();
		int row = service.reportStatus(report_id, status, adminId);
		if (row == 1) {
			data.put("success", "1");
			model.addAttribute("msg", "상태변경이 완료되었습니다.");
		} else {
			data.put("success", "0");
		}

		return data;

	}

	@RequestMapping(value = "/userActive.go")
	public String userActive(Model model, @RequestParam String user_id, @RequestParam String report_id) {
		logger.info("1:" + user_id);
		logger.info("2:" + report_id);
		ArrayList<ReportDTO> dto = service.history(user_id);

		model.addAttribute("history", dto);
		model.addAttribute("user_id", user_id);
		model.addAttribute("report_id", report_id);
		return "userActive";

	}

	@RequestMapping(value = "/userDisposal.ajax")
	@ResponseBody
	public HashMap<String, String> userDisposal(Model model, @RequestParam HashMap<String, String> param) {
		logger.info("1:" + param);

		if (param.get("disposal").equals("탈퇴")) {
			service.delUser(param.get("user_id"));
		}

		HashMap<String, String> data = new HashMap();
		int row = service.userDisposal(param);
		if (row == 1) {
			data.put("success", "1");
			model.addAttribute("msg", "상태변경이 완료되었습니다.");
		} else {
			data.put("success", "0");
		}

		return data;

	}

}
