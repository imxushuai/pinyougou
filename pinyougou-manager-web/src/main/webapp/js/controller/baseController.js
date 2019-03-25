app.controller('baseController', function ($scope) {
    //分页控件配置
    $scope.paginationConf = {
        //当前页
        currentPage: 1,
        //总记录数
        totalItems: 10,
        //每页记录数
        itemsPerPage: 10,
        //可以选择的每页记录数
        perPageOptions: [10, 20, 30, 40, 50],
        //选择每页记录数事件
        onChange: function () {
            $scope.reloadList();
        }
    };
//重新加载
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

//记录被勾选的id值
    $scope.selectIds = [];

    /*
        更新被勾选的id值
     */
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//选中状态
            //将id值加入selectIds
            $scope.selectIds.push(id);
        } else {//取消选中
            //获取id值的下标
            var index = $scope.selectIds.indexOf(id);
            //移除id
            $scope.selectIds.splice(index, 1);
        }
    };

    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString = function (jsonString, key) {
        var json = JSON.parse(jsonString);//将json字符串转换为json对象
        var value = "";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ","
            }
            value += json[i][key];
        }
        return value;
    }

});