package com.py.service.impl;

import com.py.dao.ShiroUserMapper;
import com.py.model.ShiroUser;
import com.py.service.ShiroUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Created by pysasuke on 2017/8/21.
 */
@Service("shiroUserService")
public class ShiroUserServiceImpl implements ShiroUserService {
    @Resource
    private ShiroUserMapper shiroUserMapper;

    public ShiroUser getByUsername(String username) {
        return shiroUserMapper.getByUsername(username);
    }

    public Set<String> getRoles(String username) {
        return shiroUserMapper.getRoles(username);
    }

    public Set<String> getPermissions(String username) {
        return shiroUserMapper.getPermissions(username);
    }
}
