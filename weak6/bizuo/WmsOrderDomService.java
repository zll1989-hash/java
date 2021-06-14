
@Service
public class WmsOrderDomService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(WmsOrderDomService.class);
    /**
     * spring ioc容器
     */
    @Autowired
    SpringApplicationContext context;
    /**
     * 发货单创建器工厂
     */
    @Autowired
    WmsOrderProducerFactory wmsOrderProducerFactory;
    /**
     * 发货单Mapper管理组件
     */
    @Autowired
    private WmsOrderMapper wmsOrderMapper;
    /**
     * 发货单明细管理Mapper组件
     */
    @Autowired
    private WmsOrderDetailMapper wmsOrderDetailMapper;
    /**
     * 收件人管理Mapper组件
     */
    @Autowired
    private WmsOrderReceiverMapper wmsOrderReceiverMapper;
    /**
     * 发货单状态管理组件
     */
    @Autowired
    private OrderStateManager orderStateManager;
    /**
     * 波次创建、移除服务
     */
    @DubboReference(version="${service.version:0.1}", check = false)
    private WaveService waveService;
    /**
     * 发货单业务操作记录管理mapper
     */
    @Autowired
    private WmsOrderOperationDomService wmsOrderOperationDomService;
    /**
     * 仓库服务组件
     */
    @DubboReference(version="${service.version:0.1}")
    private WarehouseService warehouseService;
    /**
     * 云仓货主仓库绑定服务
     */
    @DubboReference(version="${service.version:0.1}")
    private CloudOwnerWarehouseBindService cloudOwnerWarehouseBindService;
    /**
     * 快递公司服务
     */
    @DubboReference(version="${service.version:0.1}")
    private CourierService courierService;
    /**
     * ID生成器
     */
    @DubboReference(version="${service.version:0.1}")
    private IDGenerator idGenerator;
    /**
     * 日期组件
     */
    @Autowired
    private DateProvider dateProvider;
    /**
     * 通知OMS订单状态和电子面单号URL
     */
    @Value("${wms.notice.oms.state.url}")
    private String informOmsStateUrl;
    /**
     * 调用http请求url和port基础配置
     */
    @Resource
    private ApiRestConfig apiRestConfig;
    /**
     * 实物锁库存服务
     */
    @DubboReference(version="${service.version:0.1}")
    private CloudStockLockModifyService cloudStockLockModifyService;
    /**
     * 实物锁查询服务
     */
    @DubboReference(version="${service.version:0.1}")
    private CloudStockLockQueryService cloudStockLockQueryService;
    /**
     * 发货单状态
     */
    @Autowired
    private OrderStateDelegator orderStateDelegator;
    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor waveThreadPoolTaskExecutor;
    /**
     * 业务日志组件
     */
    @Resource
    private BizLogger bizLogger;
    /**
     * 发货单打包后的每个batch size
     */
    @Value("${wms.order.list.batch}")
    private int batchSize;
    /**
     * 发货单明细DOM服务
     */
    @Autowired
    private WmsOrderDetailDomService wmsOrderDetailDomService;

    /**
     * 内部OMS发送MQ创建发货单
     * 1：插入数据
     * 2：发送请求获取单号: 可以基于命令模式和观察者模式来异步调用请求结果
     * 3：如果请求失败，记录失败信息到wms_order_operation表
     * 4：如果成功记录信息到wms_order_receiver表
     * 5：记录日志
     * 6：发送数据到OMS-->回写发货单状态到OMS
     *
     * @param messageVO OMS发送到WmS消息VO
     * @throws Exception 异常信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void createWmsOrder(CallerInfo callerInfo, OmsToWmsCreateOrderMessageVO messageVO) throws Exception {
        logger.info("### create wms order, saleOrderId-->{}, tenantId-->{}, operatorId-->{}, msg-->{}",
                messageVO.getSaleOrderId(), messageVO.getTenantId(), messageVO.getOperatorId(), messageVO);

        WmsOrderProducer<OmsToWmsCreateOrderMessageVO, WmsOrderDTO> wmsOrderProducer = wmsOrderProducerFactory.create(
                WmsOrderProducerEnum.INTERNAL_OMS_CREATE.getProducer());
        // 创建发货单
        WmsOrderDTO wmsOrderDTO = wmsOrderProducer.produce(messageVO);
        // 获取发货单明细
        List<WmsOrderDetailDTO> wmsOrderDetailDTOS = wmsOrderDTO.getDetails();
        // 创建收件人
        WmsOrderReceiverDTO wmsOrderReceiverDTO = buildReceiverInfo(messageVO, wmsOrderDTO);

        WmsOrderOperationDTO operation = wmsOrderDTO.getWmsOrderOperationDTO();
        List<WmsOrderDetailDO> wmsOrderDetailDOS = ObjectUtils.convertList(wmsOrderDetailDTOS, WmsOrderDetailDO.class);

        // 保存数据
        wmsOrderMapper.insertSelective(wmsOrderDTO.clone(WmsOrderDO.class));
        wmsOrderDetailMapper.insertList(wmsOrderDetailDOS);
        wmsOrderReceiverMapper.insertSelective(wmsOrderReceiverDTO.clone(WmsOrderReceiverDO.class));
        wmsOrderOperationDomService.saveOperation(callerInfo, operation);
        orderStateManager.createOrder(callerInfo, wmsOrderDTO);

        bizLogger.recordLog(CommonConst.DEFAULT_CREATOR_LONG, wmsOrderDTO.getTenantId(), LogBizModuleTypeEnum.WMS_ORDER,
                LogBizOperationTypeEnum.WMS_ORDER_CREATE, BizOperationResultEnum.SUCCESSFUL.getCode(), wmsOrderDTO.getId(),
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, wmsOrderDTO.getCreateTime());
    }

    /**
     * 分页查询 发货单
     *
     * @param queryVO 查询条件
     * @return 发货单
     */
    public PagedList<WmsOrderVO> listByPage(CallerInfo callerInfo, WmsOrderQueryVO queryVO) throws Exception {
        WmsOrderQueryBuilder wmsOrderQueryBuilder = context.getBean(WmsOrderQueryBuilder.class);

        WmsOrderQueryDTO query = wmsOrderQueryBuilder.initBuildCommonCondition(queryVO, callerInfo)
                .buildEndDateCondition(queryVO)
                .buildOtherCondition(queryVO)
                .buildCourierCondition(queryVO)
                .buildOrderCondition(queryVO)
                .buildSellerCondition(queryVO)
                .buildStartDateCondition(queryVO)
                .buildWeightCondition(queryVO)
                .buildSkuNum(queryVO)
                .build();
        Long totalCounts = wmsOrderMapper.countByPage(query);
        if (null == totalCounts || totalCounts.equals(CommonConst.ZERO.longValue())) {
            return new PagedList<>(queryVO.getPage(), queryVO.getPageSize(), new ArrayList<>());
        }
        int pageSize = query.getPageSize();

        long offset = (query.getPage() - 1) * pageSize;
        query.setPage((int) offset);

        List<WmsOrderDTO> wmsOrderDOS = ObjectUtils.convertList(wmsOrderMapper.listByPage(query), WmsOrderDTO.class);

        PagedList<WmsOrderVO> page = new PagedList<>(query.getPage(), query.getPageSize(), packageData(wmsOrderDOS, callerInfo));
        page.setTotalCount(totalCounts);
        page.setTotalPageCount(totalCounts / pageSize == 0 ? totalCounts / pageSize : totalCounts / query.getPageSize() + 1);
        page.setPage(query.getPage());
        return page;
    }

    /**
     * 发货单作废
     *
     * @param messageVO 作废发货单消息VO
     * @throws Exception 异常信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelWmsOrder(OmsToWmsCancelOrderMessageVO messageVO) throws Exception {
        logger.info("cancel wms order msg, saleOrderId-->{}, operatorId-->{}",
                messageVO.getSaleOrderId(), messageVO.getOperatorId());
        long start = System.currentTimeMillis();

        List<WmsOrderDTO> wmsOrderDTOS = listBySaleOrderId(messageVO.getSaleOrderId());

        if (CollectionUtils.isEmpty(wmsOrderDTOS)) {
            logger.info("cancel wms order select result is null, messageVO:{}", messageVO);
            throw new BizException(WMS_ORDER_NULL_BY_SALE_ORDER_ID_EXCEPTION.getCode(),
                    WMS_ORDER_NULL_BY_SALE_ORDER_ID_EXCEPTION.getException());
        }

        WmsOrderDTO wmsOrderDTO = wmsOrderDTOS.get(CommonConst.ZERO);
        CallerInfo callerInfo = new CallerInfo(wmsOrderDTO.getTenantId(), CommonConst.DEFAULT_CREATOR_LONG);

        // 记录业务操作
        WmsOrderOperationDTO operation = WmsOrderDTO.operation(
                ORDER_UN_APPROVE.getCode(), wmsOrderDTO, CommonConst.EMPTY_STR);
        wmsOrderOperationDomService.saveOperation(callerInfo, operation);

        List<WmsOrderDetailDTO> details = ObjectUtils.convertList(wmsOrderDetailMapper.listByWmsOrderIds(
                Sets.newHashSet(wmsOrderDTO.getId())), WmsOrderDetailDTO.class);
        wmsOrderDTO.setDetails(details);

        // 释放库存
        InvokeApi<Boolean> invokeApi = cloudStockLockModifyService.releaseEntityStock(callerInfo,
                Sets.newHashSet(wmsOrderDTO.getId()));
        if (!invokeApi.getSuccess()) {
            throw new BizException(WmsOrderBizExceptionEnum.CREATE_WMS_ORDER_LOCK_ENTITY_STOCK_FAIL_EXCEPTION.getCode(),
                    CREATE_WMS_ORDER_LOCK_ENTITY_STOCK_FAIL_EXCEPTION.getException());
        }

        // 更新发货单作废状态：作废发货单
        orderStateManager.cancelOrder(callerInfo, wmsOrderDTO);

        // 移除波次
        if (wmsOrderDTO.getWave()) {
            InvokeApi<Boolean> removeApi = waveService.leaveWave(callerInfo, messageVO.getSaleOrderId());
            logger.info("cancel wms order leave wave result is-->{},{},{}",
                    removeApi.getErrorMsg(), removeApi.getData(), removeApi.getErrorMsg());
        }

        bizLogger.recordLog(CommonConst.DEFAULT_CREATOR_LONG, wmsOrderDTO.getTenantId(), LogBizModuleTypeEnum.WMS_ORDER,
                LogBizOperationTypeEnum.WMS_ORDER_CANCEL, BizOperationResultEnum.SUCCESSFUL.getCode(), wmsOrderDTO.getId(),
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());

        logger.info("cancel wms order msg end-->{}ms", System.currentTimeMillis() - start);
        return true;
    }

    /**
     * 根据订单ID查询没作废的发货单
     *
     * @param saleOrderId 订单ID
     * @return 发货单
     * @throws Exception 异常
     */
    public WmsOrderDTO findBySaleOrderId(Long saleOrderId) throws Exception {
        List<WmsOrderDTO> wmsOrderDTOS = listBySaleOrderId(saleOrderId);
        return CollectionUtils.isEmpty(wmsOrderDTOS) ? null : wmsOrderDTOS.get(CommonConst.ZERO);
    }

    /**
     * 根据波次策略条件查询发货单
     *
     * @param callerInfo   租户
     * @param waveStrategy 波次策略
     * @return 发货单
     * @throws Exception 异常信息
     */
    public List<Long> listByWaveStrategy(CallerInfo callerInfo, WaveStrategyDTO waveStrategy) throws Exception {
        logger.info("### 根据波次策略查询发货单, 波次策略ID-->{},波次号-->{}", waveStrategy.getWmsWaveId(), waveStrategy.getWmsWaveNo());
        StrategyQueryBuilder<WaveStrategyDTO, WmsOrderByStrategyQueryDTO> strategyQueryBuilder = context.getBean(
                WmsStrategyWmsOrderQueryBuilder.class);
        // 构建查询发货单的波次策略对象
        WmsOrderByStrategyQueryDTO query = strategyQueryBuilder
                .init(waveStrategy)
                .buildSkuRelated(waveStrategy)
                .buildWarehouseRelated(waveStrategy)
                .buildOrderRelated(waveStrategy)
                .build();
        logger.info("### 构造出来的查询条件-->{}", query);

        List<Long> wmsOrderIds;
        if (CollectionUtils.isEmpty(wmsOrderIds = fetchByStrategy(query))){
            logger.info("### 波次策略ID-->{},波次号-->{}查询到0条发货单", waveStrategy.getWmsWaveId(), waveStrategy.getWmsWaveNo());
            return new ArrayList<>();
        }

        // 将发货单id打包成一个一个的批次处理
        logger.info("batchSize===={}", batchSize);
        List<List<Long>> wmsOrderIdBatch = ListUtil.batchList(wmsOrderIds, batchSize);
        CopyOnWriteArrayList<Long> copyOnWriteArrayList = new CopyOnWriteArrayList();
        CountDownLatch countDownLatch = new CountDownLatch(wmsOrderIdBatch.size());
        // 这里加同步锁的原因做线程池资源的隔离
        synchronized (this) {
            long startTime = System.currentTimeMillis();
            logger.info("### 开始查询每一个发货单的锁库");
            for (List<Long> everyBatch : wmsOrderIdBatch) {
                waveThreadPoolTaskExecutor.submit(new CalculateHasStockWmsOrderThread(
                        everyBatch, query, cloudStockLockQueryService, countDownLatch, copyOnWriteArrayList, callerInfo));
            }
            countDownLatch.await();
            logger.info("### 波次策略ID-->{},波次号-->{}查询锁库结束，耗时：{}ms",
                    waveStrategy.getWmsWaveId(), waveStrategy.getWmsWaveNo(), System.currentTimeMillis() - startTime);
        }

        return copyOnWriteArrayList;
    }

    /**
     * 根据波次策略查询满足波次策略的发货单Id
     * @param query 波次策略
     * @return 发货单id
     */
    private List<Long> fetchByStrategy(WmsOrderByStrategyQueryDTO query){
        long current = System.currentTimeMillis();
        // 根据波次策略不考虑库区、货架、库位 查询出来的发货单id会很多
        List<Long> wmsOrderIds = wmsOrderMapper.listPrimaryKeyByWaveStrategy(query);
        logger.info("### 查询发货单{}条,耗时:{}ms", CollectionUtils.isEmpty(wmsOrderIds) ? CommonConst.ZERO : wmsOrderIds.size(),
                System.currentTimeMillis() - current);

        return wmsOrderIds;
    }

    /**
     * OMS挂起发货单
     *
     * @param messageVO
     * @return
     * @throws Exception
     */
    public Boolean stopDeliveryOrder(OmsToWmsStopOrderMessageVO messageVO) throws Exception {
        logger.info("stop wms order msg, saleOrderId-->{}, operatorId-->{}",
                messageVO.getSaleOrderId(), messageVO.getOperatorId());
        long start = System.currentTimeMillis();

        List<WmsOrderDTO> wmsOrderDTOS = listBySaleOrderId(messageVO.getSaleOrderId());

        if (CollectionUtils.isEmpty(wmsOrderDTOS)) {
            logger.info("stop wms order select result is null, messageVO:{}", messageVO);
            throw new BizException(WMS_ORDER_NULL_BY_SALE_ORDER_ID_EXCEPTION.getCode(),
                    WMS_ORDER_NULL_BY_SALE_ORDER_ID_EXCEPTION.getException());
        }

        WmsOrderDTO wmsOrderDTO = wmsOrderDTOS.get(CommonConst.ZERO);

        // TODO 加锁

        WmsOrderDTO stop = new WmsOrderDTO();
        stop.setId(wmsOrderDTO.getId());
        stop.setStopState(WmsOrderStopStateEnum.ON_HOOK.getCode().shortValue());

        wmsOrderMapper.updateByPrimaryKeySelective(stop.clone(WmsOrderDO.class));
        logger.info("stop wms order msg end-->{}ms", System.currentTimeMillis() - start);
        bizLogger.recordLog(CommonConst.DEFAULT_CREATOR_LONG, wmsOrderDTO.getTenantId(), LogBizModuleTypeEnum.WMS_ORDER,
                LogBizOperationTypeEnum.WMS_ORDER_ON_HOOK, BizOperationResultEnum.SUCCESSFUL.getCode(), wmsOrderDTO.getId(),
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());

        return true;
    }

    /**
     * 根据订单ID查询发货单
     * 只查询为挂起 未作废的
     *
     * @param saleOrderId 订单ID
     * @return 发货单
     * @throws Exception 异常信息
     */
    public List<WmsOrderDTO> listBySaleOrderId(Long saleOrderId) throws Exception {
        WmsOrderDTO selectParam = new WmsOrderDTO();
        selectParam.setCancel(Boolean.FALSE);
        selectParam.setStopState(WmsOrderStopStateEnum.NORMAL.getCode().shortValue());
        selectParam.setSaleOrderId(saleOrderId);
        selectParam.setUsable(Boolean.TRUE);
        List<WmsOrderDTO> wmsOrderDOS = ObjectUtils.convertList(wmsOrderMapper.listByAll(
                selectParam.clone(WmsOrderDO.class)), WmsOrderDTO.class);
        logger.debug("根据订单id查询发货单{}", wmsOrderDOS.toString());
        return wmsOrderDOS;
    }

    /**
     * 根据发货单ID更新发货单非空字段
     *
     * @param wmsOrderDTO 发货单
     * @return 更新结果
     * @throws Exception
     */
    public Boolean updateByPrimaryKeySelective(WmsOrderDTO wmsOrderDTO) throws Exception {
        return wmsOrderMapper.updateByPrimaryKeySelective(wmsOrderDTO.clone(WmsOrderDO.class)) > CommonConst.ZERO;
    }

    /**
     * 根据主键ID更新发货单非空字段
     *
     * @param wmsOrderDTO 发货单
     * @return 更新结果
     * @throws Exception
     */
    public Boolean updateByPrimaryKey(WmsOrderDTO wmsOrderDTO) throws Exception {
        return wmsOrderMapper.updateByPrimaryKey(wmsOrderDTO.clone(WmsOrderDO.class)) > CommonConst.ZERO;
    }

    /**
     * 通知OMS 发货单的业务和业务状态 业务时间
     *
     * @param informOMSOrderVO 业务操作类型和状态
     * @return 消费结果
     */
    public void informOmsCourierNo(InformOMSOrderVO informOMSOrderVO) throws Exception {
        logger.info("callOmsOrderServiceOrderState:{}", informOMSOrderVO);

        WmsOrderDTO wmsOrder = this.findBySaleOrderId(informOMSOrderVO.getSaleOrderId());
        if (null == wmsOrder) {
            logger.error("### CALL BACK OMS , search wms order is null, saleOrderId is :{}", informOMSOrderVO.getSaleOrderId());
            throw new BizException(WMS_ORDER_NULL_BY_SALE_ORDER_ID_EXCEPTION.getCode(),
                    WMS_ORDER_NULL_BY_SALE_ORDER_ID_EXCEPTION.getException());
        }
        // 必填项
        informOMSOrderVO.setTenantId(wmsOrder.getOwnerId());
        // 回写订单状态
        logger.info("异步通知om发货单的物流单号");
        threadPoolTaskExecutor.submit(new InformOmsOrderStateRunnable(informOMSOrderVO, apiRestConfig.getOms() + informOmsStateUrl));
    }

    /**
     * 打包数据
     *
     * @param wmsOrders 发货单
     * @param callerInfo
     * @return 发货单
     */
    private List<WmsOrderVO> packageData(List<WmsOrderDTO> wmsOrders, CallerInfo callerInfo) {
        Set<Long> warehouseIdSet = wmsOrders.stream().map(WmsOrderDTO::getWarehouseId).collect(Collectors.toSet());
        Map<Long, Warehouse> warehouseMap = warehouseService.findByIds(callerInfo, warehouseIdSet);
        logger.info("select warehouse by id:{}, result is {}", warehouseIdSet, warehouseMap);

        Set<Long> ownerIdSet = wmsOrders.stream().map(WmsOrderDTO::getOwnerId).collect(Collectors.toSet());
        List<CloudOwnerWarehouseBind> binds = cloudOwnerWarehouseBindService.listByOwnerIds(callerInfo, ownerIdSet);
        logger.info("select owner by ownerId:{}, result is {}", ownerIdSet, binds);

        List<WmsOrderVO> wmsOrderVOS = new ArrayList<>(wmsOrders.size());
        Set<Long> courierIdSet = wmsOrders.stream().filter(s -> null != s.getCourierId()).map(WmsOrderDTO::getCourierId)
                .collect(Collectors.toSet());
        Map<Long, Courier> courierMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(courierIdSet)) {
            courierMap = courierService.findByIds(callerInfo, courierIdSet);
        }

        for (WmsOrderDTO wmsOrderDTO : wmsOrders) {
            Warehouse warehouse = warehouseMap.get(wmsOrderDTO.getWarehouseId());
            WmsOrderVO wmsOrderVO = new WmsOrderVO();
            String ownerName = CommonConst.EMPTY_STR;
            for (CloudOwnerWarehouseBind bind : binds) {
                if (null != wmsOrderDTO.getOwnerId() && wmsOrderDTO.getOwnerId().equals(bind.getOwnerId())) {
                    ownerName = bind.getOwnerName();
                    break;
                }
            }

            BeanCopierUtils.copyProperties(wmsOrderDTO, wmsOrderVO);
            wmsOrderVO.setStopMark(WmsOrderStopEnum.text(wmsOrderDTO.getStopState().intValue()));
            wmsOrderVO.setCancelMark(WmsOrderCancelEnum.text(wmsOrderDTO.getCancel()));
            wmsOrderVO.setPrintedMark(WmsOrderCourierPrintMarkEnum.text(wmsOrderDTO.getCourierPrintMarkState()));
            wmsOrderVO.setExamineGoodsState(WmsOrderExamineGoodsEnum.value(wmsOrderDTO.getExamineGoods()));
            wmsOrderVO.setPrescriptionType(WmsOrderPrescriptionTypeEnum.desc(wmsOrderDTO.getPrescriptionType().intValue()));
            wmsOrderVO.setWarehouseName(null != warehouse ? warehouse.getWarehouseName() : CommonConst.EMPTY_STR);
            wmsOrderVO.setSaleOrderOriginType(SaleOrderOriginTypeEnum.type(wmsOrderDTO.getSaleOrderOriginType().intValue()));
            wmsOrderVO.setWave(WmsOrderWaveStateEnum.text(wmsOrderDTO.getWave()));
            wmsOrderVO.setCourierPrintMarkStateStr(WmsOrderCourierPrintStateEnum.txt(
                    wmsOrderDTO.getCourierPrintMarkState()));
            wmsOrderVO.setCancel(wmsOrderDTO.getCancel() ? CancelStateEnum.CANCEL.getText()
                    : CancelStateEnum.NORMAL.getText());
            wmsOrderVO.setWmsOrderState(WmsOrderStateEnum.state(wmsOrderDTO.getWmsOrderState().intValue()));
            wmsOrderVO.setOwnerName(ownerName);
            wmsOrderVO.setCourierNo(wmsOrderDTO.getWaybillCode());
            wmsOrderVO.setCourierName(null == courierMap.get(wmsOrderDTO.getCourierId())
                    ? CommonConst.EMPTY_STR
                    : courierMap.get(wmsOrderDTO.getCourierId()).getCourierName());
            wmsOrderVO.setOrderType(OmsOrderTypeEnum.text(wmsOrderDTO.getOrderType().intValue()));

            wmsOrderVOS.add(wmsOrderVO);
        }

        return wmsOrderVOS;
    }

    /**
     * 根据发货单表的所有字段作为查询条件非空判断查询
     *
     * @param queryCondition 查询条件
     * @return 发货单
     * @throws Exception 异常信息
     */
    public List<WmsOrderDTO> listByAllProperties(WmsOrderDTO queryCondition) throws Exception {
        return ObjectUtils.convertList(wmsOrderMapper.listByAll(queryCondition.clone(WmsOrderDO.class)), WmsOrderDTO.class);
    }

    /**
     * 通过快递单号查询发货单信息
     *
     * @param callerInfo  租户ID
     * @param wayBillCode 快递单号
     * @return 发货单信息
     */
    public WmsOrderDTO findOrderByWayBillNo(CallerInfo callerInfo, String wayBillCode) {

        WmsOrderDO wmsOrderDO = wmsOrderMapper.findOrderByWayBillNo(callerInfo.getTenantId(), wayBillCode);
        return EntityConverter.copyAndGetSingle(wmsOrderDO, WmsOrderDTO.class);

    }

    /**
     * 通过发货单ID查找特定的发货单记录
     *
     * @param callerInfo 租户ID
     * @param wmsOrderId 发货单ID
     * @return 发货单信息
     */
    public WmsOrderDTO selectOneOrderByPrimaryKey(CallerInfo callerInfo, Long wmsOrderId) {

        WmsOrderDO wmsOrderDO = wmsOrderMapper.selectOneOrderByPrimaryKey(callerInfo.getTenantId(), wmsOrderId);
        return EntityConverter.copyAndGetSingle(wmsOrderDO, WmsOrderDTO.class);
    }

    /**
     * 通过发货单ids更换快递公司
     *
     * @param callerInfo 租户ID
     * @param vo         发货单ID和快递编号信息
     * @return 发货单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public List<OrderResultVO> exchangeCourierInfo(CallerInfo callerInfo, WaybillCourierForwardVO vo, WaybillService waybillService) throws Exception {

        List<OrderResultVO> result = new ArrayList<>();
        String wmsOrderId = vo.getEntityIds();
        Set<Long> wmsOrderIdsSet = SplitUtils.parseCommaStr2Set(wmsOrderId);
        List<WmsOrderDTO> wmsOrderDTOList = this.listByPrimaryKeys(wmsOrderIdsSet);
        Set<Long> idsSelect = getWmsOrderIdList(wmsOrderDTOList);

        //处理差集
        Set<Long> idsDif = wmsOrderIdsSet.stream().filter(obj -> !idsSelect.contains(obj)).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(idsDif)) {
            for (Long wmsId : idsDif) {
                OrderResultVO orderResultVO = new OrderResultVO();
                orderResultVO.setOrderId(String.valueOf(wmsId));
                orderResultVO.setOrderNo(String.valueOf(wmsId));
                orderResultVO.setOperationResult("未查询到发货单");
                result.add(orderResultVO);
            }
        }

        //处理交集
//        Set<Long> idsList = wmsOrderIdsSet.stream().filter(idsSelect::contains).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(wmsOrderDTOList)) {
            for (WmsOrderDTO wmsOrderDTO : wmsOrderDTOList) {
                //判断发货单是否已经打印，如果打印则提示错误信息
                if (judgePrintStateById(wmsOrderDTO)) {
                    OrderResultVO orderResultVO = new OrderResultVO();
                    orderResultVO.setOrderId(String.valueOf(wmsOrderDTO.getId()));
                    orderResultVO.setOrderNo(wmsOrderDTO.getWmsOrderNo());
                    orderResultVO.setOperationResult("已打印的发货单不能修改快递，请至打包发货界面处理");
                    result.add(orderResultVO);
                } else {
                    //修改发货单的快递公司
                    updateCourierInfo(callerInfo, vo, result, wmsOrderDTO.getId(), wmsOrderDTO, waybillService);
                }
            }
        }
        return result;
    }

    public void updateCourierInfo(CallerInfo callerInfo, WaybillCourierForwardVO vo, List<OrderResultVO> result, Long id, WmsOrderDTO wmsOrderDTO, WaybillService waybillService) throws Exception {
        Courier courierNew = courierService.findById(callerInfo.getTenantId(), vo.getCourierId());
        if (vo.getCourierId().equals(wmsOrderDTO.getCourierId())) {
            OrderResultVO orderResultVO = new OrderResultVO();
            orderResultVO.setOrderId(String.valueOf(id));
            orderResultVO.setOrderNo(wmsOrderDTO.getWmsOrderNo());
            orderResultVO.setOperationResult("快递公司相同不能修改");
            result.add(orderResultVO);
        } else {
            wmsOrderDTO.setCourierId(vo.getCourierId());
            wmsOrderDTO.setCourierName(courierNew.getCourierName());
            wmsOrderDTO.setCpCode(courierNew.getCpCode());
            wmsOrderDTO.setWaybillCode(CommonConst.EMPTY_STR);
            //更新主键
            Integer i = wmsOrderMapper.updateByPrimaryKeySelective(wmsOrderDTO.clone(WmsOrderDO.class));
            if (!CommonConst.ZERO.equals(i)) {
                OrderResultVO orderResultVO = new OrderResultVO();
                orderResultVO.setOrderId(String.valueOf(id));
                orderResultVO.setOrderNo(wmsOrderDTO.getWmsOrderNo());
                result.add(orderResultVO);
                bizLogger.recordLog(callerInfo.getOperatorId(), wmsOrderDTO.getTenantId(), LogBizModuleTypeEnum.WMS_ORDER,
                        LogBizOperationTypeEnum.WMS_ORDER_CHANGE_COURIER, BizOperationResultEnum.SUCCESSFUL.getCode(), wmsOrderDTO.getId(),
                        CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());
                //启动异步调用
                threadPoolTaskExecutor.submit(() -> {
                    try {
                        waybillService.getWaybill(callerInfo, wmsOrderDTO, 3, wmsOrderDTO.getCpCode());
                    } catch (Exception e) {
                        logger.warn("获取电子面单失败", e);
                    }
                });
            } else {
                OrderResultVO orderResultVO = new OrderResultVO();
                orderResultVO.setOrderId(String.valueOf(id));
                orderResultVO.setOrderNo(wmsOrderDTO.getWmsOrderNo());
                orderResultVO.setOperationResult("更新快递公司失败");
                result.add(orderResultVO);
            }
        }
    }

    private boolean judgePrintStateById(WmsOrderDTO wmsOrderDTO) {
        Integer printState = wmsOrderDTO.getCourierPrintMarkState();
        return WmsOrderDeliverPrintStateEnum.PRINTED.getCode().equals(printState);
    }

    private Set<Long> getWmsOrderIdList(List<WmsOrderDTO> wmsOrderDOList) {
        Set<Long> idsSelect = new HashSet<>();
        for (WmsOrderDTO wmsOrderDTO : wmsOrderDOList) {
            if (!CollectionUtils.isEmpty(wmsOrderDOList)) {
                idsSelect.add(wmsOrderDTO.getId());
            }
        }
        return idsSelect;
    }


    /**
     * 验货更新发货单状态 异步同步订单状态
     *
     * @param callerInfo  调用方
     * @param wmsOrderDTO 发货单
     * @return 更新结果
     * @throws Exception 异常信息
     */
    public Boolean updateWmsOrderByCheckSuccess(CallerInfo callerInfo, WmsOrderDTO wmsOrderDTO) throws Exception {
        // 对发货单状态进行更新,并扣减库存
        orderStateDelegator.changeOrderState(callerInfo, wmsOrderDTO, WmsOrderStateEnum.EXAMINE_GOODS, Boolean.TRUE);
        int i = wmsOrderMapper.updateByPrimaryKeySelective(wmsOrderDTO.clone(WmsOrderDO.class));
        bizLogger.recordLog(callerInfo.getOperatorId(), wmsOrderDTO.getTenantId(), LogBizModuleTypeEnum.WMS_ORDER,
                LogBizOperationTypeEnum.WMS_ORDER_EXAMINE_GOODS, BizOperationResultEnum.SUCCESSFUL.getCode(), wmsOrderDTO.getId(),
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());
        // 异步通知OMS
        InformOMSOrderVO informOMSOrderVO = new InformOMSOrderVO();
        informOMSOrderVO.setOrderState(WmsOrderStateEnum.EXAMINE_GOODS.getCode());
        informOMSOrderVO.setSaleOrderId(wmsOrderDTO.getSaleOrderId());
        informOMSOrderVO.setServiceTime(wmsOrderDTO.getExamineGoodsTime());
        informOMSOrderVO.setTenantId(wmsOrderDTO.getTenantId());
        informOMSOrderVO.setOperationType(WmsOrderStateEnum.WEIGHT_GOODS.getCode());
        threadPoolTaskExecutor.submit(new InformOmsOrderStateRunnable(informOMSOrderVO, apiRestConfig.getOms() + informOmsStateUrl));
        return i >= 1;
    }

    /**
     * 根据多个主键进行查询
     *
     * @param wmsOrderIds 发货单ID
     * @return 发货单
     * @throws Exception 异常信息
     */
    public List<WmsOrderDTO> listByPrimaryKeys(Set<Long> wmsOrderIds) throws Exception {
        return ObjectUtils.convertList(wmsOrderMapper.selectByPrimaryKeys(wmsOrderIds), WmsOrderDTO.class);
    }

    /**
     * 批量更新根据主键
     *
     * @param updates 发货单
     * @return 更新结果
     * @throws Exception
     */
    public Boolean batchUpdateSelectiveByPrimaryKey(List<WmsOrderDTO> updates) throws Exception {
        int result = wmsOrderMapper.updateBatchSelective(ObjectUtils.convertList(updates, WmsOrderDO.class));
        return result > CommonConst.ZERO;
    }

    /**
     * 发货单创建伯茨以后更新发货单的状态以及波次号 、波次时间、捡货单ID 、捡货单编号 以及通知OMS状态
     * @param caller    登陆用户
     * @param updates   待更新的发货单
     * @param detailUpdates
     * @return  更新结果
     * @throws Exception 异常信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean createWaveUpdateWmsOrderInfo(CallerInfo caller,
                                                List<WmsOrderDTO> updates,
                                                List<WmsOrderDetailDTO> detailUpdates) throws Exception {
        if (CollectionUtils.isEmpty(updates) || CollectionUtils.isEmpty(detailUpdates)){
            logger.info("### 发货单创建波次，发货单集合数量为空：{}, {}", updates.isEmpty(), detailUpdates.isEmpty());
            return false;
        }
        // 更新发货单进入波次的状态等字段
        this.batchUpdateSelectiveByPrimaryKey(updates);
        // 更新发货单明细进入波次状态
        wmsOrderDetailDomService.batchUpdateSelectiveByPrimaryKey(detailUpdates);
        orderStateManager.createWave(caller, updates);
        Long tenantId = updates.get(CommonConst.ZERO).getTenantId();
        Set<Long> wmsOrderIds = updates.stream().map(WmsOrderDTO::getId).collect(Collectors.toSet());

        // 通知OMS发货单状态--分拣
        logger.info("### 发货单创建波次，更新发货单和发货单明细结束，开始同步状态给OMS");
        for (WmsOrderDTO wmsOrderDTO : updates){
            InformOMSOrderVO informOMSOrderVO = new InformOMSOrderVO();
            informOMSOrderVO.setSaleOrderId(wmsOrderDTO.getSaleOrderId());
            informOMSOrderVO.setOrderState(WmsOrderStateEnum.CREATED_WMS_WAVE.getCode());
            informOMSOrderVO.setServiceTime(dateProvider.getCurrentTime());
            informOMSOrderVO.setTenantId(wmsOrderDTO.getTenantId());
            informOMSOrderVO.setOperationType(WmsOrderStateEnum.CREATED_WMS_WAVE.getCode());

            threadPoolTaskExecutor.submit(new InformOmsOrderStateRunnable(informOMSOrderVO, apiRestConfig.getOms() + informOmsStateUrl));
        }


        bizLogger.batchRecordLog(caller.getOperatorId(), tenantId, LogBizModuleTypeEnum.WMS_ORDER,
                LogBizOperationTypeEnum.WMS_ORDER_WAVE, BizOperationResultEnum.SUCCESSFUL.getCode(),wmsOrderIds,
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());
        return true;
    }

    private WmsOrderReceiverDTO buildReceiverInfo(OmsToWmsCreateOrderMessageVO messageVO,
                                                  WmsOrderDTO wmsOrderDTO) {
        WmsOrderReceiverDTO wmsOrderReceiverDTO = new WmsOrderReceiverDTO();
        BeanCopierUtils.copyProperties(messageVO, wmsOrderReceiverDTO);
        BeanCopierUtils.copyProperties(wmsOrderDTO, wmsOrderReceiverDTO);
        wmsOrderReceiverDTO.setId(idGenerator.generate(SysTableEnum.WMS_ORDER_RECEIVER.getTable()));
        wmsOrderReceiverDTO.setWmsOrderId(wmsOrderDTO.getId());
        return wmsOrderReceiverDTO;
    }

    /**
     * 根据发货单和发货单明细创建锁库对象
     *
     * @param wmsOrderDTO
     * @return
     */
    public static List<EntityStockLockBo> createLockStock(WmsOrderDTO wmsOrderDTO) {
        List<WmsOrderDetailDTO> details = wmsOrderDTO.getDetails();
        List<EntityStockLockBo> result = new ArrayList<>(details.size());
        for (WmsOrderDetailDTO detail : details) {
            EntityStockLockBo entityStockLockBo = new EntityStockLockBo();
            entityStockLockBo.setSaleOrderId(wmsOrderDTO.getSaleOrderId());
            entityStockLockBo.setWmsOrderId(wmsOrderDTO.getId());
            entityStockLockBo.setSaleOrderDetailId(detail.getSaleOrderDetailId());
            entityStockLockBo.setWmsOrderDetailId(detail.getId());
            result.add(entityStockLockBo);
        }
        return result;
    }

    /**
     * 分拣更新发货单和发货单明细
     *
     * @param caller   分拣人
     * @param wmsOrder 发货单
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateSort(CallerInfo caller, WmsOrderDTO wmsOrder) throws Exception {
        logger.info("end sort start update wms order:{}######{}", caller, wmsOrder);
        Short wmsOrderType = wmsOrder.getWmsOrderType();
        Boolean needCheck = WmsOrderTypeEnum.MULTIPLY.getCode().equals(wmsOrderType.intValue());
        logger.info("needCheck:{}", needCheck);
        //更新发货单状态
        if (WmsOrderTypeEnum.SINGLE.getCode().equals(wmsOrderType.intValue())) {
            orderStateDelegator.changeOrderState(caller, wmsOrder, WmsOrderStateEnum.PRINTED_COURIER, needCheck);
        } else {
            // 多件的
            if (WmsOrderStateEnum.SORT_ORDER.getCode().equals(wmsOrder.getNextWmsOrderState())) {
                orderStateDelegator.changeOrderState(caller, wmsOrder, WmsOrderStateEnum.SORT_ORDER, needCheck);
            }
        }

        wmsOrder.setSortedTime(dateProvider.getCurrentTime());
        wmsOrder.setLastUpdateTime(dateProvider.getCurrentTime());
        wmsOrder.setLastUpdater(caller.getOperatorId());
        // 更新发货单的分拣时间
        wmsOrderMapper.updateByPrimaryKeySelective(wmsOrder.clone(WmsOrderDO.class));

        // 如果是单件 直接将发货单的分拣状态改为已完成
        WmsOrderDetailDTO wmsOrderDetail = wmsOrder.getWmsOrderDetailDTO();
        wmsOrderDetail.setLastUpdateTime(dateProvider.getCurrentTime());
        wmsOrderDetail.setLastUpdater(caller.getOperatorId());
        wmsOrderDetailMapper.updateByPrimaryKeySelective(wmsOrderDetail.clone(WmsOrderDetailDO.class));

        // 记录发货单操作记录
        WmsOrderOperationDTO operation = WmsOrderDTO.operation(WmsOrderStateEnum.SORT_ORDER.getCode(), wmsOrder, null);
        wmsOrderOperationDomService.saveOperation(caller, operation);

        // 异步通知OMS订单状态
        InformOMSOrderVO informOMSOrderVO = new InformOMSOrderVO();
        informOMSOrderVO.setSaleOrderId(wmsOrder.getSaleOrderId());
        informOMSOrderVO.setTenantId(wmsOrder.getTenantId());
        informOMSOrderVO.setOrderState(WmsOrderStateEnum.SORT_ORDER.getCode());
        informOMSOrderVO.setOperationType(WmsOrderStateEnum.SORT_ORDER.getCode());
        informOMSOrderVO.setServiceTime(wmsOrder.getSortedTime());
        threadPoolTaskExecutor.submit(new InformOmsOrderStateRunnable(informOMSOrderVO, apiRestConfig.getOms() + informOmsStateUrl));

        bizLogger.recordLog(caller.getOperatorId(), wmsOrder.getTenantId(), LogBizModuleTypeEnum.WMS_ORDER,
                LogBizOperationTypeEnum.WMS_ORDER_EXAMINE_GOODS, BizOperationResultEnum.SUCCESSFUL.getCode(), wmsOrder.getId(),
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());
    }


    /**
     * 更新打印状态
     *
     * @param caller    租户
     * @param wmsOrders 发货单
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdatePrint(CallerInfo caller, List<WmsOrderDTO> wmsOrders) throws Exception {
        logger.info("开始更新发货单的打印状态......{}", wmsOrders);
//        // 更新发货单的状态为待验货（已打印）
        if (CollectionUtils.isEmpty(wmsOrders)) {
            logger.error("更新发货单的打印状态发货单为空");
            return;
        }

        List<WmsOrderDTO> wmsOrderDOS = this.listByPrimaryKeys(
                wmsOrders.stream().map(WmsOrderDTO::getId).collect(Collectors.toSet()));

        Long tenantId = wmsOrderDOS.get(CommonConst.ZERO).getTenantId();
        Set<Long> wmsOrderIds = wmsOrderDOS.stream().map(WmsOrderDTO::getId).collect(Collectors.toSet());

        // 更改发货单状态
        orderStateDelegator.batchChangeOrderState(caller, wmsOrders, WmsOrderStateEnum.PRINTED_COURIER, Boolean.FALSE);
        // 更新发货单
        wmsOrderMapper.updateBatchSelective(ObjectUtils.convertList(wmsOrders, WmsOrderDO.class));

        List<WmsOrderOperationDTO> operations = new LinkedList<>();

        for (WmsOrderDTO wmsOrderDTO : wmsOrderDOS) {
            WmsOrderOperationDTO operation = WmsOrderDTO.operation(
                    WmsOperationEnum.ORDER_PRINT_WAYBILL.getCode(), wmsOrderDTO, null);
            operations.add(operation);
        }

        wmsOrderOperationDomService.batchSaveOperation(caller, operations);
        bizLogger.batchRecordLog(caller.getOperatorId(), tenantId, LogBizModuleTypeEnum.WMS_ORDER,
               LogBizOperationTypeEnum.WMS_ORDER_WAVE, BizOperationResultEnum.SUCCESSFUL.getCode(),wmsOrderIds,
                CommonConst.DEFAULT_CREATOR_USER_NAME, false, false, dateProvider.getCurrentTime());
        logger.info("### 打印更新发货单状态完毕，wmsOrders==={}", wmsOrders);
    }


    /**
     * 发货单重新锁库
     *
     * @param saleOrderIds 订单ID
     * @param tenantId
     * @return 是否重新锁库成功
     */
    public void reLockStock(Set<Long> saleOrderIds, Long tenantId) throws Exception {
        logger.info("######wms order reLock stock start:{},{}", saleOrderIds, tenantId);

        if (CollectionUtils.isEmpty(saleOrderIds)) {
            logger.error("reLock error reason is sale order id is null");
            return;
        }

        List<WmsOrderDTO> wmsOrders = ObjectUtils.convertList(wmsOrderMapper
                .listInSaleOrderIdsAndTenantId(saleOrderIds, tenantId), WmsOrderDTO.class);
        if (CollectionUtils.isEmpty(wmsOrders)) {
            logger.error("wms order reLock stock error because select wms order is null");
            return;
        }

        List<WmsOrderDetailDTO> details = ObjectUtils.convertList(wmsOrderDetailMapper.listByWmsOrderIds(
                wmsOrders.stream().map(WmsOrderDTO::getId).collect(Collectors.toSet())), WmsOrderDetailDTO.class);
        if (CollectionUtils.isEmpty(details)) {
            logger.error("wms order reLock stock error because select wms order detail is null");
            return;
        }

        Map<Long, List<WmsOrderDetailDTO>> wmsOrderIdToDetailListMap = details.stream()
                .collect(Collectors.groupingBy(WmsOrderDetailDTO::getWmsOrderId));

        for (WmsOrderDTO wmsOrder : wmsOrders) {
            List<WmsOrderDetailDTO> collect = details.stream().filter(
                    s -> wmsOrder.getId().equals(s.getWmsOrderId())).collect(Collectors.toList());
            wmsOrder.setDetails(collect);
            List<EntityStockLockBo> lockStock = createLockStock(wmsOrder);

            CallerInfo callerInfo = new CallerInfo(tenantId, CommonConst.DEFAULT_CREATOR_LONG);

            logger.info("create delivery stock entity lock param is:{},{}", tenantId, lockStock);
            InvokeApi<List<EntityStockLockDto>> invokeApi = cloudStockLockQueryService.querySaleStockLockBySaleOrderIds(
                    callerInfo, Sets.newHashSet(wmsOrder.getSaleOrderId()), wmsOrder.getWarehouseId());
            List<EntityStockLockDto> data = invokeApi.getData();
            logger.info("stock entity lock end ,result is {},{},{}",
                    invokeApi.getSuccess(), data, invokeApi.getErrorMsg());

            calculateAndDetermineOrderType(wmsOrderIdToDetailListMap, data, wmsOrder);

            WmsOrderDTO update = new WmsOrderDTO();
            update.setId(wmsOrder.getId());
            update.setOrderType(wmsOrder.getOrderType());
            update.setTenantId(wmsOrder.getTenantId());
            update.setSaleOrderId(wmsOrder.getSaleOrderId());

            logger.info("更新重算发货单锁库开始.....{}", update);
            wmsOrderMapper.updateByPrimaryKeySelective(update.clone(WmsOrderDO.class));

            WmsOrderOperationDTO operation = WmsOrderDTO.operation(
                    WmsOperationEnum.RE_CALCULATE_STOCK.getCode(), wmsOrder, CommonConst.EMPTY_STR);
            wmsOrderOperationDomService.saveOperation(callerInfo, operation);
            logger.info("更新重算发货单锁库结束....{}", update);
        }

    }

    /**
     * 计算发货单锁库数量以及设置发货单是缺货还是正常状态
     *
     * @param wmsOrderIdToDetailListMap 发货单明细的map
     * @param wmsOrderLockedList        锁库数据的data. 其中 的发货单id实际上是订单id , 发货单明细id实际上是订单明细id
     * @param wmsOrder                  发货单
     */
    public static void calculateAndDetermineOrderType(Map<Long, List<WmsOrderDetailDTO>> wmsOrderIdToDetailListMap,
                                                      List<EntityStockLockDto> wmsOrderLockedList,
                                                      WmsOrderDTO wmsOrder) {
        logger.info("开始转正常转缺货");

        for (Map.Entry<Long, List<WmsOrderDetailDTO>> map : wmsOrderIdToDetailListMap.entrySet()) {
            List<WmsOrderDetailDTO> wmsOrderDetails = map.getValue();
            if (!wmsOrder.getId().equals(map.getKey())){
                // 只要这个发货单的明细
                continue;
            }
            logger.info("得到当前发货单的明细发货单ID:{},订单ID:{},明细：{}",
                    wmsOrder.getId(), wmsOrder.getSaleOrderId(), wmsOrderDetails);
            // 得到当前发货单的所有明细
            boolean normal = true;
            for (WmsOrderDetailDTO detail : wmsOrderDetails) {
                int lockedNumber = wmsOrderLockedList.stream()
                        .filter(s -> null != s.getNumber()
                                && null != s.getWmsOrderDetailId()
                                && null != s.getSkuId()
                                // 这里的可销锁的发货单明细id 实际上是订单明细id，所以这里需要和发货单明细的订单明细id做equals
                                && s.getWmsOrderDetailId().equals(detail.getSaleOrderDetailId())
                                && s.getSkuId().equals(detail.getSkuId()))
                        .mapToInt(EntityStockLockDto::getNumber).sum();
                logger.info("订单明细ID{}锁库数量为{},需要数量为：{}", detail.getSaleOrderDetailId(), lockedNumber, detail.getNum());
                if (lockedNumber < detail.getNum()) {
                    // 如果有一条发货单明细缺货 整个发货单缺货 直接内循环break 设置发货单为缺货状态
                    normal = false;
                    logger.info("wms order lock entity stock failed:{}=={}", wmsOrder.getSaleOrderId(), wmsOrder.getId());
                    break;
                }
            }
            logger.info("当前发货单ID{}订单ID{}转正常结束，结果为转{}",
                    wmsOrder.getId(), wmsOrder.getSaleOrderId(), normal ? "正常" : "缺货" );

            wmsOrder.setOrderType(normal ? OmsOrderTypeEnum.NORMAL.getCode().shortValue()
                    : OmsOrderTypeEnum.OUT_OF_STOCK.getCode().shortValue());
        }
    }

    public List<Long> countPrintedOrderByIds(Long tenantId, Set<Long> wmsOrderIds) {
        return wmsOrderMapper.countPrintedOrderByIds(tenantId, wmsOrderIds);
    }

    public Integer queryStateCount(CallerInfo caller, Integer state, Integer type) {
        return wmsOrderMapper.queryStateCount(caller.getTenantId(), state, type);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRemoveWave(CallerInfo caller,
                                 List<WmsOrderDTO> wmsOrderDTOS,
                                 List<WmsOrderDetailDTO> orderDetails) throws Exception{
        logger.info("### 移除波次 更新发货单 发货单明细 发货单业务日志开始");

        batchUpdateSelectiveByPrimaryKey(wmsOrderDTOS);
        wmsOrderDetailMapper.updateBatchSelective(ObjectUtils.convertList(orderDetails, WmsOrderDetailDO.class));
        List<WmsOrderOperationDTO> wmsOrderOperationDTOS = new ArrayList<>(wmsOrderDTOS.size());
        for (WmsOrderDTO wmsOrder : wmsOrderDTOS){
            WmsOrderOperationDTO operation = WmsOrderDTO.operation(
                    WmsOperationEnum.ORDER_REMOVE_FROM_WAVE.getCode(), wmsOrder, CommonConst.EMPTY_STR);

            wmsOrderOperationDTOS.add(operation);
        }
        wmsOrderOperationDomService.batchSaveOperation(caller, wmsOrderOperationDTOS);
        logger.info("### 移除波次 更新发货单 发货单明细 发货单业务日志结束");
    }

    public void updatePick(CallerInfo callerInfo, List<WmsOrderDTO> wmsOrderDTOS) throws Exception {
        if (CollectionUtils.isEmpty(wmsOrderDTOS)){
            logger.error("### 捡货更新发货单，发货单为空，{},{}", callerInfo, wmsOrderDTOS);
            return;
        }
        wmsOrderMapper.updateBatchSelective(ObjectUtils.convertList(wmsOrderDTOS, WmsOrderDO.class));
        for (WmsOrderDTO wmsOrderDTO : wmsOrderDTOS){
            InformOMSOrderVO informOMSOrderVO = new InformOMSOrderVO();
            informOMSOrderVO.setOperationType(WmsOrderStateEnum.WMS_COMPLETE_PICKED.getCode());
            informOMSOrderVO.setSaleOrderId(wmsOrderDTO.getSaleOrderId());
            informOMSOrderVO.setOrderState(WmsOrderStateEnum.WMS_COMPLETE_PICKED.getCode());
            informOMSOrderVO.setServiceTime(dateProvider.getCurrentTime());
            informOMSOrderVO.setTenantId(wmsOrderDTO.getTenantId());
            threadPoolTaskExecutor.submit(new InformOmsOrderStateRunnable(informOMSOrderVO, apiRestConfig.getOms() + informOmsStateUrl));
        }
    }
}
