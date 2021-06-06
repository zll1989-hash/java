package com.ejlerp.cache.service;

import com.ejlerp.cache.api.SerialNumberService;
import com.ejlerp.cache.dao.SerialNumberDao;
import com.ejlerp.cache.dao.SerialNumberDetailDao;
import com.ejlerp.cache.domain.SerialNumber;
import com.ejlerp.cache.domain.SerialNumberDetail;
import com.ejlerp.cache.vo.SerialNumberDetailVo;
import com.ejlerp.cache.vo.SerialNumberVo;
import com.ejlerp.common.util.BeanCopierUtils;
import com.ejlerp.common.util.BeanUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Water
 * @date 2020/09/10
 */
@DubboService(version = "${service.version:0.1}")
public class SerialNumberServiceImpl implements SerialNumberService {

    private static Logger LOGGER = LoggerFactory.getLogger(SerialNumberServiceImpl.class);
    @Autowired
    private SerialNumberDao serialNumberDao;
    @Autowired
    private SerialNumberDetailDao serialNumberDetailDao;

    @Override
    @Transactional
    public String addDefaultRule(String billType) {
        LOGGER.info("根据bill_type更新所有租户编码规则：billType:[{}]", billType);
        if (Strings.isEmpty(billType)) {
            return null;
        }
        SerialNumber sysSerialNumber = serialNumberDao.findCustomizedSerialNumber(null, billType);
        if (null == sysSerialNumber) {
            return null;
        }
        List<Long> tenantIdList = serialNumberDao.findTenantIds();
        List<SerialNumber> coded = serialNumberDao.findCoded(billType);
        Set<Long> tenantIdSet = Sets.newHashSet();
        Set<Long> codedTenantIdSet = Sets.newHashSet();
        if (null != tenantIdList) {
            for (Long tenantId : tenantIdList) {
                tenantIdSet.add(tenantId);
            }
        }
        if (null != coded) {
            for (SerialNumber serialNumber : coded) {
                codedTenantIdSet.add(serialNumber.getTenantId());
            }
        }
        tenantIdSet.removeAll(codedTenantIdSet);
        List<SerialNumberDetail> detailList = serialNumberDetailDao.findBySNId(sysSerialNumber.getId());

        List<SerialNumber> newSerialNumbers = Lists.newArrayList();
        List<SerialNumberDetail> newSerialNumberDetails = Lists.newArrayList();
        Date operateDate = new Date();
        if (null == tenantIdSet || tenantIdSet.isEmpty()) {
            return null;
        }

        List<Long> minSerialNumberIdList = serialNumberDao.batchGenerateId(tenantIdSet.size());
        List<Long> batchIdList = serialNumberDetailDao.batchGenerateId(tenantIdSet.size() * detailList.size());
        int mainIndex = 0;
        for (Long tenantId : tenantIdSet) {
            SerialNumber curTenantSerialNumber = new SerialNumber();
            BeanUtils.coverBean(sysSerialNumber, curTenantSerialNumber);
            curTenantSerialNumber.setId(minSerialNumberIdList.get(0));
            curTenantSerialNumber.setTenantId(tenantId);
            curTenantSerialNumber.setCreatedAt(operateDate);
            curTenantSerialNumber.setLastUpdated(operateDate);
            int index = 0;
            for (SerialNumberDetail curDetail : detailList) {
                SerialNumberDetail realDetail = new SerialNumberDetail();
                BeanUtils.coverBean(curDetail, realDetail);
                realDetail.setId(batchIdList.get(index));
                realDetail.setSerialNumberId(curTenantSerialNumber.getId());
                realDetail.setTenantId(tenantId);
                realDetail.setCreatedAt(operateDate);
                realDetail.setLastUpdated(operateDate);
                newSerialNumberDetails.add(realDetail);
                index++;
            }
            newSerialNumbers.add(curTenantSerialNumber);
            mainIndex++;
        }
        if (!newSerialNumbers.isEmpty()) {
            serialNumberDao.batchInsert(null, newSerialNumbers);
        }
        if (!newSerialNumberDetails.isEmpty()) {
            serialNumberDetailDao.batchInsert(null, newSerialNumberDetails);
        }
        LOGGER.info("根据ids修改主表：newSerialNumbers:[{}]", newSerialNumbers);
        LOGGER.info("根据ids修改子表： newSerialNumberDetails:[{}]", newSerialNumberDetails);
        return "";
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addOrUpdate(SerialNumberVo serialNumberVo) {
        SerialNumber serialNumber = new SerialNumber();
        BeanCopierUtils.copyProperties(serialNumberVo, serialNumber);
        serialNumberDao.save(serialNumber.getTenantId(), serialNumber);

        Assert.notEmpty(serialNumberVo.getDetail(), "规则明细不能为空");
        List<SerialNumberDetail> detailList = Lists.transform(serialNumberVo.getDetail(), (detailVo) -> {
            SerialNumberDetail detail = new SerialNumberDetail();
            BeanCopierUtils.copyProperties(detailVo, detail);
            if (detail.getSerialNumberId() == null) {
                detail.setSerialNumberId(serialNumber.getId());
            }
            return detail;
        });
        for (SerialNumberDetail serialNumberDetail : detailList) {
            serialNumberDetailDao.save(serialNumberDetail);
        }
    }

    @Override
    public void clearCache() {
        serialNumberDao.cacheEvict();
        serialNumberDetailDao.cacheEvict();
    }

    @Override
    @Transactional(readOnly = true)
    public SerialNumberVo findById(Long tenantId, Long id) {
        SerialNumber serialNumber = serialNumberDao.findOne(tenantId, id);

        if (serialNumber == null || serialNumber.getId() == null) {
            return null;
        }
        SerialNumberVo serialNumberVo = new SerialNumberVo();
        BeanCopierUtils.copyProperties(serialNumber, serialNumberVo);

        List<SerialNumberDetail> details = serialNumberDetailDao.findBySNId(serialNumber.getId());
        List<SerialNumberDetailVo> detailList = Lists.transform(details, (detail) -> {
            SerialNumberDetailVo detailVo = new SerialNumberDetailVo();
            BeanCopierUtils.copyProperties(detail, detailVo);
            return detailVo;
        });
        serialNumberVo.setDetail(detailList);
        return serialNumberVo;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void delete(Long tenantId, Long id) {
        serialNumberDao.delete(tenantId,id);
        serialNumberDetailDao.deleteAll(id);
    }
}
