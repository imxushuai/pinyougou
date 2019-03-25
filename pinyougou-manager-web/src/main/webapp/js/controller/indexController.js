app.controller('indexController', function ($scope, loginService) {

    // 显示当前登录用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (rtn) {
                $scope.loginName = rtn.loginName;
            }
        );
    }
});