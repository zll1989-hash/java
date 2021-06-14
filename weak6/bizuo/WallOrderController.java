
@RestController
@RequestMapping(value = "/api/wms/rest/wall")
@Api(tags = "未配齐墙")
public class WallOrderController {


    @Autowired
    private WmsOrderDomService wmsOrderDomService;

    @RequestMapping(value = "/sorting/order", method = RequestMethod.GET)
    public void sortingWallList() throws Exception{
        System.out.println("begin");
        wmsOrderDomService.createWmsOrder(null, null);
    }
}
