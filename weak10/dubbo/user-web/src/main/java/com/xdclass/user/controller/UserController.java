
@RestController
public class UserController {


    @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
    private UserService userService;

    @RequestMapping("/sayHello")
    public String sayHello() {
        return userService.sayHello();
    }
}
