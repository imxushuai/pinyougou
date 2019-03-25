//控制层
app.controller('indexController' ,function($scope,$controller,loginService){

    // 展示当前登录用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (rtn) {
                $scope.loginName = rtn.loginName;
            }
        );
    }
    
});