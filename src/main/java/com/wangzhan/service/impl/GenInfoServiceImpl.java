package com.wangzhan.service.impl;

import com.wangzhan.domain.GenInfoColumn;
import com.wangzhan.mapper.GenInfoMapper;
import com.wangzhan.service.IGenInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangzhan
 * @version 1.0
 * @description
 * @date 2023/7/21 11:32:32
 */
@Service
public class GenInfoServiceImpl implements IGenInfoService {

    @Resource
    private GenInfoMapper genInfoMapper;

    @Override
    public List<GenInfoColumn> selectDbTableColumnsByName(String tableName) {
        return genInfoMapper.selectDbTableColumnsByName(tableName);
    }
}
