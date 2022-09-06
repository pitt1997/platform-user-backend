package com.yupi.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author author
 * @date 2022-09-06
 * @description 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;

}