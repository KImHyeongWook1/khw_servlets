package letter;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lgs.book.chap11.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class LetterController {

	@Autowired
	LetterDao letterDao;

	Logger logger = LogManager.getLogger();

	/**
	 * 보낸 목록
	 */
	@GetMapping("/letter/sendList")
	public void sendList(@SessionAttribute("MEMBER") Member member,
			@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		final int COUNT = 100;
		int offset = (page - 1) * COUNT;
		int totalCount = letterDao.getSendLettersCount(member.getMemberId());
		model.addAttribute("totalCount", totalCount);
		
		List<Letter> letterList = letterDao.sendListLetters(member.getMemberId(), offset, COUNT);
		// List<Letter> letterList = letterDao.sendListLetters(member.getMemberId());
		model.addAttribute("letterList", letterList);
	}

	/**
	 * 받은 목록
	 */
	@GetMapping("/letter/receiveList")
	public void receiveList(@SessionAttribute("MEMBER") Member member,
			@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		final int COUNT = 100;
		int offset = (page - 1) * COUNT;
		int totalCount = letterDao.getReceiveLettersCount(member.getMemberId());
		model.addAttribute("totalCount", totalCount);

		List<Letter> letterList = letterDao.receiveListLetters(member.getMemberId(), offset, COUNT);
		model.addAttribute("letterList", letterList);
	}
	
	/**
	 * 메일 보기
	 */
	@GetMapping("/letter/view")
	public void letterView(@RequestParam("letterId") String letterId,
			@SessionAttribute("MEMBER") Member member, Model model) {
		Letter letter = letterDao.getLetter(letterId, member.getMemberId());
		model.addAttribute("letter", letter);
	}
	
	/**
	 * 메일 작성 화면
	 */
	@GetMapping("/letter/addForm")
	public String letterAddForm() {
		return "letter/addForm";
	}

	/**
	 * 메일 저장
	 */
	@PostMapping("/letter/add")
	public String letterAdd(Letter letter, @SessionAttribute("MEMBER") Member member) {
		letter.setSenderId(member.getMemberId());
		letter.setSenderName(member.getName());
		letterDao.addLetter(letter);
		logger.debug("메일을 작성하였습니다. {}", letter);
		//return "redirect:/app/letter/sendList";
		return "letter/add";
	}
	
	/**
	 * 메일 삭제
	 */
	@GetMapping("/letter/delete")
	public String letterDelete(@RequestParam("letterId") String letterId,
			@SessionAttribute("MEMBER") Member member) {
		int updatedRows = letterDao.deleteLetter(letterId, member.getMemberId());
		if (updatedRows == 0) {
			logger.debug("{}님의 메일이 아니므로 삭제할 수 없습니다.", member.getName());
			return "letter/deleteFail";
		}
		logger.debug("{}번째 메일을 삭제하였습니다.", letterId);
		return "letter/delete";
	}

	/**
	 * 메일 삭제 실패
	 */
	@GetMapping("/letter/deleteFail")
	public String deleteFail() {
		return "letter/deleteFail";
	}
}