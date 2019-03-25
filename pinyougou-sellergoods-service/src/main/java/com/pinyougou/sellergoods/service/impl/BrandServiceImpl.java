package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import exception.PinyougouException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findByPage(int pageNum, int pageSize) {
		// 封装分页参数
		PageHelper.startPage(pageNum, pageSize);
		// 执行查询
		Page<TbBrand> pageInfo = (Page<TbBrand>) brandMapper.selectByExample(null);
		// 封装分页结果对象
		PageResult result = new PageResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(pageInfo.getResult());

		return result;
	}

	@Override
	public void add(TbBrand brand) {
		// 封装查询条件
		TbBrandExample example = new TbBrandExample();
		example.createCriteria().andNameEqualTo(brand.getName());
		// 查询
		List<TbBrand> list = brandMapper.selectByExample(example);
		if(list != null && list.size() > 0) {
			throw new PinyougouException("该品牌已存在");
		}
		brandMapper.insert(brand);
	}

	@Override
	public TbBrand findOne(long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand brand) {
		// 封装查询条件
		TbBrandExample example = new TbBrandExample();
		example.createCriteria().andNameEqualTo(brand.getName());
		// 查询
		List<TbBrand> list = brandMapper.selectByExample(example);
		if(list != null && list.size() > 0) {
			throw new PinyougouException("该品牌已存在");
		}
		brandMapper.updateByPrimaryKey(brand);
	}

	@Override
	public void delete(long[] ids) {
		for (long id : ids){
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findByPage(TbBrand brand, int page, int size) {
		// 封装分页参数
		PageHelper.startPage(page, size);
		// 创建查询条件
		TbBrandExample example = new TbBrandExample();
		// 封装查询条件
		if(brand != null) {
			TbBrandExample.Criteria criteria = example.createCriteria();
			if(brand.getName() != null && brand.getName().trim().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
			if(brand.getFirstChar() != null && brand.getFirstChar().trim().length() > 0) {
				criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
			}
		}
		// 执行查询
		Page<TbBrand> pageInfo = (Page<TbBrand>) brandMapper.selectByExample(example);
		// 封装分页结果对象
		PageResult result = new PageResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(pageInfo.getResult());

		return result;
	}

	@Override
	public List<Map> selectOptionList() {
		return brandMapper.selectOptionList();
	}

}
