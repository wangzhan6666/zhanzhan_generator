package com.wangzhan.controller;

import com.wangzhan.domain.GenInfo;
import com.wangzhan.domain.GenInfoColumn;
import com.wangzhan.service.IGenInfoService;
import com.wangzhan.utils.generator.GeneratorUtils;
import com.wangzhan.utils.result.AjaxResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/***
 * @description
    {
        "tableName": "user",
        "className": "user",
        "packageName": "com.wangzhan",
        "moduleName": "zhanzhan_generator",
        "functionName": "用户信息",
        "functionAuthor": "wangzhan",
        "genPath": "E:\\software\\IDEA\\save_zhanzhan\\springcloud_study"
    }
 *
 * @author wangzhan
 * @date 2023/7/22 19:46:53
 */

@RestController
@RequestMapping("/zhanzhan/generator")
public class GeneratorController {

    @Resource
    private IGenInfoService genInfoService;

    /***
     * @description 代码生成
     * @param genInfo
     * @return com.wangzhan.utils.result.AjaxResult
     * @author wangzhan
     * @date 2023/7/22 20:05:09
     */
    @PostMapping("/generatorCode")
    public AjaxResult generatorCode(@RequestBody GenInfo genInfo) {
        List<GenInfoColumn> genInfoColumns = genInfoService.selectDbTableColumnsByName(genInfo.getTableName());

        for (GenInfoColumn genInfoColumn : genInfoColumns) {
            GeneratorUtils.initColumnField(genInfoColumn);
        }

        genInfo.setColumns(genInfoColumns);

        GeneratorUtils.generatorCode(genInfo);
        return AjaxResult.success();
    }

}
