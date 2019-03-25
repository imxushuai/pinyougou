// 购物车控制层
app.controller('cartController', function ($scope, cartService) {
    // 查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum(response);
            }
        );
    }

    // 添加商品到购物车
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();//刷新列表
                } else {
                    alert(response.message);//弹出错误提示
                }
            }
        );
    }

    // 获取当前用户的收货地址列表
    $scope.findListByLoginUser = function () {
        cartService.findListByLoginUser().success(
            function (rtn) {
                $scope.addressList = rtn;
                // 选中默认收货地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {// 为默认收货地址
                        $scope.address = $scope.addressList[i];
                    }
                }
            }
        );
    }

    // 选择收货地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    // 判断当前收货地址是否是被选中地址
    $scope.isSelectedAddress = function (address) {
        if ($scope.address == address) {
            return true;
        }
        return false;
    }
    
    // 保存收货地址
    $scope.saveAddress = function () {
        var object = null;
        if ($scope.entity.id != null) {
            object = cartService.updateAddress($scope.entity);
        } else {
            object = cartService.addAddress($scope.entity);
        }
        object.success(
            function (rtn) {
                alert(rtn.message);
                if (rtn.success) {
                    $scope.entity = {};
                    // 重新加载数据
                    $scope.findListByLoginUser();
                }
            }
        );
    }

    // 选择地址别名
    $scope.selectAddressAlias = function (value) {
        $scope.entity.alias = value;
    }

    // 获取地址数据
    $scope.findAddressById = function (addressId) {
        cartService.findAddressById(addressId).success(
            function (rtn) {
                $scope.entity = rtn;
            }
        );
    }
    // 删除收货地址
    $scope.deleteAddress = function (addressId) {
        cartService.deleteAddress(addressId).success(
            function (rtn) {
                alert(rtn.message);
                if (rtn.success) {
                    // 重新加载数据
                    $scope.findListByLoginUser();
                }
            }
        )
    }

    // 选择付款方式
    $scope.order = {paymentType:'1'};
    $scope.selectPayment = function (type) {
        $scope.order.paymentType = type;
    }

    // 提交订单
    $scope.submitOrder = function () {
        // 封装数据
        $scope.order.receiverAreaName = $scope.address.address;// 地址
        $scope.order.receiverMobile = $scope.address.mobile;// 手机
        $scope.order.receiver = $scope.address.contact;// 联系人

        cartService.submitOrder($scope.order).success(
            function (rtn) {
                if (rtn.success) {
                    // 提交订单成功,跳转到支付页面
                    if ($scope.order.paymentType == "1") {// 微信支付 跳转到支付页面
                        location.href = 'pay.html';
                    } else {// 货到付款 跳转到提示页面
                        location.href = 'paysuccess.html';
                    }
                } else {
                    alert(rtn.message);
                }
            }
        );
    }

});
