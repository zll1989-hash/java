package com.ejlerp.cache.service;

import cn.egenie.cache.redis.core.RedisKvCacheService;
import cn.egenie.cache.redis.core.RedisListCacheService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ejlerp.cache.api.SerialNoGenerator;
import com.ejlerp.cache.dao.SerialNumberDao;
import com.ejlerp.cache.dao.SerialNumberDetailDao;
import com.ejlerp.cache.domain.SerialNumber;
import com.ejlerp.cache.domain.SerialNumberDetail;
import com.ejlerp.cache.util.DateUtil;
import com.ejlerp.cache.util.JexlUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SerialNoGeneratorImpl
 *
 * @author Eric
 * @date 16/6/13
 */
@DubboService(version = "${service.version:0.1}")
public class SerialNoGeneratorImpl implements SerialNoGenerator {
    private final static Logger LOGGER = LoggerFactory.getLogger(SerialNoGeneratorImpl.class);

    /**
     * 调用SerialNoGenerator需要传递table name,用于区分不同的模型
     */
    private final static String TABLE_NAME = "table_name";

    /**
     * tenant Id的列名(必须统一)
     */
    private final static String TANENT_ID = "tenantId";

    @Autowired
    private RedisKvCacheService redisKvCacheService;

    @Autowired
    private SerialNumberDao numberDao;

    @Autowired
    private SerialNumberDetailDao numberDetailDao;

