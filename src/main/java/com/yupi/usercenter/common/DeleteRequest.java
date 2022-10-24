package com.yupi.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author author
 * @date 2022-10-09
 * @description 通用删除请求
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 通用删除请求id
     */
    protected long id;
}