-- 用户表初始化
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                       null comment '星球编号'
)
    comment '用户';


-- user 新增表字段 tags
ALTER TABLE user ADD COLUMN tags varchar(1024) null comment '标签列表';

-- user 新增表字段 profile
ALTER TABLE user ADD COLUMN profile varchar(512) null comment '个人简介';

-- 标签表
create table tag
(
    id          bigint auto_increment comment 'id' primary key,
    tagName     varchar(256)                       null comment '标签名称',
    userId      bigint                             null comment '用户 id',
    parentId    bigint                             null comment '父标签 id',
    isParent    tinyint                            null comment '0 - 不是, 1 - 父标签',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '标签表';

CREATE INDEX idx_userId ON tag (userId);
CREATE UNIQUE INDEX uk_tagName ON tag (tagName);

-- 测试数据
INSERT INTO `platform`.`user`(`username`, `id`, `userAccount`, `avatarUrl`, `gender`, `userPassword`, `phone`, `email`, `userStatus`, `createTime`, `updateTime`, `isDelete`, `userRole`, `planetCode`, `tags`, `profile`) VALUES ('dogYupi', 1, 'dogYupi', 'https://img1.baidu.com/it/u=1645832847,2375824523&fm=253&fmt=auto&app=138&f=JPEG?w=480&h=480', 0, 'b0dd3697a192885d7c055db46155b26a', '456', '123', 0, NULL, NULL, 0, 0, '3', '[\"男\",\"Java\"]', '个人简介，你好');
INSERT INTO `platform`.`user`(`username`, `id`, `userAccount`, `avatarUrl`, `gender`, `userPassword`, `phone`, `email`, `userStatus`, `createTime`, `updateTime`, `isDelete`, `userRole`, `planetCode`, `tags`, `profile`) VALUES ('yupi23', 2, 'yupi', 'https://img1.baidu.com/it/u=1645832847,2375824523&fm=253&fmt=auto&app=138&f=JPEG?w=480&h=480', 0, 'b0dd3697a192885d7c055db46155b26a', NULL, NULL, 0, NULL, NULL, 0, 1, '2', '[\"男\",\"Java\",\"C++\"]', '个人简介，你好我是yupi');
