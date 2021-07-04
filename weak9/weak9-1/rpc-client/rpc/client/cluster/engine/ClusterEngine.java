package com.itcast.rpc.client.cluster.engine;

import com.google.common.collect.Maps;
import com.itcast.rpc.client.cluster.ClusterStrategy;
import com.itcast.rpc.client.cluster.ClusterStrategyEnum;
import com.itcast.rpc.client.cluster.impl.*;

import java.util.Map;

/**
 * 集群调用策略处理引擎
 */
public class ClusterEngine {

    private static final Map<ClusterStrategyEnum, ClusterStrategy> clusterStrategyMap = Maps.newConcurrentMap();

    static {
        clusterStrategyMap.put(ClusterStrategyEnum.RANDOM, new RandomClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.WEIGHT_RANDOM, new WeightRandomClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.POLLING, new PollingClusterStrategyImpl());
        clusterStrategyMap.put(ClusterStrategyEnum.HASH, new HashClusterStrategyImpl());
    }

    public static ClusterStrategy queryClusterStrategy(String clusterStrategy) {
        ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
        if (clusterStrategyEnum == null) {
            return new RandomClusterStrategyImpl();
        }
        return clusterStrategyMap.get(clusterStrategyEnum);
    }
}
