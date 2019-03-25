package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务实现
 * Author xushuai
 * Description
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        // 处理查询条件中的空格
        String str = (String) searchMap.get("keywords");
        // 处理空格
        searchMap.put("keywords", str.replace(" ", ""));

        // 创建返回结果
        Map<String, Object> resultMap = new HashMap<>();
        if (searchMap.get("keywords").equals("")) {
            if (!searchMap.get("category").equals("")) {
                searchMap.put("keywords", searchMap.get("category"));
            } else {
                if (!searchMap.get("brand").equals("")) {
                    searchMap.put("keywords", searchMap.get("brand"));
                }
            }
        }
        // 1、查询商品数据
        resultMap.putAll(searchList(searchMap));
        // 2、查询分类数据
        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);
        // 3、按规格和品牌过滤
        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {
            resultMap.putAll(searchBrandAndSpec(category));
        } else {
            if (categoryList.size() > 0) {
                resultMap.putAll(searchBrandAndSpec(categoryList.get(0)));
            }
        }

        return resultMap;
    }

    @Override
    public void importList(List<TbItem> itemList) {
        // 将itemList放入索引库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List ids) {
        // 创建查询对象
        Query query =  new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        // 执行
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    /**
     * 查询数据
     *
     * @param searchMap 查询条件
     * @return Map<String, Object>
     */
    private Map<String, Object> searchList(Map searchMap) {
        // 构造高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        // 创建高亮查询设置对象,并设置要高亮显示的域
        HighlightOptions highlightOptions = new HighlightOptions();
        /*
         * 设置高亮相关属性
         *          addField：设置需要高亮显示的域,可以设置多个,获取高亮数据时通过下标获取
         *          setSimplePrefix：设置高亮前缀
         *          setSimplePostfix：设置高亮后缀
         */
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        // 将高亮设置到查询对象中
        query.setHighlightOptions(highlightOptions);
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        // 设置过滤查询条件
        setFilterOptions(searchMap, query);

        // 设置分页条件
        setPageOptions(searchMap, query);

        // 设置排序
        setSort(searchMap, query);

        // 执行查询,注意:这里面的结果集中的getContent返回的结果集中,并没有带上高亮,需要自行处理
        HighlightPage<TbItem> pageInfo = solrTemplate.queryForHighlightPage(query, TbItem.class);
        // 从结果集中取出数据,最先遍历高亮结果入口集
        for (HighlightEntry<TbItem> entry : pageInfo.getHighlighted()) {
            // 健壮性判断
            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
                // 从高亮结果入口集合中再获取高亮数据
                String title = entry.getHighlights().get(0).getSnipplets().get(0);
                // 从entry中获取当前遍历到的item，并将高亮数据设置到标题
                TbItem item = entry.getEntity();
                item.setTitle(title);
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", pageInfo.getContent());
        // 返回总页数
        resultMap.put("totalPages", pageInfo.getTotalPages());
        // 返回总记录数
        resultMap.put("total", pageInfo.getTotalElements());

        return resultMap;
    }

    /**
     * 设置排序
     *
     * @param searchMap 条件
     * @param query     查询对象
     */
    private void setSort(Map searchMap, Query query) {
        // 获取查询域以及排序方式
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        // 设置排序
        if (sortValue != null && !sortValue.equals("")) {
            if(sortValue.equals("ASC")) {// 升序
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")) {// 升序
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }
    }

    /**
     * 设置分页选项，并将分页设置给查询对象
     *
     * @param searchMap 条件
     * @param query     查询对象
     */
    private void setPageOptions(Map searchMap, Query query) {
        // 获取当前页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null || pageNo < 1) {
            // 默认当前页码为1
            pageNo = 1;
        }
        // 获取每页显示记录数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null || pageSize < 1) {
            // 默认每页记录数为20
            pageSize = 20;
        }
        // 设置起始记录
        query.setOffset((pageNo - 1) * pageSize);
        // 设置每页记录数
        query.setRows(pageSize);
    }

    /**
     * 根据搜索条件集设置查询过滤条件,并将查询过滤条件设置给查询对象
     *
     * @param searchMap 搜索条件集
     * @param query     查询对象
     */
    private void setFilterOptions(Map searchMap, Query query) {
        // 设置分类过滤查询
        if (!"".equals(searchMap.get("category"))) {// 该条件不为空串
            // 创建查询过滤对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            // 设置过滤域,并设置条件
            Criteria criteria = new Criteria("item_category").is(searchMap.get("category"));
            // 将条件设置会查询过滤对象
            filterQuery.addCriteria(criteria);
            // 设置到查询对象中
            query.addFilterQuery(filterQuery);
        }

        // 设置品牌过滤查询
        if (!"".equals(searchMap.get("brand"))) {// 该条件不为空串
            // 创建查询过滤对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            // 设置过滤域,并设置条件
            Criteria criteria = new Criteria("item_brand").is(searchMap.get("brand"));
            // 将条件设置会查询过滤对象
            filterQuery.addCriteria(criteria);
            // 设置到查询对象中
            query.addFilterQuery(filterQuery);
        }

        // 设置规格过滤查询
        if (searchMap.get("spec") != null) {// 该条件不为空
            // 设置过滤域,并设置条件
            Map<String, String> map = (Map<String, String>) searchMap.get("spec");
            for (String key : map.keySet()) {
                Criteria criteria = new Criteria("item_spec_" + key).is(map.get(key));
                // 通过过滤条件创建查询过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                // 设置到查询对象中
                query.addFilterQuery(filterQuery);
            }

        }

        // 设置价格区间
        if (!searchMap.get("price").equals("")) {
            // 处理条件
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!price[0].equals("0")) {// 价格不等于0
                // 设置价格下限
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(criteria);
                // 设置到查询对象中
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")) {// 有上限
                // 设置价格上限
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(criteria);
                // 设置到查询对象中
                query.addFilterQuery(filterQuery);
            }
        }
    }

    /**
     * 查询分类列表
     *
     * @param searchMap 查询条件
     * @return java.util.List<java.lang.String>
     */
    private List<String> searchCategoryList(Map searchMap) {
        // 创建查询对象
        Query query = new SimpleQuery("*:*");
        // 按item_keywords查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        /*
         * 创建分组选项对象
         *          addGroupByField：设置分组的域
         */
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        // 将分组选项设置到查询对象中
        query.setGroupOptions(groupOptions);
        // 执行查询
        GroupPage<TbItem> pageInfo = solrTemplate.queryForGroupPage(query, TbItem.class);

        // 从结果集中获取数据,参数为分组域中的一个
        GroupResult<TbItem> groupResult = pageInfo.getGroupResult("item_category");
        // 获取分组页对象
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        // 从分组页对象中获取分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        // 遍历集合获取分组数据,并设置到返回结果集合中
        List<String> resultList = new ArrayList<>();
        for (GroupEntry<TbItem> entry : content) {
            resultList.add(entry.getGroupValue());
        }

        return resultList;
    }

    /**
     * 通过分类名称获取品牌和规格列表
     *
     * @param category 分类名称
     * @return java.util.Map
     */
    private Map<String, Object> searchBrandAndSpec(String category) {
        // 使用redis查询分类id
        Long categoryId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        // 通过分类id从redis中查询品牌列表和规格列表
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(categoryId);
        List specList = (List) redisTemplate.boundHashOps("specList").get(categoryId);

        // 将数据放入返回结果中
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("brandList", brandList);
        resultMap.put("specList", specList);

        return resultMap;
    }


}

