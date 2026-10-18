package com.seohamin.moonbangoo.domain.admin.interceptor;

import com.seohamin.moonbangoo.global.exception.CustomException;
import com.seohamin.moonbangoo.global.exception.constants.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 어드민 API(/api/v1/admin/**)를 로그인한 사람만 쓸 수 있게 막음
 * 로그인하면 세션에 표시가 남고, 서버를 껐다 켜거나 세션이 만료되면 다시 로그인해야 함
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_KEY = "adminLoggedIn";

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        //CORS 사전 요청(OPTIONS)에는 로그인 정보가 실리지 않으므로 통과시킴
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        final HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            throw new CustomException(ExceptionCode.ADMIN_UNAUTHORIZED);
        }

        return true;
    }
}
