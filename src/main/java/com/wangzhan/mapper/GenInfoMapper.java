package com.wangzhan.mapper;

import com.wangzhan.domain.GenInfoColumn;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wangzhan
 * @version 1.0
 * @description
 * @date 2023/7/21 11:18:12
 */
@Repository
@Mapper
public interface GenInfoMapper {

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    public List<GenInfoColumn> selectDbTableColumnsByName(String tableName);

}
