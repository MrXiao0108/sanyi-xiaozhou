package com.dzics.data.business.auth.jwt;

import com.alibaba.fastjson.JSON;
import com.dzics.data.common.base.exception.CustomException;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.business.auth.impl.MyUserDetailsServiceImpl;
import com.dzics.data.redis.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@SuppressWarnings("ALL")
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private MyUserDetailsServiceImpl myUserDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

//        String jwtToken = request.getHeader(jwtTokenUtil.getHeader());
//        if (!StringUtils.isEmpty(jwtToken)) {
//            String sub = request.getHeader("sub");
//            if (StringUtils.isEmpty(sub)) {
//                log.warn("sub?????????header???");
//                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "????????????"));
//                return;
//            }
//            String username = null;
//            try {
//                username = jwtTokenUtil.getUserNameFromToken(jwtToken, sub);
//            } catch (ExpiredJwtException e) {
//                log.debug("token????????????????????????token???{}", e.getMessage());
//                responseMsg(response, new CustomException(CustomExceptionType.AUTHEN_TOKEN_IS_ERROR, CustomExceptionType.AUTHEN_TOKEN_IS_ERROR.getTypeDesc()));
//                return;
//            } catch (UnsupportedJwtException e) {
//                log.warn("token????????????:{}", e.getMessage());
//                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token????????????"));
//                return;
//            } catch (MalformedJwtException e) {
//                log.warn("token????????????:{}" , e.getMessage());
//                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token????????????"));
//                return;
//            } catch (SignatureException e) {
//                List<String> list = redisUtil.lGet(RedisKey.LEASE_CAR_TOKEN_HISTORY + sub, 0, -1);
//                if (!list.isEmpty()) {
//                    boolean b = list.stream().anyMatch(token -> token.equals(jwtToken));
//                    if (b) {
//                        username = sub;
//                    } else {
//                        log.warn("token????????????:{}" , e.getMessage());
//                        responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token????????????"));
//                        return;
//                    }
//                } else {
//                    log.warn("token????????????:{}" , e.getMessage());
//                    responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token????????????"));
//                    return;
//                }
//            } catch (IllegalArgumentException e) {
//                log.warn("token????????????:{}" , e.getMessage());
//                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token????????????"));
//                return;
//            } catch (CustomException e) {
//                log.warn("token????????????:{}" , e.getMessage());
//                responseMsg(response, new CustomException(e.getCode(), e.getMessage()));
//                return;
//            } catch (Exception e) {
//                log.warn("????????????", e);
//                responseMsg(response, new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomExceptionType.SYSTEM_ERROR.getTypeDesc()));
//                return;
//            }
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = myUserDetailsServiceImpl.loadUserByUsername(username);
//                if (!userDetails.isAccountNonLocked()) {
//                    log.warn(userDetails.getUsername() + ":" + CustomExceptionType.USER_IS_LOCK.getTypeDesc());
//                    responseMsg(response, new CustomException(CustomExceptionType.USER_IS_LOCK, CustomExceptionType.USER_IS_LOCK.getTypeDesc()));
//                    return;
//                }
//                if (jwtTokenUtil.validateToken(jwtToken, userDetails, sub, username)) {
//                    //????????????JWT???????????????????????????
//                    UsernamePasswordAuthenticationToken authenticationToken
//                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                }
//            } else {
//                responseMsg(response, new CustomException(CustomExceptionType.AUTHEN_TOKEN_IS_ERROR, CustomExceptionType.AUTHEN_TOKEN_IS_ERROR.getTypeDesc()));
//                return;
//            }
//        }
        filterChain.doFilter(request, response);
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
