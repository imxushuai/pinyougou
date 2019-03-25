//自定义服务
app.service('brandService', function ($http) {

    // 分页查询品牌列表
    this.findByPage = function (page, size) {
        return $http.get('../brand/findByPage.do?page=' + page + '&size=' + size);
    };

    // 新增品牌
    this.add = function (entity) {
        return $http.post('../brand/add.do', entity);
    };

    // 按id查询品牌
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    };

    // 修改品牌
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    };

    // 删除品牌
    this.dele = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    }

    // 查询品牌
    this.search = function (page,size,queryParam) {
        return $http.post('../brand/search.do?page=' + page + '&size=' + size, queryParam);
    }

    // 获取品牌下拉列表需要的数据集
    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList.do');
    }

});