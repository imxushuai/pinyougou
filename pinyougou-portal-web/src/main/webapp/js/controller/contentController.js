app.controller('contentController',function ($scope,contentService) {

    // 所有广告的列表
    $scope.contentList = [];
    // 查询指定广告分类的广告
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (rtn) {
                // 将categoryId作为下标，将返回结果存入广告列表
                $scope.contentList[categoryId] = rtn;
            }
        );
    }

    // 搜索
    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }
});