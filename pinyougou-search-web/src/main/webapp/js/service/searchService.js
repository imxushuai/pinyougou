app.service('searchService',function ($http) {
    
    // 搜索
    this.search = function (searchMap) {
        return $http.post("itemsearch/search.do", searchMap);
    }
    
});