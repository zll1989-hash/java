

public interface ProductService {

    int save(ProductDO productDO);


    int delById(int id);


    ProductDO updateById(ProductDO productDO);


    ProductDO findById(int id);


    Map<String,Object> page(int page, int size);
}
