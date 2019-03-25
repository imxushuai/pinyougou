app.service('contentService',function ($http) {

    // 获取指定广告类型的广告
    this.findByCategoryId = function (categoryId) {
        return $http.get('content/findByCategoryId.do?categoryId=' + categoryId);
    }
})