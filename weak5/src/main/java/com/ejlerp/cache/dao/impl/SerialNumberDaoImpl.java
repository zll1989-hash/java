package com.ejlerp.cache.dao.impl;

import com.ejlerp.cache.api.IDGenerator;
import com.ejlerp.cache.dao.SerialNumberDao;
import com.ejlerp.cache.domain.SerialNumber;
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
 * SerialNumberDaoImpl
 *
 * @author Eric
 * @date 16/6/3
 */
@Repository
public class SerialNumberDaoImpl implements SerialNumberDao {
    public final static Long SUPER_TENANT_ID = -1L;
    private final static String TABLE_NAME = "serial_number";
    private final static String ID_NAME = "serial_number_id";
    private final static String[] COLUMNS = new String[]{
            ID_NAME,
            "example",
            "remarkk",
            "bill_type",
            "tenant_id",
            "create_time",
            "creator",
            "last_update_time",
            "last_updater"};
    private static Logger LOGGER = LoggerFactory.getLogger(SerialNumberDaoImpl.class);
    @Autowired
    private IDGenerator idGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public SerialNumber findOne(Long tenantId, Long id) {
        String sql = String.format("select * from %s where is_usable=1 and %s=? and %s=?", TABLE_NAME, ID_NAME, "tenant_id");
        LOGGER.debug("查询单条序列号规则的SQL:{},参数为:{}", sql, id);
        List<SerialNumber> results = jdbcTemplate.query(sql, new Object[]{id, tenantId}, getRowMapper());
        if (CollectionUtils.isEmpty(results)) {
            LOGGER.warn("id={}的序列号规则记录不存在.", id);
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public List<SerialNumber> findAll(Long tenantId) {
        String sql = String.format("select * from %s where is_usable=true and tenant_id=%s", TABLE_NAME, tenantId);
        LOGGER.debug("查询全部序列号规则的SQL:{}", sql);
        return jdbcTemplate.query(sql, getRowMapper());
    }

    @Override
    public void save(Long tenantId, SerialNumber sn) {
        if (sn.getId() == null) {
            //新增
            String sql = String.format("insert into %s (%s) values (%s)", TABLE_NAME, Joiner.on(",").join(COLUMNS), generatePlaceholders(COLUMNS.length));
            LOGGER.debug("插入SQL:{}", sql);
            Long id = idGenerator.generate(TABLE_NAME);
            jdbcTemplate.update(sql, new Object[]{id,
                    sn.getExample(), sn.getRemark(), sn.getEntityName(), sn.getTenantId(), new Date(),
                    sn.getCreator(), new Date(), sn.getLastUpdater()
            });
            sn.setId(id);
        } else {
            //更新
            String sql = String.format("update %s set %s where %s=?", TABLE_NAME, generateUpdateSql(COLUMNS), ID_NAME);
            LOGGER.debug("更新SQL:{}", sql);
            jdbcTemplate.update(sql, new Object[]{
                    sn.getExample(), sn.getRemark(), sn.getEntityName(), sn.getTenantId(), sn.getCreatedAt(),
                    sn.getCreator(), new Date(), sn.getLastUpdater(), sn.getId()
            });
        }
    }

    @Override
    public void delete(Long tenantId, Long id) {
        String sql = String.format("delete from %s where is_usable=1 and %s=? and %s=?", TABLE_NAME, ID_NAME, "tenant_id");

        LOGGER.debug("删除的SQL:{}", sql);
        jdbcTemplate.update(sql, new Object[]{id, tenantId});

        LOGGER.info("id={}的序列号记录已经被物理删除", id);
    }

    @Override
    public SerialNumber findCustomizedSerialNumber(Long tenantId, String entityName) {
        String sql = String.format("select * from %s where bill_type=? and tenant_id=? and is_usable=true", TABLE_NAME);
        if (tenantId == null) {
            tenantId = SUPER_TENANT_ID;
        }
        LOGGER.debug("查询序列号规则的SQL:{},参数为:{}", sql, new Object[]{entityName, tenantId});

        List<SerialNumber> results = jdbcTemplate.query(sql, new Object[]{entityName, tenantId}, getRowMapper());

        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        if (results.size() > 1) {
            LOGGER.warn("查询序列号规则发现有重复记录,请检查数据库.");
        }
        return results.get(0);
    }

    @Override
    public void cacheEvict() {
        LOGGER.debug("手动清理缓存");
    }

    @Override
    public List<SerialNumber> findCoded(String billType) {
        String sql = String.format("select * from %s where bill_type=? and is_usable=true", TABLE_NAME);
        LOGGER.debug("查询已存在的规则的SQL:{},参数为:{}", sql, new Object[]{billType});
        List<SerialNumber> results = jdbcTemplate.query(sql, new Object[]{billType}, getRowMapper());
        return results;
    }

    @Override
    public List<Long> findTenantIds() {
        String sql = String.format("select distinct(tenant_id) from %s where is_usable = true", TABLE_NAME);
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public List<Long> batchGenerateId(int size) {
        return idGenerator.generateList(TABLE_NAME, size);
    }

    @Override
    public Integer batchInsert(Long tenantId, List<SerialNumber> serialNumbers) {
        //新增
        String sql = String.format("insert into %s (%s) values (%s)", TABLE_NAME, Joiner.on(",").join(COLUMNS), generatePlaceholders(COLUMNS.length));
        LOGGER.debug("插入SQL:{}", sql);
        List<Object[]> batchArgs = new ArrayList<>();
        for (SerialNumber sn : serialNumbers) {
            batchArgs.add(new Object[]{sn.getId(), sn.getExample(), sn.getRemark(), sn.getEntityName(), sn.getTenantId(), new Date(),
                    sn.getCreator(), new Date(), sn.getLastUpdater()});
        }
        int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);
        if (null == results) {
            return 0;
        }
        return results.length;
    }

    private RowMapper<SerialNumber> getRowMapper() {
        return new RowMapper<SerialNumber>() {
            @Override
            public SerialNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
                SerialNumber sn = new SerialNumber();
                sn.setId(rs.getLong(ID_NAME));
                sn.setExample(rs.getString("example"));
                sn.setRemark(rs.getString("remarkk"));
                sn.setEntityName(rs.getString("bill_type"));
                sn.setTenantId(rs.getLong("tenant_id"));
                sn.setCreatedAt(rs.getTimestamp("create_time"));
                sn.setCreator(rs.getLong("creator"));
                sn.setLastUpdated(rs.getTimestamp("last_update_time"));
                sn.setLastUpdater(rs.getLong("last_updater"));
                return sn;
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
