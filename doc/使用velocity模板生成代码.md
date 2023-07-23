## 使用velocity模板生成代码

### 目录结构及代码下载



![image-20230723113059372](https://github.com/wangzhan6666/zhanzhan_generator/blob/master/doc/img/image-20230723113059372.png)





![image-20230723121554072](https://github.com/wangzhan6666/zhanzhan_generator/blob/master/doc/img/image-20230723121554072.png)



### 完整代码地址

```
https://github.com/wangzhan6666/zhanzhan_generator
```





### 1、测试使用到的数据库文件

```sql
/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3309
 Source Schema         : zhanzhan_generator

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 23/07/2023 10:56:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名字',
  `age` int(0) NULL DEFAULT NULL COMMENT '用户年龄',
  `version` int(0) NULL DEFAULT 1 COMMENT '乐观锁',
  `deleted` int(0) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1482996505408204804 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '张三', 15, 1, 0, '2023-07-15 23:49:33', '2023-07-15 23:49:38');
INSERT INTO `user` VALUES (2, '李四', 22, 1, 0, '2023-07-15 23:49:33', '2023-07-15 23:49:38');

SET FOREIGN_KEY_CHECKS = 1;

```



### 2、生成代码的关键步骤

#### 2.1、请求参数描述

```java
package com.wangzhan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wangzhan
 * @version 1.0
 * @project zhanzhan-springcloud
 * @description 生成代码信息
 * @date 2023/7/20 19:46:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenInfo {

    /** 表名称 */
    private String tableName;

    /** 表描述 */
//    @NotBlank(message = "表描述不能为空")
//    private String tableComment;

    /** 实体类名称(首字母大写) */
//    @NotBlank(message = "实体类名称不能为空")
    private String className;

    /** 生成包路径 */
    @NotBlank(message = "生成包路径不能为空")
    private String packageName;

    /** 生成模块名 */
//    @NotBlank(message = "生成模块名不能为空")
    private String moduleName;

    /** 生成业务名 */
//    @NotBlank(message = "生成业务名不能为空")
//    private String businessName;

    /** 生成功能名 */
    @NotBlank(message = "生成功能名不能为空")
    private String functionName;

    /** 生成作者 */
    @NotBlank(message = "作者不能为空")
    private String functionAuthor;

    /** 生成代码方式（0zip压缩包 1自定义路径） */
    private String genType;

    /** 生成路径 - 绝对路径（不填默认项目路径） */
    private String genPath;

    // 列数据
    private List<GenInfoColumn> columns;

}
```



#### 2.2、controller代码

```java
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
```



#### 2.3、代码生成工具类

```java
package com.wangzhan.utils.generator;

import com.wangzhan.constant.GenConstants;
import com.wangzhan.domain.GenInfo;
import com.wangzhan.domain.GenInfoColumn;
import com.wangzhan.utils.str.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GeneratorUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "src\\main\\java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "src\\main\\resources\\mapper";

    /***
     * @description 生成代码
     * @param genInfo
     * @author wangzhan
     * @date 2023/7/23 11:15:17
     */
    public static void generatorCode(GenInfo genInfo) {

        VelocityInitializer.initVelocity();

        // 设置模板参数
        VelocityContext context = prepareContext(genInfo);
        // 获取模板信息
        List<String> templateList = getTemplateList();

        for (String template : templateList) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                // 获取文件生成的路径
                String path = getGenPath(template, genInfo);
                FileUtils.writeStringToFile(new File(path), sw.toString(), "UTF-8");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * 生成代码（下载方式）
     *
     * @param genInfo
     * @return 数据
     */
    public static byte[] downloadCode(GenInfo genInfo)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(genInfo, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成zip文件
     */
    public static void genCode(HttpServletResponse response, GenInfo genInfo) throws IOException
    {
        //
        byte[] data = downloadCode(genInfo);

        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"zhanzhan-generator.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    /***
     * @description 生成代码(下载方式)
     * @param genInfo
     * @author wangzhan
     * @date 2023/7/23 11:15:17
     */
    public static void generatorCode(GenInfo genInfo, ZipOutputStream zip) {

        VelocityInitializer.initVelocity();

        // 设置模板参数
        VelocityContext context = prepareContext(genInfo);
        // 获取模板信息
        List<String> templateList = getTemplateList();

        for (String template : templateList) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                // 获取文件生成的路径
                String path = getFileName(template, genInfo);
                // 添加到zip中
                zip.putNextEntry(new ZipEntry(path));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /***
     * @description 设置模板变量参数
     * @param genInfo
     * @return org.apache.velocity.VelocityContext
     * @author wangzhan
     * @date 2023/7/20 20:45:48
     */
    public static VelocityContext prepareContext(GenInfo genInfo) {

        String className = genInfo.getClassName();

        VelocityContext context = new VelocityContext();
        context.put("tableName", genInfo.getTableName());   // 表名
        context.put("functionAuthor" , genInfo.getFunctionAuthor()); // 代码生成的作者
        context.put("createDateTime" , parseDateToStr()); // 代码创建时间
        context.put("moduleName" , genInfo.getModuleName()); // 模块名
        context.put("functionName" , genInfo.getFunctionName()); // 功能项名 用来描述注释
        context.put("className" , className == null ? genInfo.getTableName() : className);   // 实体类名，一般和表名一致
        context.put("ClassName" , StringUtils.capitalize(className));    // 实体类名首字母大写
        context.put("packageName" , genInfo.getPackageName());   // 生成包路径
        context.put("columns" , genInfo.getColumns());  // 列信息

        // 获取主键的列信息
        if (!CollectionUtils.isEmpty(genInfo.getColumns())) {
            GenInfoColumn pkColumn = genInfo.getColumns().stream().filter((item) -> "1".equals(item.getIsPk())).findFirst().orElse(null);
            context.put("pkColumn", pkColumn);
        }

        return context;
    }

    /**
     * @return java.util.List<java.lang.String>
     * @description 获取模板信息
     * @author wangzhan
     * @date 2023/7/20 19:31:49
     */
    public static List<String> getTemplateList() {
        List<String> templates = new ArrayList<String>();
        templates.add("templates/domain.java.vm");
        templates.add("templates/mapper.java.vm");
        templates.add("templates/service.java.vm");
        templates.add("templates/serviceImpl.java.vm");
        templates.add("templates/controller.java.vm");
        templates.add("templates/mapper.xml.vm");

        return templates;
    }

    /***
     * @description 获取代码生成地址
     * @param template
     * @return java.lang.String
     * @author wangzhan
     * @date 2023/7/20 19:43:52
     */
    public static String getGenPath(String template, GenInfo genInfo) {
        String genPath = genInfo.getGenPath();
        if (StringUtils.isEmpty(genPath) || StringUtils.equals(genPath, "/")) {
            return System.getProperty("user.dir") + File.separator + getFileName(template, genInfo);
        }

        return genPath + File.separator + getFileName(template, genInfo);
    }

    /***
     * @description 获取文件名称
     * @param template
     * @param genInfo
     * @return java.lang.String
     * @author wangzhan
     * @date 2023/7/20 21:20:01
     */
    public static String getFileName(String template, GenInfo genInfo) {

        // 文件名称
        String fileName = "";
        // 模块名
        String moduleName = genInfo.getModuleName();
        // 包名
        String packageName = genInfo.getPackageName();
        // 大写类名
        String className = StringUtils.capitalize(genInfo.getClassName() == null ? genInfo.getTableName() : genInfo.getClassName());


        String prePath = "";

        if (!System.getProperty("user.dir").contains(moduleName)) {
            prePath = moduleName + File.separator;
        }

        // java文件路径
        String javaPath = prePath + PROJECT_PATH + File.separator + StringUtils.replace(packageName, "." , "/");
        // mybatis 配置文件路径
        String mybatisPath = prePath + MYBATIS_PATH;

        if (template.contains("controller.java.vm")) {
            fileName = StringUtils.format("{}/controller/{}Controller.java" , javaPath, className);
        } else if (template.contains("domain.java.vm")) {
            fileName = StringUtils.format("{}/domain/{}.java" , javaPath, className);
        } else if (template.contains("mapper.java.vm")) {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java" , javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtils.format("{}/{}Mapper.xml" , mybatisPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StringUtils.format("{}/service/I{}Service.java" , javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java" , javaPath, className);
        }

        return fileName;
    }

    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenInfoColumn column) {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        // 设置java字段名
        column.setJavaField(StringUtils.toCamelCase(columnName));
        // 设置默认类型
        column.setJavaType("String");

        if (arraysContains(GenConstants.COLUMNTYPE_STR, dataType) || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType)) {
            // 字符串长度超过500设置为文本域
            Integer columnLength = getColumnLength(column.getColumnType());
            String htmlType = columnLength >= 500 || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType) ? GenConstants.HTML_TEXTAREA : GenConstants.HTML_INPUT;
        } else if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType)) {
            column.setJavaType(GenConstants.TYPE_DATE);
        } else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType)) {

            // 如果是浮点型 统一用BigDecimal
            String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(" , ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
                column.setJavaType(GenConstants.TYPE_INTEGER);
            }
            // 长整形
            else {
                column.setJavaType(GenConstants.TYPE_LONG);
            }
        }
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType) {
        if (StringUtils.indexOf(columnType, "(") > 0) {
            return StringUtils.substringBefore(columnType, "(");
        } else {
            return columnType;
        }
    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr         数组
     * @param targetValue 值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType) {
        if (StringUtils.indexOf(columnType, "(") > 0) {
            String length = StringUtils.substringBetween(columnType, "(" , ")");
            return Integer.valueOf(length);
        } else {
            return 0;
        }
    }

    /**
     * @description 解析当前日期为字符串
     * @return
     * @author wangzhan
     * @date 2023/7/22 20:10:59
     */
    public static final String parseDateToStr() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

}

```

```java
package com.wangzhan.utils.generator;

import org.apache.velocity.app.Velocity;

import java.util.Properties;

/**
 * VelocityEngine工厂
 * 
 * @author
 */
public class VelocityInitializer
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 初始化vm方法
     */
    public static void initVelocity()
    {
        Properties p = new Properties();
        try
        {
            // 加载classpath目录下的vm文件
            p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, UTF8);
            p.setProperty(Velocity.OUTPUT_ENCODING, UTF8);
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

```



#### 2.4、根据表名查询数据库列数据

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.wangzhan.mapper.GenInfoMapper">

    <resultMap id="GenInfoColumnResult" type="com.wangzhan.domain.GenInfoColumn">
        <result property="columnName" column="column_name"/>
        <result property="columnComment" column="column_comment"/>
        <result property="columnType" column="column_type"/>
        <result property="isPk" column="is_pk"/>
        <result property="isIncrement" column="is_increment"/>
        <result property="isRequired" column="is_Required"/>
        <result property="sort" column="sort"/>
    </resultMap>

    <select id="selectDbTableColumnsByName" resultType="com.wangzhan.domain.GenInfoColumn"
            resultMap="GenInfoColumnResult">
        select column_name,
               (case when (is_nullable = 'no' <![CDATA[ && ]]> column_key != 'PRI') then '1' else null end) as is_required,
               (case when column_key = 'PRI' then '1' else '0' end)                               as is_pk,
               ordinal_position                                                                   as sort,
               column_comment,
               (case when extra = 'auto_increment' then '1' else '0' end)                         as is_increment,
               column_type
        from information_schema.columns
        where table_schema = (select database())
          and table_name = (#{tableName})
        order by ordinal_position
    </select>

</mapper>
```



### 3、模板代码

#### 3.1、controller.java.vm

```
package ${packageName}.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wangzhan.common.controller.BaseController;
import com.wangzhan.common.page.TableDataInfo;
import com.wangzhan.utils.result.AjaxResult;
import ${packageName}.domain.${ClassName};
import ${packageName}.service.I${ClassName}Service;


/**
 * ${functionName}Controller
 * 
 * @author ${functionAuthor}
 * @date ${createDateTime}
 */
@RestController
@RequestMapping("/${moduleName}/${className}")
public class ${ClassName}Controller extends BaseController
{
    @Autowired
    private I${ClassName}Service ${className}Service;

    /**
     * 查询${functionName}列表
     */
    @GetMapping("/list")
    public TableDataInfo list(${ClassName} ${className})
    {
        startPage();
        List<${ClassName}> list = ${className}Service.select${ClassName}List(${className});
        return getDataTable(list);
    }


##    /**
##     * 导出${functionName}列表
##     */
##    @GetMapping("/export")
##    public AjaxResult export(${ClassName} ${className})
##    {
##        List<${ClassName}> list = ${className}Service.select${ClassName}List(${className});
##        ExcelUtil<${ClassName}> util = new ExcelUtil<${ClassName}>(${ClassName}.class);
##        return util.exportExcel(list, "${functionName}数据");
##    }

    /**
     * 获取${functionName}详细信息
     */
    @GetMapping(value = "/{${pkColumn.javaField}}")
    public AjaxResult getInfo(@PathVariable("${pkColumn.javaField}") ${pkColumn.javaType} ${pkColumn.javaField})
    {
        return AjaxResult.success(${className}Service.select${ClassName}ById(${pkColumn.javaField}));
    }

    /**
     * 新增${functionName}
     */
    @PostMapping
    public AjaxResult add(@RequestBody ${ClassName} ${className})
    {
        return toAjax(${className}Service.insert${ClassName}(${className}));
    }

    /**
     * 修改${functionName}
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ${ClassName} ${className})
    {
        return toAjax(${className}Service.update${ClassName}(${className}));
    }

    /**
     * 删除${functionName}
     */
	@DeleteMapping("/{${pkColumn.javaField}s}")
    public AjaxResult remove(@PathVariable ${pkColumn.javaType}[] ${pkColumn.javaField}s)
    {
        return toAjax(${className}Service.delete${ClassName}ByIds(${pkColumn.javaField}s));
    }
}

```



### 3.2、domain.java.vm

```
package ${packageName}.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
#foreach ($column in $columns)
#if($column.javaType == 'Date')
import java.util.Date;
#break
#end
#end

/**
 * ${functionName}对象 ${tableName}
 * 
 * @author ${functionAuthor}
 * @date ${createDateTime}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ${ClassName} implements Serializable
{
    private static final long serialVersionUID = 1L;

#foreach ($column in $columns)
    /** $column.columnComment */
    private $column.javaType $column.javaField;

#end

}

```



#### 3.3、mapper.java.vm

```
package ${packageName}.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import ${packageName}.domain.${ClassName};

/**
 * ${functionName}Mapper接口
 * 
 * @author ${functionAuthor}
 * @date ${createDateTime}
 */
@Repository
@Mapper
public interface ${ClassName}Mapper 
{
    /**
     * 查询${functionName}
     * 
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return ${functionName}
     */
    public ${ClassName} select${ClassName}ById(${pkColumn.javaType} ${pkColumn.javaField});

    /**
     * 查询${functionName}列表
     * 
     * @param ${className} ${functionName}
     * @return ${functionName}集合
     */
    public List<${ClassName}> select${ClassName}List(${ClassName} ${className});

    /**
     * 新增${functionName}
     * 
     * @param ${className} ${functionName}
     * @return 结果
     */
    public int insert${ClassName}(${ClassName} ${className});

    /**
     * 修改${functionName}
     * 
     * @param ${className} ${functionName}
     * @return 结果
     */
    public int update${ClassName}(${ClassName} ${className});

    /**
     * 删除${functionName}
     * 
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return 结果
     */
    public int delete${ClassName}ById(${pkColumn.javaType} ${pkColumn.javaField});

    /**
     * 批量删除${functionName}
     * 
     * @param ${pkColumn.javaField}s 需要删除的数据ID
     * @return 结果
     */
    public int delete${ClassName}ByIds(${pkColumn.javaType}[] ${pkColumn.javaField}s);
#if($table.sub)

    /**
     * 批量删除${subTable.functionName}
     * 
     * @param customerIds 需要删除的数据ID
     * @return 结果
     */
    public int delete${subClassName}By${subTableFkClassName}s(${pkColumn.javaType}[] ${pkColumn.javaField}s);
    
    /**
     * 批量新增${subTable.functionName}
     * 
     * @param ${subclassName}List ${subTable.functionName}列表
     * @return 结果
     */
    public int batch${subClassName}(List<${subClassName}> ${subclassName}List);
    

    /**
     * 通过${functionName}ID删除${subTable.functionName}信息
     * 
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return 结果
     */
    public int delete${subClassName}By${subTableFkClassName}(${pkColumn.javaType} ${pkColumn.javaField});
#end
}

```



#### 3.4、mapper.xml.vm

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${ClassName}Mapper">
    
    <resultMap type="${packageName}.domain.${ClassName}" id="${ClassName}Result">
#foreach ($column in $columns)
        <result property="${column.javaField}"    column="${column.columnName}"    />
#end
    </resultMap>

    <sql id="select${ClassName}Vo">
        select#foreach($column in $columns) $column.columnName#if($velocityCount != $columns.size()),#end#end from ${tableName}
    </sql>

    <select id="select${ClassName}List" parameterType="${packageName}.domain.${ClassName}" resultMap="${ClassName}Result">
        <include refid="select${ClassName}Vo"/>
        <where>
#foreach($column in $columns)
            <if test="$column.javaField != null #if($column.javaType == 'String' && $column.isRequired) and $column.javaField.trim() != ''#end">
             and $column.columnName = #{$column.javaField}
            </if>
#end
        </where>
    </select>
    
    <select id="select${ClassName}ById" parameterType="${pkColumn.javaType}" resultMap="${ClassName}Result">
        <include refid="select${ClassName}Vo"/>
        where ${pkColumn.columnName} = #{${pkColumn.javaField}}
    </select>
        
    <insert id="insert${ClassName}" parameterType="${packageName}.domain.${ClassName}"#if($pkColumn.isIncrement) useGeneratedKeys="true" keyProperty="$pkColumn.javaField"#end>
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
#foreach($column in $columns)
#if($column.columnName != $pkColumn.columnName)
            <if test="$column.javaField != null#if($column.javaType == 'String' && $column.isRequired) and $column.javaField != ''#end">
                $column.columnName,
            </if>
#end
#end
         </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
#foreach($column in $columns)
#if($column.columnName != $pkColumn.columnName)
            <if test="$column.javaField != null#if($column.javaType == 'String' && $column.isRequired) and $column.javaField != ''#end">
                #{$column.javaField},
            </if>
#end
#end
         </trim>
    </insert>

    <update id="update${ClassName}" parameterType="${packageName}.domain.${ClassName}">
        update ${tableName}
        <trim prefix="SET" suffixOverrides=",">
#foreach($column in $columns)
#if($column.columnName != $pkColumn.columnName)
            <if test="$column.javaField != null#if($column.javaType == 'String' && $column.isRequired) and $column.javaField != ''#end">
                $column.columnName = #{$column.javaField},
            </if>
#end
#end
        </trim>
        where ${pkColumn.columnName} = #{${pkColumn.javaField}}
    </update>

    <delete id="delete${ClassName}ById" parameterType="${pkColumn.javaType}">
        delete from ${tableName} where ${pkColumn.columnName} = #{${pkColumn.javaField}}
    </delete>

    <delete id="delete${ClassName}ByIds" parameterType="String">
        delete from ${tableName} where ${pkColumn.columnName} in 
        <foreach item="${pkColumn.javaField}" collection="array" open="(" separator="," close=")">
            #{${pkColumn.javaField}}
        </foreach>
    </delete>
</mapper>
```



#### 3.5、service.java.vm

```
package ${packageName}.service;

import java.util.List;
import ${packageName}.domain.${ClassName};

/**
 * ${functionName}Service接口
 * 
 * @author ${functionAuthor}
 * @date ${createDateTime}
 */
public interface I${ClassName}Service 
{
    /**
     * 查询${functionName}
     * 
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return ${functionName}
     */
    public ${ClassName} select${ClassName}ById(${pkColumn.javaType} ${pkColumn.javaField});

    /**
     * 查询${functionName}列表
     * 
     * @param ${className} ${functionName}
     * @return ${functionName}集合
     */
    public List<${ClassName}> select${ClassName}List(${ClassName} ${className});

    /**
     * 新增${functionName}
     * 
     * @param ${className} ${functionName}
     * @return 结果
     */
    public int insert${ClassName}(${ClassName} ${className});

    /**
     * 修改${functionName}
     * 
     * @param ${className} ${functionName}
     * @return 结果
     */
    public int update${ClassName}(${ClassName} ${className});

    /**
     * 批量删除${functionName}
     * 
     * @param ${pkColumn.javaField}s 需要删除的${functionName}ID
     * @return 结果
     */
    public int delete${ClassName}ByIds(${pkColumn.javaType}[] ${pkColumn.javaField}s);

    /**
     * 删除${functionName}信息
     * 
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return 结果
     */
    public int delete${ClassName}ById(${pkColumn.javaType} ${pkColumn.javaField});
}

```



#### 3.6、serviceImpl.java.vm

```
package ${packageName}.service.impl;

import java.util.List;
#foreach ($column in $columns)
#if($column.javaType == 'Date')
import java.util.Date;
#break
#end
#end

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ${packageName}.mapper.${ClassName}Mapper;
import ${packageName}.domain.${ClassName};
import ${packageName}.service.I${ClassName}Service;

/**
 * ${functionName}Service业务层处理
 *
 * @author ${functionAuthor}
 * @date ${createDateTime}
 */
@Service
public class ${ClassName}ServiceImpl implements I${ClassName}Service {
    @Autowired
    private ${ClassName}Mapper ${className}Mapper;

    /**
     * 查询${functionName}
     *
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return ${functionName}
     */
    @Override
    public ${ClassName} select${ClassName}ById(${pkColumn.javaType} ${pkColumn.javaField}) {
        return ${className}Mapper.select${ClassName}ById(${pkColumn.javaField});
    }

    /**
     * 查询${functionName}列表
     *
     * @param ${className} ${functionName}
     * @return ${functionName}
     */
    @Override
    public List<${ClassName}> select${ClassName}List(${ClassName} ${className}) {
        return ${className}Mapper.select${ClassName}List(${className});
    }

    /**
     * 新增${functionName}
     *
     * @param ${className} ${functionName}
     * @return 结果
     */
    @Override
    public int insert${ClassName}(${ClassName} ${className}) {
#foreach ($column in $columns)
    #if($column.javaField == 'createTime')
        ${className}.setCreateTime(new Date());
    #end
#end
        return ${className}Mapper.insert${ClassName}(${className});
    }

    /**
     * 修改${functionName}
     *
     * @param ${className} ${functionName}
     * @return 结果
     */
    @Override
    public int update${ClassName}(${ClassName} ${className}) {
#foreach ($column in $columns)
    #if($column.javaField == 'updateTime')
        ${className}.setUpdateTime(new Date());
    #end
#end
        return ${className}Mapper.update${ClassName}(${className});
    }

    /**
     * 批量删除${functionName}
     *
     * @param ${pkColumn.javaField}s 需要删除的${functionName}ID
     * @return 结果
     */
    @Override
    public int delete${ClassName}ByIds(${pkColumn.javaType}[] ${pkColumn.javaField}s) {
        return ${className}Mapper.delete${ClassName}ByIds(${pkColumn.javaField}s);
    }

    /**
     * 删除${functionName}信息
     *
     * @param ${pkColumn.javaField} ${functionName}ID
     * @return 结果
     */
    @Override
    public int delete${ClassName}ById(${pkColumn.javaType} ${pkColumn.javaField}) {
        return ${className}Mapper.delete${ClassName}ById(${pkColumn.javaField});
    }

}

```





































