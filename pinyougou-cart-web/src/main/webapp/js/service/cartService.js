//购物车服务层
app.service('cartService', function ($http) {
    //购物车列表
    this.findCartList = function () {
        return $http.get('cart/findCartList.do');
    }
    //添加商品到购物车
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    }
    // 合计金额
    this.sum = function (cartList) {
        // 返回结果集
        var totalValue = {totalNum: 0, totalMoney: 0};
        // 遍历购物车列表
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                // 累加数量
                totalValue.totalNum += cart.orderItemList[j].num;
                // 累加金额
                totalValue.totalMoney += cart.orderItemList[j].totalFee;
            }
        }
        return totalValue;
    }

    // 获取当前登录用户的收货地址列表
    this.findListByLoginUser = function () {
        return $http.get('address/findListByLoginUser.do');
    }

    // 新增收货地址
    this.addAddress = function (entity) {
        return $http.post('address/add.do',entity);
    }
    // 修改收货地址
    this.updateAddress = function (entity) {
        return $http.post('address/update.do',entity);
    }
    // 获取收货地址数据
    this.findAddressById = function (addressId) {
        return $http.get('address/findOne.do?id=' + addressId);
    }
    // 删除收货地址
    this.deleteAddress = function (addressId) {
        return $http.get('address/delete.do?id=' + addressId);
    }

    // 提交订单
    this.submitOrder = function (order) {
        return $http.post('order/add.do', order);
    }


});
