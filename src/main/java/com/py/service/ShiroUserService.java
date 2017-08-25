package com.py.service;

import com.py.model.ShiroUser;
import java.util.Set;

/**
 * Created by pysasuke on 2017/8/21.
 */
public interface ShiroUserService {
    ShiroUser getByUsername(String username);

    Set<String> getRoles(String username);

    Set<String> getPermissions(String username);

}
