//服务层
app.service('loginService',function($http){
    // 获取当前登录用户名
    this.showName = function () {
        return $http.get('../login/showName.do');
    }
});
