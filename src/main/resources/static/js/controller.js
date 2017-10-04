angular.module("todoList", ["ngRoute", "ngResource", "ngCookies"])
    .constant("baseURL", "http://localhost:8024/cxf/todo_list")
    .config(["$httpProvider", "$routeProvider", function ($httpProvider, $routeProvider) {
        //$httpProvider.defaults.headers.common['Access-Control-Allow-Headers'] = '*';

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

    .controller("defaultCtrl", function ($scope, $http, baseURL, $cookies, $location) {
        $scope.alerts = ["User already exists", "Wrong login or password", "Server error", "User not found"];

        $scope.alertType = "";


        //should filled with func from rest
        $scope.items = [];

        //should filled with func from rest
        $scope.lists = [];

        $scope.user = {id: 0, username: ""};

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
                withCredentials: true,
                data: {username: user.username, password: user.password}
            }).then(function success(response) {
                    $scope.alertType = "";
                    $scope.user.username = user.username;
                    $scope.user.id = response.data;
                    $location.path("/list");
                    window.alert($cookies.get("X-JWT-AUTH"));
                },

                function error(response) {
                    if (response.status === 302) {
                        $scope.alertType = $scope.alerts[0];
                    }

                    if (response.status === 500) {
                        $scope.alertType = $scope.alerts[2];
                    }
                }
            );

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
                    $scope.user.username = user.username;
                    $scope.user.id = response.data;
                    $location.path("/list");
                    window.alert($cookies.get("X-JWT-AUTH"));

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

        $scope.createList = function (newList) {
            $scope.lists.push({

                id: newList.id,
                name: newList.name,
                userId: $scope.user.id
            });


            //add http post for new list
        };

        $scope.addItem = function (item) {
            $scope.items.push({name: item.name, done: false, listId: item.listId});
            //add http post for new list
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


        $scope.setScreen = function (index) {

            $scope.current = $scope.screens[index];

        };

        $scope.getScreen = function () {

            return $scope.current === "List" ? 'list.html' : 'login.html';

        };

    })
;
