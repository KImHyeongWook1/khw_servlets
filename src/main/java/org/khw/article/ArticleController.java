package org.khw.article;

import java.util.List;

import javax.servlet.http.HttpSession;

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
public class ArticleController {

	@Autowired
	ArticleDao articleDao;

	Logger logger = LogManager.getLogger();

	/**
	 * 글 목록
	 */
	@GetMapping("/article/list")
	public void articleList(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {

		// 페이지당 행의 수와 페이지의 시작점
		final int COUNT = 100;
		int offset = (page - 1) * COUNT;

		List<Article> articleList = articleDao.listArticles(offset, COUNT);
		int totalCount = articleDao.getArticlesCount();
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("articleList", articleList);
	}

	/**
	 * 글 보기
	 */
	@GetMapping("/article/view")
	public void articleView(@RequestParam("articleId") String articleId, Model model) {
		Article article = articleDao.getArticle(articleId);
		model.addAttribute("article", article);
	}

	/**
	 * 글 등록 화면
	 */
	@GetMapping("/article/addForm")
	public String articleAddForm(HttpSession session) {
		return "article/addForm";
	}

	/**
	 * 글 등록
	 */
	@PostMapping("/article/add")
	public String articleAdd(Article article, @SessionAttribute("MEMBER") Member member) {
		article.setUserId(member.getMemberId());
		article.setName(member.getName());
		articleDao.addArticle(article);
		logger.debug("글을 작성하였습니다. {}", article);
		// return "redirect:/app/article/list";
		return "article/add";
	}

	/**
	 * 글 수정 화면
	 */
	@GetMapping("/article/updateForm")
	public String articleUpdateForm(@RequestParam("articleId") String articleId,
			@SessionAttribute("MEMBER") Member member, Model model) {
		Article article = articleDao.getArticle(articleId);

		// 권한 체크 : 세션의 memberId와 글의 userId를 비교
		if (!article.getUserId().equals(member.getMemberId())) {
			// 자신의 글이 아니면
			logger.debug("{}님의 글이 아니므로 수정할 수 없습니다.", member.getName());
			return "article/updateFail";
			// return "redirect:/app/article/view?articleId=" + articleId;
		}
		model.addAttribute("article", article);
		return "article/updateForm";
	}

	/**
	 * 글 수정
	 */
	@PostMapping("/article/update")
	public String articleUpdate(Article article, @SessionAttribute("MEMBER") Member member) {
		article.setUserId(member.getMemberId());
		int updatedRows = articleDao.updateArticle(article);

		// 권한 체크 : 글이 수정되었는지 확인
		if (updatedRows == 0)
			// 글이 수정되지 않음. 자신이 쓴 글이 아님
			throw new RuntimeException("No Authority!");
		logger.debug("{}번째 글을 수정하였습니다.", article.getArticleId());
		return "redirect:/app/article/view?articleId=" + article.getArticleId();
	}
	
	/**
	 * 글 수정 불가 알림창
	 */
	@PostMapping("/article/updateFail")
	public String updateFail() {
		return "article/updateFail";
	}

	/**
	 * 글 삭제
	 */
	@GetMapping("/article/delete")
	public String articleDelete(@RequestParam("articleId") String articleId,
			@SessionAttribute("MEMBER") Member member) {
		int updatedRows = articleDao.deleteArticle(articleId, member.getMemberId());
		
		// 권한 체크 : 글이 삭제되었는지 확인
		if (updatedRows == 0) {
			// 글이 삭제되지 않음. 자신이 쓴 글이 아님
			logger.debug("{}님의 글이 아니므로 삭제할 수 없습니다.", member.getName());
			return "article/deleteFail";
		}
		logger.debug("{}번째 글을 삭제하였습니다.", articleId);
		return "article/delete";
	}

	/**
	 * 글 삭제 불가 알림창
	 */
	@GetMapping("/article/deleteFail")
	public String deleteFail() {
		return "article/deleteFail";
	}
}