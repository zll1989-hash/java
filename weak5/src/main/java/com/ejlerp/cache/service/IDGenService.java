package com.ejlerp.cache.service;

import com.ejlerp.cache.common.Result;

/**
 * @author lucien
 */
public interface IDGenService {
    /**
     * 获取ID值
     *
     * @param key
     * @return
     */
    Result get(String key);

}
