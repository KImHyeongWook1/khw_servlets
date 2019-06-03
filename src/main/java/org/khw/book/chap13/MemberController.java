package org.khw.book.chap13;

import javax.servlet.http.HttpSession;

import org.lgs.book.chap11.Member;
import org.lgs.book.chap11.MemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * p.366 [리스트 13.17] ChangePwdController 수정<br/>
 * ChangePasswordService를 만들지 않고 컨트롤러에서 직접 Dao를 호출함
 */
@Controller
public class MemberController {

	@Autowired
	MemberDao memberDao;
	
	@RequestMapping("/member/memberInfo")
	public String memberInfo(HttpSession session) {
		return "member/memberInfo";
	}

	@RequestMapping("/member/changePwdForm")
	public String changePwdForm(HttpSession session) {
		return "member/changePwdForm";
	}

	@PostMapping("/member/changePwd")
	public String submit(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @SessionAttribute("MEMBER") Member member, Model model) {

		int updatedRows = memberDao.changePassword(member.getMemberId(), currentPassword, newPassword);

		if (updatedRows > 0) {
			// 현재 비밀번호가 맞으면
			return "member/changedPwd";
		} else {
			// 현재 비밀번호가 틀리면
			model.addAttribute("mode", "FAILURE");
			return "member/changePwdForm";
		}
	}
}