/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/seller/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){

        /** 发送post请求 */
        baseService.sendPost("/seller/save", $scope.seller)
            .then(function(response){
                if (response.data){
                    /** 跳转到商家登录页面 */
                    location.href = "/shoplogin.html";
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/seller/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };

    /*查询资料（修改资料回显数据）*/
    $scope.search1=function () {
        /** 发送post请求 */
         baseService.sendGet("/seller/sear?sellerId="+localStorage.getItem("loginName"))
            .then(function (response) {
                 $scope.data1=response.data;
             });
    };

    /*修改资料*/
    $scope.save1=function () {
      /*  $scope.seller=localStorage.getItem("data1");*/
        /** 发送post请求 */
        baseService.sendPost("/seller/save1",$scope.data1)
            .then(function (response) {
                if(response.data){
                    alert("保存成功")
                    /** 重新加载数据 */
                    $scope.reload();
                }else {
                    alert("操作失败")
                }
            });
    }
});