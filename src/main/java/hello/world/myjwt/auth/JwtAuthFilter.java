package hello.world.myjwt.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static Logger logger = Logger.getLogger(JwtAuthFilter.class.getSimpleName());

    @Value("${jwt.non-target-get}")
    private List<String> allowListGet;
    @Value("${jwt.non-target-post}")
    private List<String> allowListPost;

    @Autowired
    private JwtTokenProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("###################################### REQUEST HEADER #################################");
        logger.info(request.getRequestURI());   //>> "/api/auth/test"
        logger.info(String.valueOf(request.getRequestURL())); //>> "http://localhost/api/auth/test "
        logger.info(request.getMethod()); //>> "GET" / "POST" / "UPDATE" / .....
        logger.info(request.getHeader("Authorization"));  //>> header 에 어떤 key 로 들어 있는 value 받아오기
        Enumeration<String> headerNames = request.getHeaderNames();  //>> header 에 들어 있는 key 들 꺼내기
        String token = null;    // 토큰 저장 변수
        while (headerNames.hasMoreElements()) {
            // header 중에 토큰 찾기
            String key = headerNames.nextElement();
            String val = request.getHeader(key);
            logger.info(key);
            logger.info(val);
            if (val.startsWith("Bearer ")) {
                token = val;
            }
        }
        logger.info("###################################### REQUEST HEADER #################################");

        List<String> targetList = null;     // 로그인 안 해도 되는 페이지 주소 리스트
        String uri = request.getRequestURI();   // 클라이언트가 요청한 주소
        String method = request.getMethod();    // 클라이언트가 요청한 method

        if ("GET".equals(method)) {
            // get 요청일 경우
            targetList = this.allowListGet;
        } else if ("POST".equals(method)) {
            // post 요청일 경우
            targetList = this.allowListPost;
        }
        if (!(targetList != null && targetList.contains(uri))) {
            // 로그인 해야 하는 페이지인 경우 - 요청한 header 로부터 로큰 꺼내서 로그인 여부 확인
            if (!(token != null && token.startsWith("Bearer ") && jwtProvider.isValidToken(token.substring(7)))) {
                // 유효한 토큰이 아닌 경우
                logger.info("####################################### auth check ###################################");
                logger.info(token);
                response.sendError(403, "로그인 후 이용해주세요");
                return;
            }
        }

        // 로그인 안 해도 접근할 수 있는 페이지일 경우 - 처리를 그대로 controller 에게 위임
        filterChain.doFilter(request, response);
    }

}