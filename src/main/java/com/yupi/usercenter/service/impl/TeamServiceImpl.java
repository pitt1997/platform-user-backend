package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.common.ErrorCodeEnum;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.model.dto.TeamQuery;
import com.yupi.usercenter.model.enums.TeamStatusEnum;
import com.yupi.usercenter.model.request.TeamJoinRequest;
import com.yupi.usercenter.model.request.TeamQuitRequest;
import com.yupi.usercenter.model.request.TeamUpdateRequest;
import com.yupi.usercenter.model.vo.TeamUserVO;
import com.yupi.usercenter.model.vo.UserVO;
import com.yupi.usercenter.service.TeamService;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author lijingsong
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2022-09-04 22:21:14
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    /**
     * 创建队伍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        // 2. 是否登录，未进行登录则不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_LOGIN);
        }

        final long userId = loginUser.getId();

        //  3. 校验信息
        //      1. 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍人数不满足要求");
        }

        //      2. 队伍标题  <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍标题不满足要求");
        }

        //      3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(name) && description.length() > 512) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍描述过长");
        }

        //      4. status 是否公开 （int）不传默认为0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍状态不满足要求");
        }

        //      5. 如果 status 是加密状态，密码有的话 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "密码设置不正确");
        }

        //      6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "超时时间小于当前时间");
        }

        //      7. 校验用户最多创建 10 个队伍
        // todo 有 bug ，可能同时创建 100 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum > 10) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }

        // 4. 插入队伍信息到队伍表
        // id 使用自增
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        // 数据库插入已经设置好 teamId
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "创建队伍失败");
        }

        // 5. 插入用户 - 队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "插入用户 - 队伍关系表失败");
        }

        return teamId;
    }

    /**
     * 队伍查询
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }

            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }

            // 关键字模糊查询
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }

            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                // 允许模糊匹配
                queryWrapper.like("name", name);
            }

            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }

            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数 - 相等
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }

            Long userId = teamQuery.getUserId();
            // 根据创建人查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }

            // 根据状态查询
            // 只有管理员才能查询加密还有非公开的队伍
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }

            // 非admin账号 并且 队伍非公开 -> 无权限
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
               throw new BusinessException(ErrorCodeEnum.NO_AUTH);
            }

            queryWrapper.eq("status", statusEnum.getValue());
        }

        // 不展示已过期的队伍
        // and expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        List<Team> teamList = this.list(queryWrapper);

        // 关联查询用户信息
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOList = new ArrayList<>();

        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }

            User user = userService.getById(userId);
            if (user == null) {
                continue;
            }

            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);

            // 用户信息脱敏
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
        }

        return teamUserVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        Long id = teamUpdateRequest.getId();
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_NULL_ERROR, "队伍不存在");
        }

        // 只有管理员或者队伍的创建者可以修改
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH);
        }

        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET)) {
            // 如果修改队伍为加密的那么必须设置密码
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }

        // 脱敏实体方便只修改指定字段
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);

        // 根据 id 进行修改
        return this.updateById(updateTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if ( expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍已过期");
        }

        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "禁止加入私有队伍");
        }

        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "密码错误");
            }
        }

        // 该用户已经加入的队伍数量
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinNum > 5) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "最多创建和加入 5 个队伍");
        }

        // 不能重复加入队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long hasUserTeamJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (hasUserTeamJoinNum > 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "不能重复加入队伍");
        }

        // 已加入队伍的人数
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        if (teamHasJoinNum >= team.getMaxNum()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍已满");
        }

        // 存用户加入队伍关联信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "请求队伍不存在");
        }
        
        Long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "未加入队伍");
        }

        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        // 队伍只剩下一人，解散
        if (teamHasJoinNum == 1) {
            // 删除队伍
            this.removeById(teamId);
        } else {
            // 队伍至少还剩两人
            // 队伍还有其他人，判断是否为队长
            if (team.getUserId() == userId) {
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last(" order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
                }

                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "更新队伍失败.");
                }
            }
        }

        // 删除当前用户队伍关联关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 关于 Transactional 注解的使用说明：
     * 默认是当该方法抛出 RuntimeException 的时候才会触发事务的回滚，修改为 Exception 范围则当抛出自定义异常的时候也会发生回滚
     *
     * @param id
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        // 校验队伍是否存在
        Team team = this.getTeamById(id);
        Long teamId = team.getId();
        // 校验你是不是队长
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCodeEnum.NO_AUTH);
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 根据teamId查询队伍人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 根据id获取队伍
     * @param teamId
     * @return
     */
    public Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }

        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "队伍不存在");
        }

        return team;
    }
}




