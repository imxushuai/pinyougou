 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService,goodsService,brandService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// 显示图片列表
                $scope.entity.goodsDesc.itemImages= JSON.parse($scope.entity.goodsDesc.itemImages);
                // 显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=  JSON.parse($scope.entity.goodsDesc.customAttributeItems);
            }
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
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

	// 监控品牌ID
    $scope.$watch('entity.goods.brandId', function (newValue, oldValue) {
		brandService.findOne(newValue).success(
			function (rtn) {
				$scope.brandName = rtn.name;
			}
		);
    });

    // 修改商品状态
	$scope.updateStatus = function (status) {
		goodsService.updateStatus($scope.selectIds,status).success(
			function (rtn) {
				alert(rtn.message);
				if(rtn.success){
					// 刷新页面
					$scope.reloadList();
					// 清空selectIds中的数据
                    $scope.selectIds = [];
				}
            }
		);
    }


    
});	
