package hello.world.myjwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {


    @GetMapping("my-test")
    @ResponseBody
    public String mytestg() {
        return "hello world - g!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }
    @PostMapping("my-test")
    @ResponseBody
    public String mytestp() {
        return "hello world - p!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }

    @GetMapping("my-test-get")
    @ResponseBody
    public String mytestget() {
        return "hello world - get!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }
    @PostMapping("my-test-post")
    @ResponseBody
    public String mytestpost() {
        return "hello world - post!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }


    @GetMapping("my-auth-test-get")
    @ResponseBody
    public String myauthtestget() {
        return "hello world - auth get!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }
    @PostMapping("my-auth-test-post")
    @ResponseBody
    public String myauthtestpost() {
        return "hello world - auth post!!!!!!!!!!!!!!!!!!!!!!!!!!!!1";
    }



}
