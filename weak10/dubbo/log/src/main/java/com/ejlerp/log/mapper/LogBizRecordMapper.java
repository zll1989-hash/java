package com.ejlerp.log.mapper;

import com.ejlerp.log.domain.LogBizRecordDO;
import com.ejlerp.log.domain.LogBizRecordDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author paul
 */
public interface LogBizRecordMapper {
    /**
     * delete by primary key
     *
     * @param logBizRecordId primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Long logBizRecordId);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(LogBizRecordDO record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(LogBizRecordDO record);

    /**
     * select by primary key
     *
     * @param logBizRecordId primary key
     * @return object by primary key
     */
    LogBizRecordDO selectByPrimaryKey(Long logBizRecordId);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(LogBizRecordDO record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(LogBizRecordDO record);

    /**
     * 批量更新
     *
     * @param list
     * @return 更新条数
     */
    int updateBatch(List<LogBizRecordDO> list);

    /**
     * 批量插入
     *
     * @param list 日志
     * @return 插入条数
     */
    int batchInsert(@Param("list") List<LogBizRecordDO> list);

    /**
     * 根据日志表全字段非空查询
     *
     * @param logBizRecordDO 日志
     * @return 日志集合
     */
    List<LogBizRecordDO> listByAll(LogBizRecordDO logBizRecordDO);

    /**
     * 根据条件查询总数
     *
     * @param query
     * @return Long
     */
    Long countByPage(LogBizRecordDO query);

    /**
     * 分页查询发货单
     *
     * @param query 发货单查询条件
     * @return 发货单集合
     */
    List<LogBizRecordDO> listByPage(LogBizRecordDTO query);

}