    @Override
    public String generateSerialNumber(JSONObject entity) {
        LOGGER.debug("需要计算SerialNo的实体信息:{}", entity.toString());

        SerialNumber sn = numberDao.findCustomizedSerialNumber(entity.getLong(TANENT_ID), entity.getString(TABLE_NAME));
        Assert.notNull(sn, "当前实体没有自定义的SerialNo生成规则");

        //查询SerialNumberDetail列表
        List<SerialNumberDetail> details = numberDetailDao.findBySNId(sn.getId());
        Assert.notEmpty(details);
        LOGGER.debug("具体的自定义SerialNo生成规则包括:{}", details);

        StringBuilder sb = new StringBuilder();
        for (SerialNumberDetail detail : details) {
            //挨个解析SerialNumberDetail,生成序列号的片段
            if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_STR)) {
                //固定字符串,直接取值
                sb.append(detail.getFormat());
            } else if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_TIMESTAMP)) {
                //时间戳模式串,将系统时间按该模式转换为字符串
                sb.append(DateUtil.date2String(new Date(), detail.getFormat()));
            } else if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_SEQUENCE)) {
                //自增数,需要从Cache服务中获取当前自增数,自增数的key要考虑租户相关
                //sequence类型的SerialNumberDetail必须有Timestamp类型的SerialNumberDetail配套
                SerialNumberDetail detailFormat = this.getDetailFormat(details);
                Assert.notNull(detailFormat);

                String key = this.generateSequenceKey(entity.getString(TABLE_NAME), detailFormat.getResetType(), detailFormat.getFormat(), entity.getLong(TANENT_ID));
                sb.append(this.paddingFixedNumber(this.getSequenceValue(key, detailFormat.getResetType(), detailFormat.getFormat(), 1), detail.getLength()));
            } else {
                //第四种类型,需要参考Entity的某些字段的值,甚至还需要解析公式
                sb.append(this.getFormulaResult(entity, detail));
            }
            LOGGER.debug("当前的序列号结果:{}", sb.toString());
        }

        return sb.toString();
    }


    @Override
    public List<String> batchGenerateSerialNumber(Long tenantId, String tableName, JSONArray entitys) {
        LOGGER.debug("批量计算[{}]的SerialNo的数量:{}", tableName, entitys.size());

        SerialNumber sn = numberDao.findCustomizedSerialNumber(tenantId, tableName);
        Assert.notNull(sn, "当前实体没有自定义的SerialNo生成规则");

        List<String> list = new ArrayList<String>();
        //查询SerialNumberDetail列表
        List<SerialNumberDetail> details = numberDetailDao.findBySNId(sn.getId());
        Assert.notEmpty(details);
        LOGGER.debug("具体的自定义SerialNo生成规则包括:{}", details);

        for (int i = 0; i < entitys.size(); i++) {
            JSONObject entity = entitys.getJSONObject(i);
            StringBuilder sb = new StringBuilder();
            for (SerialNumberDetail detail : details) {
                //挨个解析SerialNumberDetail,生成序列号的片段
                if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_STR)) {
                    //固定字符串,直接取值
                    sb.append(detail.getFormat());
                } else if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_TIMESTAMP)) {
                    //时间戳模式串,将系统时间按该模式转换为字符串
                    sb.append(DateUtil.date2String(new Date(), detail.getFormat()));
                } else if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_SEQUENCE)) {
                    //自增数,需要从Cache服务中获取当前自增数,自增数的key要考虑租户相关
                    SerialNumberDetail detailFormat = this.getDetailFormat(details);
                    Assert.notNull(detailFormat); //sequence类型的SerialNumberDetail必须有Timestamp类型的SerialNumberDetail配套

                    String key = this.generateSequenceKey(tableName, detailFormat.getResetType(), detailFormat.getFormat(), tenantId);
                    sb.append(this.paddingFixedNumber(this.getSequenceValue(key, detailFormat.getResetType(), detailFormat.getFormat(), 1), detail.getLength()));
                } else {
                    //第四种类型,需要参考Entity的某些字段的值,甚至还需要解析公式
                    sb.append(this.getFormulaResult(entity, detail));
                }
            }
            LOGGER.debug("第{}个序列号结果:{}", i, sb);
            list.add(sb.toString());
        }

        return list;
    }

    /**
     * 从构成SerialNO的details集合中遍历寻找Timestamp类型的记录,并返回其Format
     *
     * @param details
     * @return
     */
    private SerialNumberDetail getDetailFormat(List<SerialNumberDetail> details) {
        if (!CollectionUtils.isEmpty(details)) {
            for (SerialNumberDetail detail : details) {
                if (detail.getDetailType().equals(SerialNumberDetail.DETAIL_TYPE_TIMESTAMP)) {
                    return detail;
                }
            }
        }
        return null;
    }

    private long getSequenceValue(String key, int resetType, String ymdFormat, int count) {
        LOGGER.debug("SEQUENCE key:{}, resetType:{}, ymdFormat:{}", key, resetType, ymdFormat);

        long next = redisKvCacheService.increment(key, count);
        if (next == 1) {
            // 当前key初始化,需要设定过期时间
            int expired = -1;
            if (resetType == SerialNumberDetail.RESET_TYPE_PERIOD) {
                if (ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YM1)
                        || ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YM2)
                        || ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YM3)
                        || ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YM4)) {
                    expired = 31;
                } else {
                    expired = 1;
                }
            }

            if (expired != -1) {
                redisKvCacheService.expire(key, expired, TimeUnit.DAYS);
            }
        }
        return next;
    }

    /**
     * 组合序号
     *
     * @param tableName
     * @param resetType
     * @param ymdFormat
     * @param tenantId
     * @return
     */
    private String generateSequenceKey(String tableName, Integer resetType, String ymdFormat, Long tenantId) {
        //ex: {"SKU_20160801_100005": "000010"}
        //201608根据resetType来决定:0表示永不过期;1表示需要按照ymdFormt来按日清零或按月清零
        Date now = new Date();
        String timePart = DateUtil.date2String(now, "yyyyMMdd");
        Assert.notNull(timePart);
        if (null != timePart && resetType == SerialNumberDetail.RESET_TYPE_PERIOD) {
            //生成键的时候，按照日期format生成对应的日或月的键
            if (ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YMD1) || ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YMD2)
                    || ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YMD3) || ymdFormat.equals(SerialNumberDetail.RESET_FORMAT_YMD4)) {
                timePart = "_" + timePart;
            } else {
                timePart = "_" + timePart.substring(0, 6);
            }
        } else {
            timePart = ""; //不需要时间部分,生成的key: SKU_100005
        }

        if (tenantId == null) {
            return String.format("%s%s", tableName.toLowerCase(), timePart);
        } else {
            return String.format("%s%s_%s", tableName.toLowerCase(), timePart, tenantId);
        }
    }

    /**
     * 根据实体类型+租户id+时间戳,生成默认的序列号
     *
     * @param entityName
     * @return
     */
    private String generateDefaultSerialNumber(String entityName, Long tenantId) {
        //Entity的名称,转换为全大写
        String prefix = entityName.substring(0, 2).toUpperCase();
        String timestamp = DateUtil.date2String(new Date(), "yyyyMMdd");
        String key = generateSequenceKey(entityName, SerialNumberDetail.RESET_TYPE_PERIOD, SerialNumberDetail.RESET_FORMAT_YMD4, tenantId);
        String sequenceValue = this.paddingFixedNumber(this.getSequenceValue(key, SerialNumberDetail.RESET_TYPE_PERIOD, SerialNumberDetail.RESET_FORMAT_YMD4, 1), 5);
        return String.format("%s_%s_%s", prefix, timestamp, sequenceValue);
    }

    /**
     * 根据实体类型+租户id+时间戳+流水号,批量生成默认的序列号
     *
     * @param entityName
     * @return
     */
    private List<String> batchGenerateDefaultSerialNumber(String entityName, Long tenantId, int count) {
        List<String> list = new ArrayList<>();
        //Entity的名称,转换为全大写
        String prefix = entityName.substring(0, 2).toUpperCase();
        String timestamp = DateUtil.date2String(new Date(), "yyyyMMdd");
        String key = generateSequenceKey(entityName, SerialNumberDetail.RESET_TYPE_PERIOD, SerialNumberDetail.RESET_FORMAT_YMD4, tenantId);
        long sequenceValue = this.getSequenceValue(key, SerialNumberDetail.RESET_TYPE_PERIOD, SerialNumberDetail.RESET_FORMAT_YMD4, count) - count + 1;
        for (int i = 0; i < count; i++) {
            String serialNumber = String.format("%s_%s_%s", prefix, timestamp, this.paddingFixedNumber((sequenceValue), 5));
            sequenceValue++;
            list.add(serialNumber);
        }

        return list;
    }

    /**
     * 根据entity对象,以及具体的序列号生成规则,生成序列号片段
     *
     * @param entity
     * @param snDetail
     * @return
     */
    private String getFormulaResult(JSONObject entity, SerialNumberDetail snDetail) {
        String detailType = snDetail.getDetailType(); //多字段用逗号分隔

        try {
            //解析出参与序列号计算的字段和值
            String[] fields = detailType.split(",");
            Object[] values = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                values[i] = this.parseFieldValue(fields[i].trim(), entity);
            }

            if (!snDetail.isNeedFormula()) {
                //不需要公式时,直接返回值,用"-"连起来
                return Joiner.on("-").join(values);
            } else {
                Map<String, Object> params = Maps.newHashMap();
                for (int i = 0; i < fields.length; i++) {
                    params.put(fields[i], values[i]);
                }

                return String.valueOf(JexlUtil.evaluate(snDetail.getFormat(), params));
            }
        } catch (Exception e) {
            LOGGER.warn("计算序列号遇到公式解析错误.", e);
            return null;
        }
    }

    /**
     * 传入一个字段表达式和一个对象的实例,解析出该字段表达式对应的值.
     * <p>
     * 例如表达式为product.productNo,传入实例为某sku实例,
     * 则返回sku.product属性的productNo属性的值.
     * <p>
     * !!!注:目前只支持2级嵌套对象,比如说:
     * product.productNo可以支持,
     * 而product.color.colorType则不支持.
     *
     * @param fieldStr
     * @param entity
     * @return
     */
    private Object parseFieldValue(String fieldStr, JSONObject entity) throws Exception {
        LOGGER.debug("尝试解析的表达式:{},待解析的对象:{}", fieldStr, entity);

        String[] fields = fieldStr.split("\\.");
        if (fields.length > 2) {
            //大于两级的嵌套,暂不支持
            throw new RuntimeException("字段表达式[" + fieldStr + "]不支持");
        }

        if (fields.length == 1) {
            //只有一级,直接返回
            return entity.get(fieldStr);
        }

        //两级嵌套,第一级还是JSONObject
        JSONObject field = (JSONObject) entity.get(fields[0]);
        if (field == null) {
            throw new RuntimeException("指定的字段不存在, 字段名称:" + fields[0]);
        }

        Object result = field.get(fields[1]);
        return result;
    }

    /**
     * 按照定长格式化数字串,例如"1"格式化为"000001"(size=6)
     *
     * @param number
     * @param size
     * @return
     */
    private String paddingFixedNumber(long number, int size) {
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < size; i++) {
            format.append("0");
        }

        DecimalFormat df = new DecimalFormat(format.toString());
        return df.format(number);
    }

}
