package com.ejlerp.log.api;


import com.ejlerp.common.dal.PagedList;
import com.ejlerp.common.vo.CallerInfo;
import com.ejlerp.log.domain.LogBizQueryVO;
import com.ejlerp.log.domain.LogBizRecordDTO;
import com.ejlerp.log.domain.LogBizRecordVO;
import com.ejlerp.log.message.BatchBizLoggerMessageVO;
import com.ejlerp.log.message.BizLoggerMessageVO;

import java.util.List;


/**
 * @author paul
 * 日志服务组建
 */
public interface LogBizService {
    /**
     * 新增业务日志
     * @param bizLoggerMessageVO 日志消息体
     * @see com.ejlerp.log.enums.LogBizModuleTypeEnum
     * @see com.ejlerp.log.enums.LogBizOperationTypeEnum
     * @return 新增结果条数
     */
    int log(BizLoggerMessageVO bizLoggerMessageVO);

    /**
     * 批量新增业务日志
     * @param bizLoggerMessageVO 日志消息提
     * @see com.ejlerp.log.enums.LogBizModuleTypeEnum
     * @see com.ejlerp.log.enums.LogBizOperationTypeEnum
     * @return 新增结果条数
     */
    int batchLog(BatchBizLoggerMessageVO bizLoggerMessageVO);

    /**
     * 根据租户ID 模块模型 表ID查询日志
     * @param queryVO        模块类型
     * @see com.ejlerp.log.enums.LogBizModuleTypeEnum
     * @return 日志
     */
    List<LogBizRecordDTO> listByBizModule(LogBizQueryVO queryVO);

    /**
     * 根据用户传递过来的信息 进行分页查找数据
     *
     */
    PagedList<LogBizRecordVO> listByBizModuleByPage(LogBizQueryVO queryVO);

}
