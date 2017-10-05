angular.module("todoList", ["ngRoute", "ngResource", "ngCookies"])
    .constant("baseURL", "http://localhost:8024/cxf/todo_list")
    .config(["$httpProvider", "$routeProvider", function ($httpProvider, $routeProvider) {
        $httpProvider.defaults.headers.common['Access-Control-Allow-Headers'] = '*';
        $httpProvider.defaults.withCredentials = true;
        $routeProvider.when('/', {
            templateUrl: 'login.html'
        })
            .when('/list', {
                templateUrl: 'list.html'
            })
            .otherwise({
                redirectTo: '/'
            });
    }])
    .run(function($rootScope) {
        $rootScope.user = {id: 0, username: ""};

    })
    .controller("loginCtrl",function ($scope, $rootScope, $http, baseURL, $cookies, $location) {
        $scope.createUser = function (user) {
            $http({
                method: 'POST',
                url: baseURL + "/create_user",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: {username: user.username, password: user.password}
            }).then(function success(response) {
                    $scope.alertType = "";
                    $rootScope.user.username = user.username;
                    $rootScope.user.id = response.data;
                    $location.path("/list");
                },

                function error(response) {
                    if (response.status === 302) {
                        $scope.alertType = $scope.alerts[0];
                    }

                    if (response.status === 500) {
                        $scope.alertType = $scope.alerts[2];
                    }
                });

        };


        $scope.loginUser = function (user) {
            $http({
                method: 'POST',
                url: baseURL + "/login_user",
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                withCredentials: true,
                data: {username: user.username, password: user.password}
            }).then(function success(response) {
                    $scope.alertType = "";
                    $rootScope.user.username = user.username;
                    $rootScope.user.id = response.data;
                    $location.path("/list");
                },

                function error(response) {
                    if (response.status === 401) {
                        $scope.alertType = $scope.alerts[1];
                    }

                    if (response.status === 404) {
                        $scope.alertType = $scope.alerts[3];
                    }

                    if (response.status === 500) {
                        $scope.alertType = $scope.alerts[2];
                    }
                });

        };

    })
    .controller("listCtrl", function ($scope, $rootScope, $http, baseURL, $cookies) {
        $scope.alerts = ["User already exists", "Wrong login or password", "Server error", "User not found"];

        $scope.alertType = "";


        //should filled with func from rest
        $scope.items = [];

        $scope.lists = [];
        $scope.fillLists = function () {
            $http({
                method: 'GET',
                url: baseURL + "/get_lists_by_user_id",
                headers: {
                    Authorization:'JWT '+ $cookies.get("X-JWT-AUTH")},
                params:{user_id:$rootScope.user.id}
            }).then(function success(response) {
                $scope.lists=response.data;
            });
        };





        $scope.createList = function (newList) {

            $http({
                method: 'POST',
                url: baseURL + "/create_list",
                headers: {
                    'Content-Type': 'application/json',
                Authorization:'JWT '+ $cookies.get("X-JWT-AUTH")},
                withCredentials: true,
                data: {name: newList.name,userId:$rootScope.user.id}
            }).then(function success(response) {
                    $scope.lists.push({

                        id: response.data,
                        name: newList.name,
                        userId: $rootScope.user.id
                    });
                });

        };

        $scope.addItem = function (item) {
            $http({
                method: 'POST',
                url: baseURL + "/create_item",
                headers: {
                    'Content-Type': 'application/json',
                    Authorization:'JWT '+ $cookies.get("X-JWT-AUTH")},
                withCredentials: true,
                data: {name: item.name,done:item.done,listId:item.listId}
            }).then(function success(response) {
                $scope.items.push({
                    id:response.data,
                    name: item.name,
                    done: item.done,
                    listId: item.listId});
            });

        };

        $scope.updateItem = function (item) {
            //$http.post
            $scope.items.splice(item, 1);
            $scope.items.splice(item, 0, item);

            for (i = 0; i < $scope.items.length; i++) {
                console.log($scope.items[i])
            }

        };

        $scope.deleteAllLists = function () {
            ///$http call
            $scope.lists = []
        };

        $scope.clearList = function (listId) {
            ///$http call

            removeByAttr($scope.items, 'listId', listId)

        };

        $scope.deleteList = function (listId) {
            ///$http call
            removeByAttr($scope.items, 'listId', listId);
            removeByAttr($scope.lists, 'id', listId);
        };

        $scope.deleteItem = function (itemId) {
            ///$http call
            $scope.items.splice(itemId, 1);
        };


        $scope.removeByAttr = function (arr, attr, value) {
            var i = arr.length;
            while (i--) {
                if (arr[i]
                    && arr[i].hasOwnProperty(attr)
                    && (arguments.length > 2 && arr[i][attr] === value )) {

                    arr.splice(i, 1);

                }
            }
            return arr;
        };
    });
