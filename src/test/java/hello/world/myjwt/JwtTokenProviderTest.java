package hello.world.myjwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class JwtTokenProviderTest {



    private static JwtTokenProvider jwtProvider;

    @BeforeAll
    public static void setup() {
        //
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("context = " + context);
//        // 등록된 빈 전체 조회
//        List<String> beanNames = Arrays.stream(context.getBeanDefinitionNames()).toList();
//        Iterator<String> iter = beanNames.iterator();
//        while(iter.hasNext()) {
//            System.out.println("beanNames = " + iter.next());
//        }
        //
        JwtTokenProviderTest.jwtProvider = context.getBean("jwtTokenProvider", JwtTokenProvider.class);
        System.out.println("jwtProvider = " + jwtProvider);
    }



    @Test
    public void createTest() {
        String sub = "hello";
        String result = jwtProvider.createToken(sub);
        Assertions.assertEquals(sub, JwtTokenProviderTest.jwtProvider.extractSubject(result));
    }
}
