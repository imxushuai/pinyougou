package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbAddress;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface AddressService {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbAddress> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	void add(TbAddress address);
	
	
	/**
	 * 修改
	 */
	void update(TbAddress address);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	TbAddress findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param id
	 */
	void delete(Long id);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbAddress address, int pageNum, int pageSize);

	/**
	 * 获取指定用户的收货地址列表
	 *
	 * @param userId 用户ID
	 * @return java.util.List<com.pinyougou.pojo.TbAddress>
	 */
	List<TbAddress> findListByLoginUser(String userId);
	
}
