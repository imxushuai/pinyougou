//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        // 查看是否有参数传递
        var id = $location.search()['id'];
        if (id == null) {// 没有
            return;
        }
        // 有id参数传递,查询该id的数据
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                // 加载商品介绍数据到富文本编辑器中
                editor.html($scope.entity.goodsDesc.introduction);
                // 将商品图片列表转换为json对象
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                // 将商品扩展属性转换为json对象
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                // 将商品规格转换为json对象
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                // 转换SKU商品列表中的规格对象
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //增加商品
    $scope.save = function () {
        // 将富文本编辑器中的内容,赋值给商品描述对象
        $scope.entity.goodsDesc.introduction = editor.html();

        var serviceObject;
        if($scope.entity.goods.id != null) {
            serviceObject = goodsService.update($scope.entity);
        } else {
            serviceObject = goodsService.add($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    // 显示新增成功
                    alert("新增成功");
                    location.href = 'goods.html';
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    // 上传文件
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (rtn) {
                if (rtn.success) {
                    $scope.image_entity.url = rtn.message;
                } else {
                    alert(rtn.message);
                }
            }
        );
    }

    // 初始化
    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}}
    // image_entity到表格中
    $scope.add_image_entity = function () {
        // 将文件信息放入item
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    // 删除表格中展示的图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    // 展示一级下拉列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (rtn) {
                $scope.itemCat1List = rtn;
            }
        );
    }

    // 关联展示二级下拉列表
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        // alert(newValue);
        itemCatService.findByParentId(newValue).success(
            function (rtn) {
                $scope.itemCat2List = rtn;
            }
        );
    });

    // 关联展示三级下拉列表
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        // alert(newValue);
        itemCatService.findByParentId(newValue).success(
            function (rtn) {
                $scope.itemCat3List = rtn;
            }
        );
    });

    // 关联展示模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        // alert(newValue);
        itemCatService.findOne(newValue).success(
            function (rtn) {
                $scope.entity.goods.typeTemplateId = rtn.typeId;
            }
        );
    });

    // 关联加载品牌下拉列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        // alert(newValue);
        typeTemplateService.findOne(newValue).success(
            function (rtn) {
                $scope.typeTemplate = rtn;
                // 将字符串转换为json对象
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                // 判断是否页面为修改时记载数据
                if ($location.search()['id'] == null) {// 不是
                    // 将扩展属性转换为json对象
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            }
        );
        // 读取规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (rtn) {
                $scope.specList = rtn;
            }
        );
    });

    // 更新specificationItems集合
    $scope.updateSpecAttribute = function ($event, name, value) {
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        // 判断是否存在该key的值
        if (object == null) {//不存在
            // 新增
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]})
        } else {// 存在
            if ($event.target.checked) {//勾选
                // 追加
                object.attributeValue.push(value);
            } else {// 取消勾选
                if (object.attributeValue.length > 1) {
                    object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                } else {
                    var index = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                    $scope.entity.goodsDesc.specificationItems.splice(index, 1);
                }
            }

        }
    }


    // 构建SKU商品列表
    $scope.createItemList = function () {
        // 初始化SKU商品列表
        $scope.entity.itemList = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];

        var specificationItems = $scope.entity.goodsDesc.specificationItems;

        for (var i = 0; i < specificationItems.length; i++) {
            // 用新生成的列表，覆盖久的列表
            $scope.entity.itemList = addColumn($scope.entity.itemList, specificationItems[i].attributeName, specificationItems[i].attributeValue);
        }
    }

    /**
     * 生成SKU列表的方法
     *
     * @param list          原集合
     * @param columnName    新生成列的列名
     * @param columnValues  新生成列的值列表
     * @return SKU列表
     */
    addColumn = function (list, columnName, columnValues) {
        var SKUList = [];
        for (var i = 0; i < list.length; i++) {
            // 取出当前集合中的行
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                // 对当前行对象进行深克隆
                var newRow = JSON.parse(JSON.stringify(oldRow));
                // 将值，追加到新增行
                newRow.spec[columnName] = columnValues[j];
                // 将新的行对象，添加到要SKU列表中
                SKUList.push(newRow);
            }
        }
        return SKUList;
    }

    // 状态对应的值列表
    /*
        数据库中状态的值本身为：0,1,2,3，刚好对应列表的下标
     */
    $scope.status = ['未审核', '已审核', '审核未通过', '已关闭'];

    // 初始化商品分类列表
    $scope.itemCatList = [];
    // 查询商品分类列表，并将其赋值给itemCatList，其中id为下标，name为值
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (rtn) {
                for (var i = 0; i < rtn.length; i++) {
                    // 以id为下标，name为值，放入商品分类列表
                    $scope.itemCatList[rtn[i].id] = rtn[i].name;
                }
            }
        );
    }

    // 根据规格选项数据选中对应复选框
    $scope.checkAttributeValue = function (specName, optionName) {
        // 在集合中查询是否有对应的值存在
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        // 判断是否存在
        if (object != null) { // 存在该规格
            // 继续判断规格中是否有该属性值
            var index = object.attributeValue.indexOf(optionName);
            if (index != -1) {// 存在该属性值
                return true;
            } else {// 不存在该属性值
                return false;
            }
        }
        // 不存在
        return false;
    }

});	
