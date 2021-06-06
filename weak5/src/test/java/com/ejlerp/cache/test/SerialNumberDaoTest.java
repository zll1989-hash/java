package com.ejlerp.cache.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ejlerp.cache.api.IDGenerator;
import com.ejlerp.cache.api.SerialNoGenerator;
import com.ejlerp.cache.dao.SerialNumberDao;
import com.ejlerp.cache.dao.SerialNumberDetailDao;
import com.ejlerp.cache.domain.SerialNumber;
import com.ejlerp.cache.domain.SerialNumberDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * SerialNumberDaoTest
 *
 * @author Eric
 * @date 16/6/3
 */
public class SerialNumberDaoTest extends CacheProviderTests {

    @Autowired
    private SerialNumberDao dao;
    @Autowired
    private SerialNumberDetailDao detailDao;

    @Autowired
    private IDGenerator idGenerator;

    @Autowired
    private SerialNoGenerator serialNoGenerator;

    @Test
    public void testFindCustomizedSerialNumber() {
        System.out.println(dao.findCustomizedSerialNumber(100019L, "sale_order"));
        System.out.println(dao.findCustomizedSerialNumber(100019L, "sale_order"));

        List<SerialNumberDetail> bySNId = detailDao.findBySNId(100131L);
        System.out.println(JSON.toJSONString(bySNId));

        List<SerialNumberDetail> bySNId2 = detailDao.findBySNId(100131L);
        System.out.println(JSON.toJSONString(bySNId2));
    }

    @Test
    public void testFindOne() {
        System.out.println(dao.findOne(200001L, 200001L));
    }

    @Test
    public void testFindAll() {
        System.out.println(dao.findAll(200001L));
    }

    @Test
    public void testUpdateOne() {
        SerialNumber sn = dao.findOne(100013L, 200001L);

        sn.setEntityName(sn.getEntityName() + "_eric");
        dao.save(200001L, sn);
        System.out.println(sn);
    }


    @Test
    public void testAddOne() {
        SerialNumber sn = new SerialNumber();
        sn.setTenantId(999999L);
        sn.setEntityName("entity01");
        sn.setExample("example01");
        sn.setRemark("remark01");
        dao.save(200001L, sn);
    }

    @Test
    public void testDelete() {
        dao.delete(200001L, 200001L);
    }

    @Test
    public void testBatchGenerateSerialNumber() {
        String tableName = "sale_order";
        String extitysStr = "[{\"fixedSalePrice\":true,\"pic\":\"https://img.alicdn.com/bao/uploaded/i2/487372925/TB2AsY7XzUd61BjSZPcXXc6hXXa_!!487372925.jpg\",\"enabled\":true,\"createdAt\":1476363787000,\"lastUpdated\":1476363787000,\"usable\":true,\"donation\":false,\"huge\":false,\"id\":205054,\"sizeType\":4,\"baseUnitId\":0,\"productId\":115885,\"product_no\":\"D-A011-C258-\",\"skuType\":0,\"barCode\":\"D-A011-C258-1052X\",\"colorType\":102,\"procurementCycle\":1,\"tenantId\":100019,\"minPurchaseNum\":1,\"mayByAir\":false},{\"fixedSalePrice\":true,\"pic\":\"https://img.alicdn.com/bao/uploaded/i2/487372925/TB2i9f_XxAb61BjSZFAXXcQfVXa_!!487372925.jpg\",\"enabled\":true,\"createdAt\":1476363787000,\"lastUpdated\":1476363787000,\"usable\":true,\"donation\":false,\"huge\":false,\"id\":205055,\"sizeType\":4,\"baseUnitId\":0,\"productId\":115885,\"product_no\":\"D-A011-C258-\",\"skuType\":0,\"barCode\":\"D-A011-C258-0702X\",\"colorType\":67,\"procurementCycle\":1,\"tenantId\":100019,\"minPurchaseNum\":1,\"mayByAir\":false},{\"fixedSalePrice\":true,\"pic\":\"https://img.alicdn.com/bao/uploaded/i2/487372925/TB2AsY7XzUd61BjSZPcXXc6hXXa_!!487372925.jpg\",\"enabled\":true,\"createdAt\":1476363787000,\"lastUpdated\":1476363787000,\"usable\":true,\"donation\":false,\"huge\":false,\"id\":205056,\"sizeType\":1,\"baseUnitId\":0,\"productId\":115885,\"product_no\":\"D-A011-C258-\",\"skuType\":0,\"barCode\":\"D-A011-C258-105X\",\"colorType\":102,\"procurementCycle\":1,\"tenantId\":100019,\"minPurchaseNum\":1,\"mayByAir\":false}]";
        JSONArray entitys = JSONArray.parseArray(extitysStr);
        List<String> list = serialNoGenerator.batchGenerateSerialNumber(100019L, tableName, entitys);
        System.out.println(list.toString());
    }


    @Test
    public void generate() {
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.generate("leaf-segment-test");
            System.out.println(id);
        }
    }

    @Test
    public void cacheTest() {
        SerialNumber serialNumber = dao.findCustomizedSerialNumber(-1L, "my_testc_table");
        serialNumber = dao.findCustomizedSerialNumber(-1L, "my_testc_table");
        dao.cacheEvict();
        serialNumber = dao.findCustomizedSerialNumber(-1L, "my_testc_table");

        List<SerialNumberDetail> details = detailDao.findBySNId(serialNumber.getId());
        details = detailDao.findBySNId(serialNumber.getId());
        detailDao.cacheEvict();
        details = detailDao.findBySNId(serialNumber.getId());
    }

}
