package org.khw.book.chap13;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;	// 구현체

/**
 * p.373 [리스트 13.22] AutoCheckInterceptor 수정
 */
public class AutoCheckInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		HttpSession session = request.getSession();
		Object member = session.getAttribute("MEMBER");
		if (member != null)
			// 세션에 member가 있을 경우 계속 진행
			return true;

		// 세션에 member가 없을 경우 로그인 화면으로
		response.sendRedirect(request.getContextPath() + "/app/loginForm");
		return false;
	}
}

    © 2019 GitHub, Inc.
    Terms
    Privacy
