package com.ejlerp.cache.service;

import com.ejlerp.cache.api.IDGenerator;
import com.ejlerp.cache.common.Result;
import com.ejlerp.cache.enums.Status;
import com.ejlerp.common.exception.BizRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@DubboService(version = "${service.version:0.1}")
public class IDGeneratorImpl implements IDGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(IDGeneratorImpl.class);
    @Autowired
    private IDGenService idGenService;

    @Override
    public List<Long> generateList(String tagName, int size) {

        LOGGER.info("generate id  table  name is :{}, size is :{}", tagName,size);
        if (StringUtils.isEmpty(tagName)) {
            throw new BizRuntimeException("NO TAG");
        }
        if (size <= 0) {
            throw new BizRuntimeException("SIZE IS ZERO");
        }
        try {
            List<Long> idList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Result result = idGenService.get(tagName);
                if (Status.SUCCESS.equals(result.getStatus())) {
                    idList.add(result.getId());
                }
            }
            return idList;
        } catch (Exception e) {
            LOGGER.debug("生成主键错误" + e.getMessage());
            throw new BizRuntimeException("ID ERROR");
        }

    }


    @Override
    public Long generate(String tagName) {

        LOGGER.info("generate id  table  name is :{}", tagName);
        if (StringUtils.isEmpty(tagName)) {
            throw new BizRuntimeException("NO TAG");
        }
        try {
            Result result = idGenService.get(tagName);
            if (Status.SUCCESS.equals(result.getStatus())) {
                Long id = result.getId();
                return id;
            }
        } catch (Exception e) {
            LOGGER.debug("生成主键错误" + e.getMessage());
            throw new BizRuntimeException("ID ERROR");
        }
        return null;
    }

}
