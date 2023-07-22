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
