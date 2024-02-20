package hello.world.myjwt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {


    @RequestMapping("mytest")
    @ResponseBody
    public String mytest() {
        return "hello world!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }
}
