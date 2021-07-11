

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;


    @Override
    public int save(ProductDO productDO) {
        return productMapper.insert(productDO);
    }

    @Override
    @CacheEvict(value = {"product"},key = "#root.args[0]")
    public int delById(int id) {
        return productMapper.deleteById(id);
    }

    @Override
    @CachePut(value = {"product"},key="#productDO.id", cacheManager = "cacheManager1Minute")
    public ProductDO updateById(ProductDO productDO) {
        productMapper.updateById(productDO);
        return productDO;
    }

    @Override
    //@Cacheable(value = {"product"},key = "#root.args[0]",cacheManager = "cacheManager1Minute")
    @Cacheable(value = {"product"}, keyGenerator = "springCacheCustomKeyGenerator",cacheManager = "cacheManager1Minute",sync = true)

//    @Caching(
//            cacheable = {
//                    @Cacheable(value = {"product"},key = "#root.args[0]"),
//                    @Cacheable(value = {"product"},key = "'xdclass_'+#root.args[0]")
//            },
//            put = {
//                    @CachePut(value = {"product_test"},key="#id", cacheManager = "cacheManager1Minute")
//            }
//    )
    public ProductDO findById(int id) {


        return productMapper.selectById(id);
    }

    @Override
    //@Cacheable(value = {"product_page"},key = "#root.methodName+'_'+#page+'_'+#size")
    @Cacheable(value = {"product_page"},keyGenerator = "springCacheCustomKeyGenerator")
    public Map<String, Object> page(int page, int size) {

        Page pageInfo = new Page<>(page,size);

        IPage<ProductDO> iPage = productMapper.selectPage(pageInfo,null);

        Map<String,Object> pageMap = new HashMap<>(3);

        pageMap.put("total_record",iPage.getTotal());
        pageMap.put("total_page",iPage.getPages());
        pageMap.put("current_data",iPage.getRecords());

        return pageMap;
    }

}
