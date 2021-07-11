

@RequestMapping("/api/v1/card")
@RestController
public class VideoCardController {


    @Autowired
    private VideoCardService videoCardService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 缓存key
     */
    private static final String VIDEO_CARD_CACHE_KEY = "video:card:key";

    /**
     * 有缓存
     * @return
     */
    @GetMapping("list_cache")
    public JsonData listCardCache(){


        Object cacheObj = redisTemplate.opsForValue().get(VIDEO_CARD_CACHE_KEY);

        if(cacheObj != null){

            List<VideoCardDO> list = (List<VideoCardDO>) cacheObj;
            return JsonData.buildSuccess(list);

        } else {

            List<VideoCardDO> list = videoCardService.list();

            redisTemplate.opsForValue().set(VIDEO_CARD_CACHE_KEY,list,10,TimeUnit.MINUTES);

            return JsonData.buildSuccess(list);
        }

    }


    /**
     * 无缓存
     * @return
     */
    @GetMapping("list_nocache")
    public JsonData listCardNoCache(){


        List<VideoCardDO> list = videoCardService.list();

        return JsonData.buildSuccess(list);

    }


}
