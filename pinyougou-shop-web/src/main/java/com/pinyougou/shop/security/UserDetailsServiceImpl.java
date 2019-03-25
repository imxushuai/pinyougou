package com.pinyougou.shop.security;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security 自定义认证类
 * Author xushuai
 * Description
 */
public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 构建角色列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 这个角色名必须在 Spring Security 配置文件中配置
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //按用户名获取商家
        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            // 判断商家状态是否合法
            if(seller.getStatus().equals(TbSeller.STATUS_CHECK)) {// 合法
                /*
                 * 进行校验：
                 *      Spring Security会自动校验输入的username、password，与User对象中的useranme和password进行校验
                 *      如果校验成功，就将角色列表中的角色赋予给当前登录的用户
                 */
                return new User(username, seller.getPassword(), authorities);
            }
        }

        return null;
    }
}

