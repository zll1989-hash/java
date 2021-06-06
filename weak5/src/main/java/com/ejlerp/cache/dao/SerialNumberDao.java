package com.ejlerp.cache.dao;


import com.ejlerp.cache.domain.SerialNumber;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * SerialNumberDao
 *
 * @author Eric
 * @date 16/6/3
 */
public interface SerialNumberDao {
    /**
     * 根据id查询规则对象
     *
     * @param tenantId
     * @param id
     * @return
     */
    SerialNumber findOne(Long tenantId, Long id);

    /**
     * 查询全部的规则列表
     *
     * @return
     */
    List<SerialNumber> findAll(Long tenantId);

    /**
     * 保存规则对象
     *
     * @param sn
     */
    void save(Long tenantId, SerialNumber sn);

    /**
     * 删除规则对象,连带删除关联的规则详情列表
     *
     * @param id
     */
    void delete(Long tenantId, Long id);

    /**
     * 查找实体对象的SerialNumber生成规则,tenantId可能为空,为空代表租户无关的实体.
     *
     * @param tenantId
     * @param entityName
     * @return
     */
    @Cacheable(cacheNames = "findCustomizedSerialNumber")
    SerialNumber findCustomizedSerialNumber(Long tenantId, String entityName);

    /**
     * 手动清理缓存
     *
     * @return
     */
    @CacheEvict(cacheNames = "findCustomizedSerialNumber", allEntries = true)
    void cacheEvict();

    /**
     * 查询所有租户id
     *
     * @return
     */
    List<Long> findTenantIds();

    /**
     * 查询所有已拥有某种编码规则的租户信息
     *
     * @param billType 编码规则
     * @return
     */
    List<SerialNumber> findCoded(String billType);

    /**
     * 生成id
     *
     * @param size 生成id的个数
     * @return
     */
    List<Long> batchGenerateId(int size);

    /**
     * 批量插入
     *
     * @param tenantId
     * @param serialNumbers
     */
    Integer batchInsert(Long tenantId, List<SerialNumber> serialNumbers);
}
