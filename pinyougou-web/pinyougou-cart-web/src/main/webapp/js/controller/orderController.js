app.controller("orderController",function ($scope,$controller,$interval,$location,baseService) {
    /** 指定继承cartController */
    $controller("cartController", {$scope:$scope});

    $scope.findAddressByUser = function () {
        baseService.sendGet("/order/findAddressByUser").then(function (response) {
            $scope.addressList = response.data;
            for(var i in response.data){
                if(response.data[i].isDefault == 1){
                    $scope.address = response.data[i];
                    break;
                }
            }
        });
    };

    $scope.selectAddress = function (item) {
        $scope.address = item;
    };

    $scope.isSelectedAddress = function (item) {
        return item == $scope.address;
    };

    $scope.order = {paymentType : '1'};
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    /** 保存订单 */
    $scope.saveOrder = function () {
        // 设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        // 设置收件人手机号码
        $scope.order.receiverMobile = $scope.address.mobile;
        // 设置收件人
        $scope.order.receiver = $scope.address.contact;
        // 发送异步请求
        baseService.sendPost("/order/save",$scope.order).then(function (response) {
            if(response.data){
                // 如果是微信支付，跳转到扫码支付页面
                if($scope.order.paymentType == 1){
                    location.href = "/order/pay.html";
                }else {
                    // 如果是货到付款，跳转到成功页面
                    location.href = "/order/paysuccess.html";
                }
            }else {
                alert("订单提交失败!");
            }
        });
    };

    
    $scope.genPayCode = function () {
        baseService.sendGet("/order/genPayCode").then(function (response) {
            /** 获取金额(转化成元) */
            $scope.money = (response.data.totalFee/100).toFixed(2);
            /** 获取订单交易号 */
            $scope.outTradeNo = response.data.outTradeNo;
            /** 生成二维码 */
            var qr = new QRious({
                element : document.getElementById('qrious'),
                size : 250,
                level : 'H',
                value : response.data.codeUrl
            });
            /**
             * 开启定时器
             * 第一个参数：调用的函数
             * 第二个参数：时间毫秒数(3000毫秒也就是3秒)
             * 第三个参数：调用的总次数(60次)
             * */
            var timer = $interval(function () {
                /** 发送请求，查询支付状态 */
                baseService.sendGet("/order/queryPayStatus?outTradeNo="+$scope.outTradeNo)
                    .then(function (response) {
                        if(response.data.status == 1){
                            $interval.cancel(timer);
                            location.href = "/order/paysuccess.html?money="
                                + $scope.money;
                        }
                        if(response.data.status == 3){
                            $interval.cancel(timer);
                            location.href = "/order/payfail.html";
                        }
                    });
            },30000,60);
            /** 执行60次(3分钟)之后需要回调的函数 */
            timer.then(function () {
               alert("微信支付二维码失效！");
            });
        });
    };

    /** 获取支付总金额 */
    $scope.getMoney = function(){
        return $location.search().money;
    };

});