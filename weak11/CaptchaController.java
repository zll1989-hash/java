
@RestController
@RequestMapping("/api/v1/captcha")
public class CaptchaController {


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private Producer captchaProducer;



    @GetMapping("get_captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){

        String captchaText = captchaProducer.createText();

        String key = getCaptchaKey(request);

        //10分钟过期
        redisTemplate.opsForValue().set(key,captchaText,10,TimeUnit.MINUTES);

        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);

        ServletOutputStream outputStream = null;

        try {
            outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage,"jpg",outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    /**
     * 发送验证码
     * @return
     */
    @GetMapping("send_code")
    public JsonData sendCode(@RequestParam(value = "to",required = true)String to,
                             @RequestParam(value = "captcha",required = true) String captcha,
                             HttpServletRequest request){

        String key = getCaptchaKey(request);
        String cacheCaptcha = redisTemplate.opsForValue().get(key);

        if(captcha!=null && cacheCaptcha!=null && cacheCaptcha.equalsIgnoreCase(captcha)){
            redisTemplate.delete(key);

            //TODO 发送验证码

            return JsonData.buildSuccess();

        }else {
            return JsonData.buildError("验证码错误");
        }


    }





    private String getCaptchaKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "user-service:captcha:"+CommonUtil.MD5(ip+userAgent);
        return key;
    }


}
