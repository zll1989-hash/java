
@RestController
@RequestMapping("api/v1/video_order")
public class OrderController {


//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private DiscoveryClient discoveryClient;

    @Autowired
    private VideoService videoService;

    @RequestMapping("find_by_id")
    public Object findById(int videoId){


        Video video = videoService.findById(videoId);

        VideoOrder videoOrder = new VideoOrder();
        videoOrder.setVideoId(video.getId());
        videoOrder.setVideoTitle(video.getTitle());
        videoOrder.setCreateTime(new Date());

        videoOrder.setServerInfo(video.getServeInfo());
        return videoOrder;

    }


    /**
     * 测试 feign 调用 使用post方式传输对象
     * @param video
     * @return
     */
    @RequestMapping("save")
    public Object save(@RequestBody Video video){

       Integer rows =  videoService.save(video);

       Map<String,Object> map  = new HashMap<>();

       map.put("rows",rows);

       return map;
    }


    int temp = 0;

    @RequestMapping("list")
    public Object list(){


        temp++;
        if(temp%3 == 0){
            throw  new RuntimeException();
        }

        Map<String,String> map  = new HashMap<>();

        map.put("title1","spring cloud微服务专题");
        map.put("title2","lucien java");

        return map;
    }






}
