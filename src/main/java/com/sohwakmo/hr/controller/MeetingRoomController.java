package com.sohwakmo.hr.controller;

import com.sohwakmo.hr.domain.MeetingRoom;
import com.sohwakmo.hr.dto.MeetingRoomCreateDto;
import com.sohwakmo.hr.dto.MeetingRoomUpdateDto;
import com.sohwakmo.hr.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/meetingRoom")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;



    @GetMapping("/list")
    public String list(String employeeNo, Model model) {
        log.info("list(employeeNo={})", employeeNo);

        List<MeetingRoom> meetingRoom =  meetingRoomService.read(employeeNo);

        model.addAttribute("meetingRoom", meetingRoom);
        log.info("model={}", model);

        return "/meetingRoom/list";
    }



    @GetMapping("/readByDate")
    public void list(Model model, String dateValue){
        log.info("readByDate(date={})",dateValue);

    }

    @GetMapping("/create")
    public String create() {
        log.info("create()");

        return "/meetingRoom/create";
    }

    @PostMapping("/create")
    public String create(MeetingRoomCreateDto dto, RedirectAttributes attrs)  {
        log.info("create(dto={})", dto);
        String no = "1";
        MeetingRoom entity = meetingRoomService.create(dto);

        attrs.addFlashAttribute("createdNo", entity.getMeetingRoomNo());

        return "redirect:/meetingRoom/list?employeeNo=" + dto.getEmployeeNo();
    }

    // 예약 상세
    @GetMapping("/detail")
    public String detail(Integer meetingRoomNo, Model model) {
        log.info("detail(meetingRoomNo={})", meetingRoomNo);

        MeetingRoom meetingRoom = meetingRoomService.read(meetingRoomNo);

        model.addAttribute("meetingRoom", meetingRoom);
        model.addAttribute("reservationNo", meetingRoom.getEmployeeNo());
        log.info("model={}", model);

        return "/meetingRoom/detail";
    }

    @GetMapping("/modify")
    public String modify(Integer meetingRoomNo, Model model) {
        log.info("modify(meetingRoomNo={})", meetingRoomNo);

        MeetingRoom meetingRoom = meetingRoomService.read(meetingRoomNo);
        model.addAttribute("meetingRoom", meetingRoom);

        return "/meetingRoom/modify";
    }

    // 예약 수정
    @PostMapping("/update")
    public String update(MeetingRoomUpdateDto dto) {
        log.info("update(dto={})", dto);

        Integer meetingRoomNo = meetingRoomService.update(dto);


        return "redirect:/meetingRoom/detail?meetingRoomNo=" + dto.getMeetingRoomNo();
    }


    @PostMapping("/delete")
    public String delete(Integer meetingRoomNo) {
        log.info("delete(meetingRoomNo={})", meetingRoomNo);

        meetingRoomService.delete(meetingRoomNo);

        return "redirect:/meetingRoom/list";

    }


}

