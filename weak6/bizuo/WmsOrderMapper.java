
/**
 * 发货单Mapper管理组件
 */
public interface WmsOrderMapper {
    /**
     * 新增记录
     *
     * @param record 记录
     * @return 新增结果
     */
    int insert(WmsOrderDO record);

    /**
     * 新增记录(只插入非空字段)
     *
     * @param record 记录
     * @return 新增结果
     */
    int insertSelective(WmsOrderDO record);

    /**
     * 根据主键进行查询
     *
     * @param primaryKey 主键
     * @return 结果
     */
    WmsOrderDO selectByPrimaryKey(@Param("tenantId") Long tenantId, @Param("primaryKey") Long primaryKey);

    /**
     * 根据主键批量查询
     *
     * @param primaryKeys 主键
     * @return 结果
     */
    List<WmsOrderDO> selectByPrimaryKeys(@Param("set") Set<Long> primaryKeys);

    /**
     * 根据主键更新非空字段
     *
     * @param record 记录
     * @return 更新结果
     */
    int updateByPrimaryKeySelective(WmsOrderDO record);

    /**
     * 根据主键更新所有字段，不做非空判断
     *
     * @param record 记录
     * @return 更新结果
     */
    int updateByPrimaryKey(WmsOrderDO record);

    /**
     * 根据主键进行逻辑删除
     *
     * @param primaryKey 主键
     * @return 删除结果
     */
    int deleteByPrimaryKey(Long primaryKey);

    /**
     * 批量更新 不做非空判断
     *
     * @param list 待更新出入库单
     * @return 批量更新条数
     */
    int updateBatch(List<WmsOrderDO> list);

    /**
     * 批量更新：做非空判断
     *
     * @param list 待更新出入库单
     * @return 批量更新条数
     */
    int updateBatchSelective(List<WmsOrderDO> list);

    /**
     * 批量插入
     *
     * @param list 待插入出入库单
     * @return 插入条数
     */
    int insertList(@Param("list") List<WmsOrderDO> list);

    /**
     * 更新订单状态
     *
     * @param record 发货单
     * @return 更新条数
     */
    int updateWmsOrderStateByPrimaryKey(WmsOrderDO record);

    /**
     * 更新发货单作废状态
     *
     * @param record 发后单
     * @return 更新条数
     */
    int updateCancelStateByPrimaryKey(WmsOrderDO record);

    /**
     * 根据查询条件查询
     *
     * @param wmsOrderDO 发货单
     * @return 发货单
     */
    List<WmsOrderDO> listByAll(WmsOrderDO wmsOrderDO);

    /**
     * 分页统计总条数
     *
     * @param query 查询条件
     * @return 总条数
     */
    Long countByPage(WmsOrderQueryDTO query);

    /**
     * 分页查询发货单
     *
     * @param query 发货单查询条件
     * @return 发货单集合
     */
    List<WmsOrderDO> listByPage(WmsOrderQueryDTO query);

    /**
     * 根据波次策略条件查询发货单
     *
     * @param query 查询条件
     * @return 发货单
     */
    List<WmsOrderDO> listByWaveStrategy(WmsOrderByStrategyQueryDTO query);

    /**
     * 根据波次策略查询发货单主键
     * @param query 波次策略查询条件
     * @return 发货单
     */
    List<Long> listPrimaryKeyByWaveStrategy(WmsOrderByStrategyQueryDTO query);

    /**
     * 通过快递单号查询发货单信息
     *
     * @param tenantId    租户ID
     * @param waybillCode 快递单号
     * @return 发货单集合
     */
    WmsOrderDO findOrderByWayBillNo(@Param("tenantId") Long tenantId, @Param("waybillCode") String waybillCode);

    /**
     * 根据主键进行查询
     *
     * @param wmsOrderId 主键
     * @param tenantId   租户ID
     * @return 结果
     */
    WmsOrderDO selectOneOrderByPrimaryKey(@Param("tenantId") Long tenantId, @Param("wmsOrderId") Long wmsOrderId);

    /**
     * 查询指定租户的一批订单ID
     *
     * @param saleOrderIds 订单ID
     * @param tenantId     租户ID
     * @return 发货单
     */
    List<WmsOrderDO> listInSaleOrderIdsAndTenantId(@Param("saleOrderIds") Set<Long> saleOrderIds,
                                                   @Param("tenantId") Long tenantId);

    /**
     * 按发货单计算每个发货单里有
     *
     * @param tenantId    租户ID
     * @param wmsOrderIds 发货单id集合
     * @return
     */
    List<Long> countPrintedOrderByIds(@Param("tenantId") Long tenantId, @Param("wmsOrderIds") Set<Long> wmsOrderIds);

    int queryStateCount(@Param("tenantId") Long tenantId, @Param("state") Integer state, @Param("type") Integer type);
}
