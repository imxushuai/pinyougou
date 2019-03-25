//控制层
app.controller('seckillGoodsController' ,function($scope,$location,$interval,seckillGoodsService){
    //读取列表数据绑定到表单中
    $scope.findList=function(){
        seckillGoodsService.findList().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //查询实体
    $scope.findOne=function(){
        seckillGoodsService.findOne($location.search()['id']).success(
            function(response){
                $scope.entity= response;

                // 计算当前时间与结束时间的毫秒数
                timeSecond = Math.floor((new Date($scope.entity.endTime).getTime() - new Date().getTime()) / 1000);
                time = $interval(function () {
                    if (timeSecond > 0) {
                        timeSecond = timeSecond - 1;
                        // 拼接时间字符串
                        $scope.timeString = convertTimeString(timeSecond);
                    } else {
                        $interval.cancel(time);
                    }
                },1000);
            }
        );
    }

    // 拼装时间字符串
    convertTimeString = function (second) {
        // 计算当前天数
        var days = Math.floor(second / (60*60*24));
        // 计算当前小时数
        var hours = Math.floor((second - days*60*60*24) / (60*60));

        // 计算当前分钟数
        var minutes = Math.floor((second - days*60*60*24 - hours*60*60) / 60);

        // 计算当前描述
        var seconds = Math.floor((second - days*60*60*24 - hours*60*60 - minutes*60));

        // 格式化
        var dayString = "";
        if (days > 0) {
            dayString = days + "天  ";
        }
        var hoursString = hours + ":";
        if (hours < 10) {
            hoursString = "0" + hours + ":";
        }
        var minutesString = minutes + ":";
        if (minutes < 10) {
            minutesString = "0" + minutes + ":";
        }
        var secondsString = "" + seconds;
        if (seconds < 10) {
            secondsString = "0" + seconds;
        }

        return dayString + hoursString + minutesString  + secondsString;
    }

    //提交订单
    $scope.submitOrder=function(){
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function(response){
                if(response.success){
                    alert("下单成功，请在5分钟内完成支付");
                    location.href="pay.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }


});	
