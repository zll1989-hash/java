package com.ejlerp.cache.dao;


import com.ejlerp.cache.domain.SerialNumberDetail;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * SerialNumberDao
 *
 * @author Eric
 * @date 16/6/3
 */
public interface SerialNumberDetailDao {
    /**
     * 根据id查询规则详情
     *
     * @return
     */
    SerialNumberDetail findOne(long id);

    /**
     * 保存规则详情
     *
     * @param snd
     */
    void save(SerialNumberDetail snd);

    /**
     * 删除规则详情
     *
     * @param id
     */
    void delete(long id);

    /**
     * 删除关联的规则详情
     *
     * @param serialNumberId
     */
    void deleteAll(long serialNumberId);

    /**
     * 根据SerialNumber的id, 以及租户id, 查找detail的列表
     *
     * @param snid
     * @return
     */
    @Cacheable(cacheNames = "findBySNId")
    List<SerialNumberDetail> findBySNId(long snid);

    /**
     * 手动清理缓存
     *
     * @return
     */
    @CacheEvict(cacheNames = "findBySNId", allEntries = true)
    void cacheEvict();

    /**
     * 批量插入明细数据
     *
     * @param tenantId
     * @param serialNumberDetails
     * @return
     */
    Integer batchInsert(Long tenantId, List<SerialNumberDetail> serialNumberDetails);

    /**
     * 生成最小可用数据
     *
     * @param size
     * @return
     */
    List<Long> batchGenerateId(int size);
}
