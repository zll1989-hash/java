
@RestController
@RequestMapping("/api/v1/video")
public class ProductController {


    @Autowired
    private ProductService productService;


    /**
     * 新增
     * @param productDO
     * @return
     */
    @PostMapping("add")
    public JsonData add(@RequestBody ProductDO productDO){

        productDO.setCreateTime(new Date());
        int rows = productService.save(productDO);

        return JsonData.buildSuccess(rows);
    }



    @PostMapping("update")
    public JsonData update(@RequestBody ProductDO productDO){

        productService.updateById(productDO);

        return JsonData.buildSuccess(productDO);
    }


    @DeleteMapping("del")
    public JsonData del(int id){


        int rows = productService.delById(id);

        return JsonData.buildSuccess(rows);

    }



    @GetMapping("find")
    public JsonData findById(int id){

        ProductDO productDO  = productService.findById(id);
        return JsonData.buildSuccess(productDO);
    }




    @GetMapping("page")
    public JsonData page(int page,int size){

        Map<String,Object> map =  productService.page(page,size);

        return JsonData.buildSuccess(map);
    }



}
