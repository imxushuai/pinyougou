package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSpecification specification) {
        specificationMapper.insert(specification);
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        //更新规格实体
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //更新规格选项
        TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
        specificationOptionExample.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
        //先删除规格选项
        specificationOptionMapper.deleteByExample(specificationOptionExample);

        //重新插入规格选项
        List<TbSpecificationOption> tbSpecificationOptionList = specification.getSpecificationOptionList();
        //循环保存
        for (TbSpecificationOption option : tbSpecificationOptionList) {
            //设置规格id
            option.setSpecId(tbSpecification.getId());
            //保存规格选项
            specificationOptionMapper.insert(option);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //封装规格选项查询条件
        TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
        specificationOptionExample.createCriteria().andSpecIdEqualTo(id);
        //查询规格选项
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(specificationOptionExample);

        //创建规格组合实体类
        Specification specification = new Specification();
        specification.setSpecification(tbSpecification);
        specification.setSpecificationOptionList(tbSpecificationOptions);

        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格数据
            specificationMapper.deleteByPrimaryKey(id);

            //删除规格选项数据
            TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
            specificationOptionExample.createCriteria().andSpecIdEqualTo(id);
            //删除规格选项
            specificationOptionMapper.deleteByExample(specificationOptionExample);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    @Override
    public void add(Specification specification) {
        //从组合实体中获取规格对象
        TbSpecification tbSpecification = specification.getSpecification();
        //执行保存
        specificationMapper.insert(tbSpecification);

        //从组合试题中获取规格选项
        List<TbSpecificationOption> tbSpecificationOptionList = specification.getSpecificationOptionList();
        //循环保存
        for (TbSpecificationOption option : tbSpecificationOptionList) {
            //设置规格id
            option.setSpecId(tbSpecification.getId());
            //保存规格选项
            specificationOptionMapper.insert(option);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
