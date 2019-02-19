/** 定义控制器层 */
app.controller('itemCatController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    /*$scope.search = function(page, rows){
        baseService.findByPage("/itemCat/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /!** 获取分页查询结果 *!/
                $scope.dataList = response.data.rows;
                /!** 更新分页总记录数 *!/
                $scope.paginationConf.totalItems = response.data.total;
            });
    };*/
    $scope.idd = 0;

    $scope.search = function(page, rows){
        $scope.findItemCatByParentId(page,rows);
    };

    $scope.findItemCatByParentId = function (page,rows) {
        baseService.sendGet("/itemCat/findItemCatByParentId","page="+page+"&rows="+rows+"&parentId="+$scope.idd)
            .then(function (response) {
                $scope.dataList = response.data.rows;
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        if ($scope.itemCat.id){
            url = "update";
        } else {
            $scope.itemCat.parentId = $scope.idd;
        }
        /** 发送post请求 */
        baseService.sendPost("/itemCat/" + url, $scope.itemCat)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    /*$scope.reload();*/
                    $scope.findItemCatByParentId($scope.paginationConf.currentPage,
                        $scope.paginationConf.itemsPerPage);
                    $scope.itemCat = null;
                }else{
                    alert("操作失败！");
                }
            });
    };

    $scope.changeEntityToNull = function () {
        $scope.itemCat = null;
    }

    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.itemCat = entity;
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/itemCat/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.findItemCatByParentId($scope.paginationConf.currentPage,
                            $scope.paginationConf.itemsPerPage);
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };

    $scope.grade = 1;
    $scope.selectList = function (entity,grade) {
        $scope.idd = entity.id;
        $scope.grade = grade;
        if(grade == 1){
            $scope.itemCat_1 = null;
            $scope.itemCat_2 = null;
        } else if(grade == 2){
            $scope.itemCat_1 = entity;
            $scope.itemCat_2 = null;
        } else {
            $scope.itemCat_2 = entity;
        }
        $scope.paginationConf.currentPage = 1;
        $scope.paginationConf.itemsPerPage = 10;
        $scope.findItemCatByParentId(1,10);
    };

    $scope.findTypeTemplateList = function () {
        baseService.sendGet("/typeTemplate/findTypeTemplateList")
            .then(function (response) {
                $scope.typeTemplateList = response.data;
            });
    };
});