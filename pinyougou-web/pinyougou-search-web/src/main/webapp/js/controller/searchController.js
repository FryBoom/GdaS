/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope,$location,$controller,baseService) {

    /** 指定继承baseController */
    $controller("baseController", {$scope:$scope});

    $scope.searchParam = {keywords:'',category:'',brand:'',price:'',spec:{},page:1,rows:10,sortField:'',sort:''};

    $scope.getkeywords = function () {
        $scope.searchParam.keywords = $location.search().keywords;
        $scope.search();
    };

    $scope.addSearchItem = function (key,value) {
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchParam[key] = value;
        } else {
            $scope.searchParam.spec[key] = value;
        }
        $scope.search();
    };

    $scope.removeSearchItem = function (key) {
      if(key == 'category' || key == 'brand' || key == 'price'){
          $scope.searchParam[key] = "";
      }else {
          delete $scope.searchParam.spec[key];
      }
      $scope.search();
    };
    var initPageNum = function () {
        /** 定义页码数组 */
        $scope.pageNums = [];
        /** 获取总页数 */
        var totalPages = $scope.resultMap.totalPages;
        /** 开始页码 */
        var firstPage = 1;
        /** 结束页码 */
        var lastPage = totalPages;
        /** 前面有点 */
        $scope.firstDot = true;
        /** 后面有点 */
        $scope.lastDot = true;
        /** 如果总页数大于5，显示部分页码 */
        if(totalPages > 5){
            // 如果当前页码处于前面位置
            if($scope.searchParam.page <= 3){
                lastPage = 5;
                $scope.firstDot = false;
            } else if($scope.searchParam.page >= totalPages - 3){
                firstPage = totalPages - 4;
                $scope.lastDot = false;
            } else {
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }else {
            $scope.firstDot = false; // 前面没点
            $scope.lastDot = false; // 后面没点
        }
        for (var i = firstPage; i <= lastPage ; i++){
            $scope.pageNums.push(i);
        }
    };

    $scope.search = function () {
      baseService.sendPost("/Search",$scope.searchParam).then(function (response) {
          $scope.resultMap = response.data;
          initPageNum();
      });
    };


    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };
    
    $scope.pageSearch = function (page) {
        page = parseInt(page);
        if(page >= 1 && page <= $scope.resultMap.totalPages && page != $scope.searchParam.page){
            $scope.searchParam.page = page;
            $scope.search();
        }
    };

    $scope.sortSearch = function (sortField,sort) {
      $scope.searchParam.sortField = sortField;
      $scope.searchParam.sort = sort;
      $scope.search();
    };
});
