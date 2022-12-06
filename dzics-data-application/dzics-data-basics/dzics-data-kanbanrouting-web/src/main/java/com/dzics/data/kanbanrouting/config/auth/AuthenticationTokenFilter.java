package com.dzics.data.kanbanrouting.config.auth;

import com.alibaba.fastjson.JSON;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Administrator
 * @Classname AuthenticationTokenFilter
 * @Description 描述
 * @Date 2022/3/31 16:42
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter  {

    @Value("${spring.profiles.active}")
    private String profile;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if ("prod".equals(profile)) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String reqAddress = request.getHeader("reqAddress");
                if ("c21403fb257a49a4bdcb273c2253765b".equals(reqAddress)) {
                    filterChain.doFilter(request, response);
                } else {
                    log.warn("sub:{}",request.getHeader("sub"));
                    log.warn("reqAddress:{}", reqAddress);
                    responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "签名错误"));
                }
            } catch (Throwable throwable) {
                log.error("AuthenticationTokenFilter error:{}", throwable.getMessage());
            }
        }

    }


    private void responseMsg(HttpServletResponse response, CustomException e) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        Result error = Result.error(e);
        writer.write(JSON.toJSONString(error));
        writer.flush();
        writer.close();
    }
}
