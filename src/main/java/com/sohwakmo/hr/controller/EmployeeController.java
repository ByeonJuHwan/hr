package com.sohwakmo.hr.controller;

import com.sohwakmo.hr.domain.Attendance;
import com.sohwakmo.hr.domain.Employee;
import com.sohwakmo.hr.domain.Part;
import com.sohwakmo.hr.dto.EmployeeJoinDto;
import com.sohwakmo.hr.dto.EmployeeUpdateDto;
import com.sohwakmo.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    @GetMapping("/login")
    public String login() {

        return "/employee/login";
    }


    @GetMapping("/join")
    public String join(){

        return "/employee/join";
    }

    @PostMapping("/join")
    public String join(EmployeeJoinDto joinDto, Part part, MultipartFile photo,@DateTimeFormat(pattern= "yyyy-MM-dd")Date joinedDate) throws Exception {
        log.info("joinDto = {}", joinDto);
        log.info("part={}",part.toString());
        log.info("photo={}",photo.getSize());
        employeeService.join(joinDto,part,photo,joinedDate);

        return "redirect:/";
    }

    @GetMapping("/findPw")
    public String findPw(){
        return "/employee/findPw";
    }

    @PostMapping("/getPw")
    public String getEmployeeNo(String employeeNo, Model model,RedirectAttributes attrs){
        String result = employeeService.checkEmployeeNo(employeeNo);
        if (result.equals("yes")) {
            model.addAttribute("employeeNo", employeeNo);
            return "/employee/setPw";
        }else{
            model.addAttribute("result", result);
            return "/employee/findPw";
        }
    }

    @PostMapping("/setPw")
    public String setPw(String password,String employeeNo) {
        employeeService.resetPassword(password,employeeNo);
        return "redirect:/login";
    }

    @GetMapping("/myPage")
    public String myPage(Model model, String employeeNo, @RequestParam(required = false,defaultValue = "")String month){
        Employee employee = employeeService.findEmployee(employeeNo);

        log.info("month={}", month);
        // ????????? ?????? ??????????????? list ??? ?????? ????????? list??? ?????????.
        List<Attendance> list = new ArrayList<>();
        if(month.equals("")){
            list = employeeService.getCurrentMonth(list,employeeNo);
            setAttendanceList(list,model);
        }else{
            list = employeeService.getSearchMonth(month,employeeNo);
            if (list.size() == 0){
                model.addAttribute("searchResult", 0);
                model.addAttribute("monthLength", setMonthLength(month));
            }
            else setAttendanceList(list, model);
        }
        model.addAttribute("employee", employee);
        model.addAttribute("month", month);
        return "/employee/myPage";
    }

    /**
     * ????????? ?????? ?????? ????????? ????????? ?????? ????????? ????????? ???????????? ????????? colspan??? ???????????? ????????? ??????
     * @param month ?????? ?????? ???
     * @return ???????????? ?????? ????????? ??????
     */
    private int setMonthLength(String month) {
        if(month.equals("02")) return 28;
        else if (month.equals("01")||month.equals("03")||month.equals("05")||month.equals("07")||month.equals("08")
            ||month.equals("10")||month.equals("12"))return 31;
        else return 30;
    }

    private void setAttendanceList(List<Attendance> list, Model model) {
        model.addAttribute("startTimeDays", employeeService.setStartTimeDays(list)); // ???????????? ??????
        model.addAttribute("endTimeDays",employeeService.setEndTimeDays(list)); // ?????? ?????? ??????
        model.addAttribute("workState",employeeService.setWorkState(list)); // ?????? ?????? O,X ????????? ??????
    }

    @PostMapping("/myPage/update")
    public String myPageUpdate(EmployeeUpdateDto dto, Principal principal, Part part, MultipartFile photo) throws IOException {
        String employeeNo = principal.getName();
        log.info("employeeNo = {}", employeeNo);
        log.info(dto.toString());
        log.info(part.toString());
        log.info("photo={}", photo.getOriginalFilename());
        employeeService.update(dto, part,employeeNo,photo);
        return "redirect:/myPage?employeeNo=" + employeeNo;
    }
}
