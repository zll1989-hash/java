package com.ejlerp.cache.mapper;

import com.ejlerp.cache.model.LeafAlloc;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Author lucien
 * @create 2021/5/25 13:42
 */
public interface LeafAllocMapper {
    int deleteByPrimaryKey(String bizTag);

    int insert(LeafAlloc record);

    int insertOrUpdate(LeafAlloc record);

    int insertOrUpdateSelective(LeafAlloc record);

    int insertSelective(LeafAlloc record);

    LeafAlloc selectByPrimaryKey(String bizTag);

    int updateByPrimaryKeySelective(LeafAlloc record);

    int updateByPrimaryKey(LeafAlloc record);

    int updateBatch(List<LeafAlloc> list);

    int updateBatchSelective(List<LeafAlloc> list);

    int batchInsert(@Param("list") List<LeafAlloc> list);

    List<String> getAllTags();

    void updateMaxIdByCustomStep(LeafAlloc record);

    void updateMaxId(String bizTag);


}