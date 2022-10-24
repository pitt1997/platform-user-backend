package com.yupi.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author author
 * @date 2022-09-06
 * @descriptionn 退出队伍请求封装
 */
@Data
public class TeamQuitRequest implements Serializable {

    /**
     * 加入队伍ID
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}