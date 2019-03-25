package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbTypeTemplate;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface TypeTemplateService {

	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbTypeTemplate> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	void add(TbTypeTemplate typeTemplate);
	
	
	/**
	 * 修改
	 */
	void update(TbTypeTemplate typeTemplate);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	TbTypeTemplate findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize);

	/**
	 * 获取类型模板下拉列表需要的数据
	 *
	 * @return java.util.List<java.util.Map>
	 */
	List<Map> selectOptionList();

	/**
	 * 获取规格列表
	 *
	 * @param id 模板id
	 * @return java.util.List<java.util.Map>
	 */
	List<Map> findSpecList(Long id);
	
}
