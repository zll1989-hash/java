package com.ejlerp.log.api;

import com.alibaba.druid.util.StringUtils;
import com.ejlerp.cache.api.IDGenerator;
import com.ejlerp.common.dal.PagedList;
import com.ejlerp.common.util.BeanCopierUtils;
import com.ejlerp.common.util.ObjectUtils;
import com.ejlerp.log.common.Constants;
import com.ejlerp.log.domain.LogBizQueryVO;
import com.ejlerp.log.domain.LogBizRecordDO;
import com.ejlerp.log.domain.LogBizRecordDTO;
import com.ejlerp.log.domain.LogBizRecordVO;
import com.ejlerp.log.enums.LogBizModuleTypeEnum;
import com.ejlerp.log.enums.SysConstantEnum;
import com.ejlerp.log.mapper.LogBizRecordMapper;
import com.ejlerp.log.message.BatchBizLoggerMessageVO;
import com.ejlerp.log.message.BizLoggerMessageVO;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author paul
 */
@Service
public class LogBizServiceImpl implements LogBizService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(LogBizServiceImpl.class);
    /**
     * 日志记录管理mapper组件
     */
    @Autowired
    private LogBizRecordMapper logBizRecordMapper;

    @Autowired
    private IDGenerator idGenerator;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int log(BizLoggerMessageVO messageVO) {
        logger.info("### 记录日志开始，消息体：{}", messageVO);
        try {
            LogBizRecordDTO logBizRecordDTO = new LogBizRecordDTO();
            BeanCopierUtils.copyProperties(messageVO, logBizRecordDTO);
            LogBizRecordDTO.propertyAfterSet(logBizRecordDTO, messageVO.getModuleTypeEnum(), messageVO.getOperationTypeEnum());
            /**
             * 获取主键ID值
             */
            Long id = idGenerator.generate("log_biz_record");
            logger.info("获取日志新增主键id：{}", id);
            logBizRecordDTO.setLogBizRecordId(id);
            int a = logBizRecordMapper.insert(logBizRecordDTO.clone(LogBizRecordDO.class));
            logger.info("获取日志新增记录logBizRecordDTO：{},输出插入数据成功与否a:{}", logBizRecordDTO, a);
        } catch (Exception e) {
            logger.error("### 日志 ERROR :", e);
        }
        return SysConstantEnum.ZERO_ONE.getCode();
    }

    /**
     * @param messageVO 日志消息提
     * @return 新增条件
     */
    @Override
    public int batchLog(BatchBizLoggerMessageVO messageVO) {
        logger.info("### 记录日志开始：{}", messageVO);
        List<LogBizRecordDTO> list = new LinkedList<>();
        try {
            for (Long entityId : messageVO.getEntityIds()) {
                LogBizRecordDTO logBizRecordDTO = new LogBizRecordDTO();
                BeanCopierUtils.copyProperties(messageVO, logBizRecordDTO);
                LogBizRecordDTO.propertyAfterSet(logBizRecordDTO, messageVO.getModuleTypeEnum(), messageVO.getOperationTypeEnum());
                logBizRecordDTO.setEntityId(entityId);
                /**
                 * 获取主键ID值
                 */
                Long id = idGenerator.generate("log_biz_record");
                logger.info("获取日志新增主键id：{}", id);
                logBizRecordDTO.setLogBizRecordId(id);
                list.add(logBizRecordDTO);
            }
            if (CollectionUtils.isEmpty(list)) {
                return SysConstantEnum.ZERO_INT.getCode();
            }
            logBizRecordMapper.batchInsert(ObjectUtils.convertList(list, LogBizRecordDO.class));
        } catch (Exception e) {
            logger.error("### 日志 ERROR : ", e);
        }
        logger.info("### 日志记录结束");
        return CollectionUtils.isEmpty(list) ? SysConstantEnum.ZERO_INT.getCode() : list.size();
    }

    /**
     * 根据租户ID 模块模型 表ID查询日志
     *
     * @param queryVO 租户Id
     * @return 日志
     */
    @Override
    public List<LogBizRecordDTO> listByBizModule(LogBizQueryVO queryVO) {
        logger.info("### 查询日志：{}", queryVO);
        try {
            if (!LogBizModuleTypeEnum.contain(queryVO.getLogBizModuleType())) {
                logger.info("### 查询日志 模块类型错误：{}", queryVO.getLogBizModuleType());
                return new ArrayList<>();
            }
            LogBizRecordDTO selectParam = new LogBizRecordDTO();
            selectParam.setEntityId(queryVO.getEntityId());
            selectParam.setModuleType(queryVO.getLogBizModuleType());
            return ObjectUtils.convertList(
                    logBizRecordMapper.listByAll(selectParam.clone(LogBizRecordDO.class)), LogBizRecordDTO.class);
        } catch (Exception e) {
            logger.info("### 查询日志 ERROR：", e);
            return Lists.newArrayList();
        }
    }

    @Override
    public PagedList<LogBizRecordVO> listByBizModuleByPage(LogBizQueryVO queryVO) {
        try {

            LogBizRecordDTO selectParam = new LogBizRecordDTO();
            selectParam.setEntityId(queryVO.getEntityId());
            selectParam.setModuleType(queryVO.getLogBizModuleType());
            if (!StringUtils.isEmpty(queryVO.getSort())) {
                selectParam.setTenantId(Long.parseLong(queryVO.getSort()));
            }
            selectParam.setPage(queryVO.getPage());
            selectParam.setPageSize(queryVO.getPageSize());

            Long totalCounts = logBizRecordMapper.countByPage(selectParam.clone(LogBizRecordDO.class));

            if (null == totalCounts || totalCounts.equals(Constants.ZERO.longValue())) {
                return new PagedList<>(selectParam.getPage(), selectParam.getPageSize(), new ArrayList<>());
            }
            int pageSize = selectParam.getPageSize();
            long offset = (long) (selectParam.getPage() - 1) * pageSize;
            selectParam.setPage((int) offset);

            List<LogBizRecordVO> recordList = ObjectUtils.convertList(logBizRecordMapper.listByPage(selectParam), LogBizRecordVO.class);

            PagedList<LogBizRecordVO> page = new PagedList<>(selectParam.getPage(), selectParam.getPageSize(), recordList);

            page.setTotalCount(totalCounts);
            page.setTotalPageCount(totalCounts / pageSize == 0 ? totalCounts / pageSize : totalCounts / selectParam.getPageSize() + 1);
            page.setPage(selectParam.getPage());
            return page;
        } catch (Exception e) {
            logger.error("list by page error", e);
            return new PagedList<>();
        }
    }


}
