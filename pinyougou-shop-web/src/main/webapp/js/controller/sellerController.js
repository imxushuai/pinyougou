 //控制层 
app.controller('sellerController' ,function($scope,$controller,sellerService,loginService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		sellerService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		sellerService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		sellerService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//新增
	$scope.add=function(){
        sellerService.add( $scope.entity  ).success(
			function(response){
				if(response.success){
					// 成功跳转到登录页
					location.href = "shoplogin.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}



    //批量删除
	$scope.dele=function(){			
		//获取选中的复选框			
		sellerService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		sellerService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	// 使用id加载当前商家信息
	$scope.sellerId = "";
	$scope.loadId = function () {
		loginService.sellerId().success(
			function (rtn) {
                sellerId = JSON.parse(rtn);
                $scope.findOne(sellerId);
            }
		);
    }


    //更新
    $scope.update=function(){
        sellerService.update( $scope.entity  ).success(
            function(response){
                if(response.success){
                    alert(response.message);
                    $scope.loadId();
                }else{
                    alert(response.message);
                }
            }
        );
    }

    // 修改密码
	$scope.updatePassword = function () {
        //校验两次密码是否一致
		if($scope.newPwd != $scope.newPwd1) {
			alert("两次密码输入不一致!");
		} else {
			$scope.password={oldPwd:$scope.oldPwd,newPwd:$scope.newPwd};
			sellerService.updatePassword($scope.password).success(
				function (rtn) {
					alert(rtn.message);
                }
			);
		}
    }
    
});	
