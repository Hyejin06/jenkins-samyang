package com.app.samyang.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.samyang.entity.HjMember;
import com.app.samyang.service.HjMemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@CrossOrigin(origins = "*")   //CORS 허용
public class HjMemberController {

    private final HjMemberService hjMemberService;
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

   @GetMapping("/hjjoin")
    public String joinForm(Model model) {
      model.addAttribute("member", new HjMember());
        return "member/hjjoin";
    }

    @PostMapping("/hjjoin")
    public String join(@ModelAttribute HjMember hjMember) {
        hjMemberService.join(hjMember);
        return "redirect:/member/hjlogin";
    }

    @GetMapping("/hjlogin")
    public String loginForm() {
        return "member/hjlogin";
    }
    //Java의 객체
    //현재 페이지 내에서만 인지 가능한 객체:page(Page)
    //로그인하고 있는 동안에만 저장하고 객체:session(HttpSession)
    //요청한 곳으로만 보내는 객체: request(HttpServletRequest)
    //보내지는 곳에서만 알 수 있는 객체: response(HttpServletResponse)
    @PostMapping("/hjlogin")
    public String login(@RequestParam("id") String id, @RequestParam("pw") String pw, HttpSession session) {
    	HjMember hjMember = hjMemberService.login(id, pw);
        if (hjMember != null) {
            session.setAttribute("hjloginMember", hjMember);   //로그인 처리
            return "redirect:/hj";
        } else {
            return "redirect:/member/hjlogin";
        }
    }

    @GetMapping("/hjlogout")
    public String logout(HttpSession session) {
       session.invalidate();
       return "redirect:/hj";
    }
    
    @GetMapping("/hjmypage")
    public String myPage(HttpSession session, Model model) {
        HjMember hjMember = (HjMember) session.getAttribute("hjloginMember");
        if (hjMember == null) {
            return "redirect:/member/hjlogin"; // 로그인 안 되어 있으면 로그인 페이지로
        }
        model.addAttribute("member", hjMember);
        return "member/hjmy-page";
    }

    @GetMapping("/hjedit")
    public String editForm(HttpSession session, Model model) {
    	HjMember hjMember = (HjMember) session.getAttribute("hjloginMember");
        model.addAttribute("member", hjMember);
        return "member/hjmember-edit";
    }

    @PostMapping("/hjedit")
    public String edit(@ModelAttribute HjMember editedMember, HttpSession session) {
    	HjMember hjloginMember = (HjMember) session.getAttribute("hjloginMember");

        editedMember.setNo(hjloginMember.getNo());
        editedMember.setId(hjloginMember.getId());
        editedMember.setName(hjloginMember.getName());

        // ⭐ 비밀번호 변경 시 암호화 적용
        if (editedMember.getPw() == null || editedMember.getPw().isBlank()) {
            editedMember.setPw(hjloginMember.getPw());
        } else {
            editedMember.setPw(bCryptPasswordEncoder.encode(editedMember.getPw()));
        }

        editedMember.setGrade(hjloginMember.getGrade());
        editedMember.setAmountToVip(hjloginMember.getAmountToVip());
        editedMember.setGradeProgress(hjloginMember.getGradeProgress());

        hjMemberService.update(editedMember);
        session.setAttribute("hjloginMember", editedMember);

        return "redirect:/member/hjmypage";
    }

    @GetMapping("/hjdelete")
    public String delete(HttpSession session) {
        HjMember hjMember = (HjMember) session.getAttribute("hjloginMember");
        hjMemberService.delete(hjMember.getNo());
        session.invalidate();
        return "redirect:/hj";
    }

    @GetMapping("/hjlist")
    public String list(Model model) {
        model.addAttribute("members", hjMemberService.getMemberList());
        return "member/member-list";
    }
    
    @GetMapping("/hjcheckId")
    @ResponseBody
    public boolean checkDuplicateId(@RequestParam("id") String id) {
        return hjMemberService.idCheck(id);  // true이면 이미 존재함
    }
  @GetMapping("/hjcheckEmail")
    @ResponseBody
    public boolean checkDuplicateEmail(@RequestParam("email") String email) {
        return hjMemberService.emailCheck(email);  // true = 이미 존재
    }
    
 // ✅ 회원가입 약관 동의 페이지
    @GetMapping("/hjform")
    public String joinFormPage(Model model) {
        model.addAttribute("member", new HjMember());
        return "member/hjform";
    }
}
