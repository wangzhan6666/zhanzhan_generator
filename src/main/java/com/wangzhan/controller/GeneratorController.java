package com.wangzhan.controller;

import com.wangzhan.domain.GenInfo;
import com.wangzhan.domain.GenInfoColumn;
import com.wangzhan.service.IGenInfoService;
import com.wangzhan.utils.generator.GeneratorUtils;
import com.wangzhan.utils.result.AjaxResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public AjaxResult generatorCode(HttpServletResponse response, @RequestBody GenInfo genInfo) {
        // 根据表名查询该表的所有列数据
        List<GenInfoColumn> genInfoColumns = genInfoService.selectDbTableColumnsByName(genInfo.getTableName());

        for (GenInfoColumn genInfoColumn : genInfoColumns) {
            GeneratorUtils.initColumnField(genInfoColumn);
        }

        genInfo.setColumns(genInfoColumns);

        // 生成类型，路径
        GeneratorUtils.generatorCode(genInfo);
        return AjaxResult.success();
    }

    /**
     * @description TODO 为方便测试，GenInfo数据写死，通过浏览器来访问改地址测试下载代码功能，
     * @param response
     * @author wangzhan
     * @date 2023/7/23 12:12:20
     */
    @GetMapping("/downloadGeneratorCode")
    public void downloadGeneratorCode(HttpServletResponse response) {

        GenInfo genInfo= new GenInfo();
        genInfo.setTableName("user");
        genInfo.setClassName("user");
        genInfo.setPackageName("com.wangzhan");
        genInfo.setModuleName("zhanzhan_generator");
        genInfo.setFunctionName("用户信息");
        genInfo.setFunctionAuthor("wangzhan");

        // 根据表名查询该表的所有列数据
        List<GenInfoColumn> genInfoColumns = genInfoService.selectDbTableColumnsByName(genInfo.getTableName());

        for (GenInfoColumn genInfoColumn : genInfoColumns) {
            GeneratorUtils.initColumnField(genInfoColumn);
        }

        genInfo.setColumns(genInfoColumns);

        // 生成类型：zip压缩包
        try {
            GeneratorUtils.genCode(response, genInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
