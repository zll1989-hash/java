package com.ejlerp.cache.dao.impl;

import com.ejlerp.cache.api.IDGenerator;
import com.ejlerp.cache.dao.SerialNumberDetailDao;
import com.ejlerp.cache.domain.SerialNumberDetail;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SerialNumberDetailDaoImpl
 *
 * @author Eric
 * @date 16/6/3
 */
@Repository
public class SerialNumberDetailDaoImpl implements SerialNumberDetailDao {
    private final static String TABLE_NAME = "serial_number_detail";
    private final static String ID_NAME = "serial_number_detail_id";
    //!!!注意字段顺序不能错乱,否则下面的保存逻辑会出错
    private final static String[] COLUMNS = new String[]{ID_NAME,
            "property_name", "format", "need_formula", "length", "serial_number_id",
            "display_order", "is_reset_according", "tenant_id", "create_time", "creator",
            "last_update_time", "last_updater"};
    private static Logger LOGGER = LoggerFactory.getLogger(SerialNumberDetailDaoImpl.class);
    @Autowired
    private IDGenerator idGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public SerialNumberDetail findOne(long id) {
        String sql = String.format("select * from %s where %s=?", TABLE_NAME, ID_NAME);
        LOGGER.debug("查询单条序列号规则详情的SQL:{},参数为:{}", sql, id);

        List<SerialNumberDetail> results = jdbcTemplate.query(sql, new Object[]{id}, getRowMapper());
        if (CollectionUtils.isEmpty(results)) {
            LOGGER.warn("id={}的序列号规则记录不存在.", id);
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public void save(SerialNumberDetail snd) {
        if (snd.getId() == null) {
            //新增
            String sql = String.format("insert into %s (%s) values (%s)", TABLE_NAME, Joiner.on(",").join(COLUMNS), generatePlaceholders(COLUMNS.length));
            LOGGER.debug("插入SQL:{}", sql);
            jdbcTemplate.update(sql, new Object[]{idGenerator.generate(TABLE_NAME),
                    snd.getDetailType(), snd.getFormat(), snd.isNeedFormula(), snd.getLength(), snd.getSerialNumberId(),
                    snd.getDisplayOrder(), snd.getResetType(), snd.getTenantId(), new Date(), snd.getCreator(),
                    new Date(), snd.getLastUpdater()
            });
        } else {
            //更新
            String sql = String.format("update %s set %s where %s=?", TABLE_NAME, generateUpdateSql(COLUMNS), ID_NAME);
            LOGGER.debug("更新SQL:{}", sql);
            jdbcTemplate.update(sql, new Object[]{
                    snd.getDetailType(), snd.getFormat(), snd.isNeedFormula(), snd.getLength(), snd.getSerialNumberId(),
                    snd.getDisplayOrder(), snd.getResetType(), snd.getTenantId(), snd.getCreatedAt(), snd.getCreator(),
                    new Date(), snd.getLastUpdater(), snd.getId()
            });
        }
    }

    @Override
    public void delete(long id) {
        String sql = String.format("delete from %s where %s=?", TABLE_NAME, ID_NAME);

        LOGGER.debug("删除的SQL:{}", sql);
        jdbcTemplate.update(sql, new Object[]{id});

        LOGGER.info("id={}的序列号详情记录已经被物理删除", id);
    }

    @Override
    public void deleteAll(long serialNumberId) {
        String sql = String.format("delete from %s where %s=?", TABLE_NAME, "serial_number_id");

        LOGGER.debug("删除的SQL:{}", sql);
        jdbcTemplate.update(sql, new Object[]{serialNumberId});

        LOGGER.info("serial_number_id={}的序列号详情记录已经全部被物理删除", serialNumberId);
    }

    @Override
    public List<SerialNumberDetail> findBySNId(long id) {
        String sql = String.format("select * from %s where serial_number_id=? and is_usable = 1 order by display_order", TABLE_NAME);
        LOGGER.debug("查询序列号规则详情的SQL:{},参数为:{}", sql, id);

        List<SerialNumberDetail> results = jdbcTemplate.query(sql, new Object[]{id}, getRowMapper());
        return results;
    }

    @Override
    public void cacheEvict() {
        LOGGER.debug("手动清理缓存");
    }

    @Override
    public Integer batchInsert(Long tenantId, List<SerialNumberDetail> SerialNumberDetails) {
        String sql = String.format("insert into %s (%s) values (%s)", TABLE_NAME, Joiner.on(",").join(COLUMNS), generatePlaceholders(COLUMNS.length));
        LOGGER.debug("插入SQL:{}", sql);
        List<Object[]> batchArgs = new ArrayList<>();
        for (SerialNumberDetail snd : SerialNumberDetails) {
            batchArgs.add(new Object[]{snd.getId(),
                    snd.getDetailType(), snd.getFormat(), snd.isNeedFormula(), snd.getLength(), snd.getSerialNumberId(),
                    snd.getDisplayOrder(), snd.getResetType(), snd.getTenantId(), snd.getCreatedAt(), snd.getCreator(),
                    new Date(), snd.getLastUpdater()
            });
        }
        int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);
        if (null == results) {
            return 0;
        }
        return results.length;
    }

    @Override
    public List<Long> batchGenerateId(int size) {
        return idGenerator.generateList(TABLE_NAME, size);
    }

    private RowMapper<SerialNumberDetail> getRowMapper() {
        return new RowMapper<SerialNumberDetail>() {
            @Override
            public SerialNumberDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                SerialNumberDetail snd = new SerialNumberDetail();
                snd.setId(rs.getLong(ID_NAME));
                snd.setDetailType(rs.getString("property_name"));
                snd.setFormat(rs.getString("format"));
                snd.setNeedFormula(rs.getBoolean("need_formula"));
                snd.setLength(rs.getInt("length"));
                snd.setSerialNumberId(rs.getLong("serial_number_id"));
                snd.setDisplayOrder(rs.getInt("display_order"));
                snd.setResetType(rs.getInt("is_reset_according"));
                snd.setTenantId(rs.getLong("tenant_id"));
                snd.setCreatedAt(rs.getTimestamp("create_time"));
                snd.setCreator(rs.getLong("creator"));
                snd.setLastUpdated(rs.getTimestamp("last_update_time"));
                snd.setLastUpdater(rs.getLong("last_updater"));
                return snd;
            }
        };
    }

    private String generateUpdateSql(String[] columnNames) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columnNames) {
            //忽略ID字段
            if (columnName.equals(ID_NAME)) {
                continue;
            }

            sb.append(columnName);
            sb.append("=?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    private String generatePlaceholders(int size) {
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = "?";
        }
        return Joiner.on(",").join(arr);
    }

}
