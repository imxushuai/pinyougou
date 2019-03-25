 //控制层 
app.controller('userController' ,function($scope,$controller,userService){

    // 注册方法
    $scope.reg = function () {
        if ($scope.entity == null || $scope.entity.username == null || $scope.entity.username.trim() == '' ||
            $scope.entity.password == null || $scope.entity.password.trim() == '' || $scope.entity.phone == null ||
            $scope.entity.phone.trim() == '') {
            alert("请完成填写您的资料");
            return ;
        }

        if ($scope.entity.password != $scope.password) {
            alert("两次输入的密码不一致!");
            return ;
        }
        // alert(JSON.stringify($scope.entity));
        userService.add($scope.entity, $scope.smsCode).success(
            function (rtn) {
                alert(rtn.message);
            }
        );
    }

    // 发送短信验证码
    $scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone.trim() == "") {
            alert("请正确填写手机号!");
            return ;
        }
        userService.sendCode($scope.entity.phone).success(
            function (rtn) {
                alert(rtn.message);
            }
        );
    }


    
});	
