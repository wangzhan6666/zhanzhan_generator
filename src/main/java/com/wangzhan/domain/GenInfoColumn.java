package com.wangzhan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangzhan
 * @version 1.0
 * @description 生成代码的表字段信息
 * @date 2023/7/21 11:13:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenInfoColumn {

    /** 列名称 */
    private String columnName;

    /** 列描述 */
    private String columnComment;

    /** 列类型 */
    private String columnType;

    /** 是否主键（1是） */
    private String isPk;

    /** 是否自增（1是） */
    private String isIncrement;

    /** 是否必填（1是） */
    private String isRequired;

    /** 排序 */
    private Integer sort;

    /** domain属性名 */
    private String javaField;

    /** domain类型 */
    private String javaType;
}
