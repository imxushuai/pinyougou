//自定义控制器
app.controller('brandController', function ($scope, $controller, brandService) {

    // 继承baseController
    $controller('baseController', {$scope: $scope});

    /*
        分页查询品牌列表
     */
    $scope.findByPage = function (page, size) {
        brandService.findByPage(page, size).success(
            function (rtn) {
                //显示当前页的数据
                $scope.list = rtn.rows;
                //修改总记录数
                $scope.paginationConf.totalItems = rtn.total;
            }
        );
    };

    /*
        新增品牌、修改品牌
     */
    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }
        object.success(
            function (rtn) {
                alert(rtn.message);
                if (rtn.success) {
                    //重新加载数据
                    $scope.reloadList();
                }
            }
        );
    };

    /*
        加载要修改的品牌到编辑品牌表单
     */
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (rtn) {
                $scope.entity = rtn;
            }
        );
    };


    /*
        删除操作
     */
    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(
            function (rtn) {
                alert(rtn.message);
                if (rtn.success) {
                    //重新加载数据
                    $scope.reloadList();
                }
            }
        );
    };

    /*
        条件查询
     */
    //初始化queryParam
    $scope.queryParam = {};
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.queryParam).success(
            function (rtn) {
                //显示当前页的数据
                $scope.list = rtn.rows;
                //修改总记录数
                $scope.paginationConf.totalItems = rtn.total;
            }
        );
    };


});