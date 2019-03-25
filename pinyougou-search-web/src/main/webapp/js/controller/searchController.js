app.controller('searchController',function ($scope,$location,searchService) {

    // 初始化搜索对象,包括分类名称、品牌、规格选项
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},
        'price':'','pageNo':1,'pageSize':20,'sort':'','sortField':''};

    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (rtn) {
                $scope.resultMap = rtn;
                buildPageLabel();
            }
        );
    }

    buildPageLabel = function () {
        // 分页属性
        $scope.pageLabel = [];
        // 总页数
        var maxPageNo = $scope.resultMap.totalPages;
        // 起始页
        var firstPage = 1;
        // 最后一页
        var lastPage = maxPageNo;
        if ($scope.resultMap.totalPages > 5) {// 如果总页数大于5
            // 判断特殊情况
            if ($scope.searchMap.pageNo <= 3) {// 页码下限越界
                lastPage = 5;
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {// 页码上限越界
                firstPage = maxPageNo - 4;
            } else {// 正常情况
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }

        }
        // 遍历总页数生成分页属性
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }

    }

    // 分页查询
    $scope.searchByPage = function(pageNo){
        pageNo = parseInt(pageNo);
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return ;
        }
        // 设置当前页码信息到查询条件中
        $scope.searchMap.pageNo = pageNo;
        // 执行查询
        $scope.search();
    }

    // 添加搜索选项
    $scope.addSearchOptions = function (key, value) {
        // 判断key值
        if (key == 'category' || key == 'brand' || key == 'price') {// 分类名称、品牌和价格区间
            // 将查询条件值放入对应的key
            $scope.searchMap[key] = value;
        } else {// 规格选项
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

    // 移除搜索选项
    $scope.removeSearchOptions = function (key) {
        // 判断key值
        if (key == 'category' || key == 'brand' || key == 'price') {// 分类名称、品牌和价格区间
            // 设置为空字符串
            $scope.searchMap[key] = '';
        } else {// 规格选项
            // 使用delete关键字移除该key
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    // 判断当前页是否是第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }
    // 判断当前页是否是最后一页
    $scope.isEndPage = function () {
        if ($scope.resultMap.totalPages == $scope.searchMap.pageNo) {
            return true;
        } else {
            return false;
        }
    }

    // 排序搜索
    $scope.sortSearch = function (sortField, sort) {
        // 设置排序字段和排序方式到查询对象中
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        // 执行查询
        $scope.search();
    }

    // 判断关键字是否包含品牌信息
    $scope.keywordsIsBrand = function () {
        // 遍历查询结果中的品牌列表
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            // 判断keywords中是否包含品牌信息
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) > -1) {// 包含
                return true;
            }
        }
        return false;
    }

    // 接受首页传送过来的关键字
    $scope.loadKeywords = function () {
        // 将首页传送的关键字赋值给查询对象
        $scope.searchMap.keywords = $location.search()['keywords'];
        // 执行查询
        $scope.search();
    }


});