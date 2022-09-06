package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.service.UserTeamService;
import com.yupi.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author lijingsong
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2022-09-04 22:28:37
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




