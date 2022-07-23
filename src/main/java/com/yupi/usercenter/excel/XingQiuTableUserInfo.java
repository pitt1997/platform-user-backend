package com.yupi.usercenter.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ljs
 * @date 2022-07-14
 * @description
 */
@Data
@EqualsAndHashCode
public class XingQiuTableUserInfo {
    /**
     * 星球编号
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("昵称")
    private String userName;

}
