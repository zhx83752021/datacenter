package com.smart.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.manager.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT DISTINCT r.role_key " +
            "FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 " +
            "AND (r.del_flag IS NULL OR r.del_flag = 0)")
    List<String> selectRoleKeysByUserId(Long userId);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT DISTINCT r.* " +
            "FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 " +
            "AND (r.del_flag IS NULL OR r.del_flag = 0)")
    List<SysRole> selectRolesByUserId(Long userId);
}
