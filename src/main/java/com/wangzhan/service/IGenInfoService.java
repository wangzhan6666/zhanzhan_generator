package com.wangzhan.service;

import com.wangzhan.domain.GenInfoColumn;

import java.util.List;

/**
 * @author wangzhan
 * @version 1.0
 * @description
 * @date 2023/7/21 11:31:42
 */

public interface IGenInfoService {

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    public List<GenInfoColumn> selectDbTableColumnsByName(String tableName);

}
