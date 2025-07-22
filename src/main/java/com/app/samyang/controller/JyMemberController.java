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

import com.app.samyang.entity.JyMember;
import com.app.samyang.service.JyMemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@CrossOrigin(origins = "*")   //CORS 허용
public class JyMemberController {

    private final JyMemberService jyMemberService;
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

   @GetMapping("/jyjoin")
    public String joinForm(Model model) {
      model.addAttribute("member", new JyMember());
        return "member/jyjoin";
    }

    @PostMapping("/jyjoin")
    public String join(@ModelAttribute JyMember jyMember) {
        jyMemberService.join(jyMember);
        return "redirect:/member/jylogin";
    }

    @GetMapping("/jylogin")
    public String loginForm() {
        return "member/jylogin";
    }
    //Java의 객체
    //현재 페이지 내에서만 인지 가능한 객체:page(Page)
    //로그인하고 있는 동안에만 저장하고 객체:session(HttpSession)
    //요청한 곳으로만 보내는 객체: request(HttpServletRequest)
    //보내지는 곳에서만 알 수 있는 객체: response(HttpServletResponse)
    @PostMapping("/jylogin")
    public String login(@RequestParam("id") String id, @RequestParam("pw") String pw, HttpSession session) {
        JyMember jyMember = jyMemberService.login(id, pw);
        if (jyMember != null) {
            session.setAttribute("jyloginMember", jyMember);   //로그인 처리
            return "redirect:/jy";
        } else {
            return "redirect:/member/jylogin";
        }
    }

    @GetMapping("/jylogout")
    public String logout(HttpSession session) {
       session.invalidate();
       return "redirect:/jy";
    }
    
    @GetMapping("/jymypage")
    public String myPage(HttpSession session, Model model) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        if (jyMember == null) {
            return "redirect:/member/jylogin"; // 로그인 안 되어 있으면 로그인 페이지로
        }
        model.addAttribute("member", jyMember);
        return "member/jymy-page";
    }

    @GetMapping("/jyedit")
    public String editForm(HttpSession session, Model model) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        model.addAttribute("member", jyMember);
        return "member/jymember-edit";
    }

    @PostMapping("/jyedit")
    public String edit(@ModelAttribute JyMember editedMember, HttpSession session) {
        JyMember jyloginMember = (JyMember) session.getAttribute("jyloginMember");

        editedMember.setNo(jyloginMember.getNo());
        editedMember.setId(jyloginMember.getId());
        editedMember.setName(jyloginMember.getName());

        // ⭐ 비밀번호 변경 시 암호화 적용
        if (editedMember.getPw() == null || editedMember.getPw().isBlank()) {
            editedMember.setPw(jyloginMember.getPw());
        } else {
            editedMember.setPw(bCryptPasswordEncoder.encode(editedMember.getPw()));
        }

        editedMember.setGrade(jyloginMember.getGrade());
        editedMember.setAmountToVip(jyloginMember.getAmountToVip());
        editedMember.setGradeProgress(jyloginMember.getGradeProgress());

        jyMemberService.update(editedMember);
        session.setAttribute("jyloginMember", editedMember);

        return "redirect:/member/jymypage";
    }

    @GetMapping("/jydelete")
    public String delete(HttpSession session) {
        JyMember jyMember = (JyMember) session.getAttribute("jyloginMember");
        jyMemberService.delete(jyMember.getNo());
        session.invalidate();
        return "redirect:/jy";
    }

    @GetMapping("/jylist")
    public String list(Model model) {
        model.addAttribute("members", jyMemberService.getMemberList());
        return "member/member-list";
    }
    
    @GetMapping("/jycheckId")
    @ResponseBody
    public boolean checkDuplicateId(@RequestParam("id") String id) {
        return jyMemberService.idCheck(id);  // true이면 이미 존재함
    }
  @GetMapping("/jycheckEmail")
    @ResponseBody
    public boolean checkDuplicateEmail(@RequestParam("email") String email) {
        return jyMemberService.emailCheck(email);  // true = 이미 존재
    }
    
 // ✅ 회원가입 약관 동의 페이지
    @GetMapping("/jyform")
    public String joinFormPage(Model model) {
        model.addAttribute("member", new JyMember());
        return "member/jyform";
    }
}
