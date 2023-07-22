package com.wangzhan.utils.generator;

import com.wangzhan.constant.GenConstants;
import com.wangzhan.domain.GenInfo;
import com.wangzhan.domain.GenInfoColumn;
import com.wangzhan.utils.str.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GeneratorUtils {

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "src\\main\\java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "src\\main\\resources\\mapper";

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
