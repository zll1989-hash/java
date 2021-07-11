package com.ejlerp.log.controller;

import com.ejlerp.common.dal.PagedList;
import com.ejlerp.common.util.ObjectUtils;
import com.ejlerp.common.vo.InvokeResult;
import com.ejlerp.log.api.BizLogger;
import com.ejlerp.log.api.LogBizService;
import com.ejlerp.log.domain.LogBizQueryVO;
import com.ejlerp.log.domain.LogBizRecordDTO;
import com.ejlerp.log.domain.LogBizRecordVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paul
 */
@RestController
@RequestMapping("/api/log")
public class LogBizRecordController {
    /**
     * 日志服务组建
     */
    @Autowired
    private LogBizService logBizService;
    @Autowired
    private BizLogger bizLogger;
    /**
     * 日志组件
     */
    private static final Logger logger = LoggerFactory.getLogger(LogBizRecordController.class);

    /**
     * 查询业务模块日志
     *
     * @param queryVO 查询参数
     * @return 日志
     */
    @PostMapping(value = "/list")
    public InvokeResult<List<LogBizRecordVO>> list(@RequestBody LogBizQueryVO queryVO) {
        logger.info("### 发货单日志查询开始,{}", queryVO);
        InvokeResult<List<LogBizRecordVO>> invokeResult = new InvokeResult<>();
        try {
            List<LogBizRecordDTO> logs = logBizService.listByBizModule(queryVO);
            invokeResult.setData(ObjectUtils.convertList(logs, LogBizRecordVO.class));
            invokeResult.setStatus(InvokeResult.SUCCESSFUL);
        } catch (Exception e) {
            invokeResult.setData(new ArrayList<>());
            invokeResult.setStatus(InvokeResult.FAILED);
            invokeResult.setInfo(e.getMessage());
        }
        return invokeResult;
    }

    /**
     * 查询业务模块日志
     *
     * @param queryVO 查询参数
     * @return 日志
     */
    @PostMapping(value = "/listPage")
    public InvokeResult<PagedList<LogBizRecordVO>> listPage(@RequestBody LogBizQueryVO queryVO) {
        logger.info("### 发货单日志查询开始,{}", queryVO);
        InvokeResult<PagedList<LogBizRecordVO>> invokeResult = new InvokeResult<>();
        try {
            PagedList<LogBizRecordVO> logs = logBizService.listByBizModuleByPage(queryVO);
            invokeResult.setData(logs);
            invokeResult.setStatus(InvokeResult.SUCCESSFUL);
        } catch (Exception e) {
            invokeResult.setData(null);
            invokeResult.setStatus(InvokeResult.FAILED);
            invokeResult.setInfo(e.getMessage());
        }
        return invokeResult;
    }
}
