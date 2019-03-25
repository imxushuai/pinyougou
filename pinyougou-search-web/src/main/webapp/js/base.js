// 自定义模块
var app = angular.module('pinyougou', []);

// 定义过滤器
app.filter('trustHtml',['$sce',function ($sce) {
    // data为要过滤的内容
    return function (data) {
        // 使用$sce的trustAsHtml方法信任该html内容
        return $sce.trustAsHtml(data);
    }
    
}]);