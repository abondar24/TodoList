angular.module('todoList', ['ngRoute', 'ngResource', 'ngCookies', 'ui.bootstrap', 'ui.bootstrap.modal', 'xeditable'])
    .constant('baseURL', 'http://localhost:8024/cxf/todo_list')
    .config(['$httpProvider', '$routeProvider', function ($httpProvider, $routeProvider) {
        $httpProvider.defaults.withCredentials = true;
        $routeProvider
            .when('/list', {
                templateUrl: 'list.html'
            })
            .when('/', {
                templateUrl: 'list.html'
            })
            .otherwise({
                redirectTo: '/'
            });
    }])
    .factory('appFactory', function ($http, $cookies, baseURL, $rootScope, $location) {

        function fillLists() {
            $http({
                method: 'GET',
                url: baseURL + '/get_lists_by_user_id',
                Authorization: $cookies.get('X-JWT-AUTH'),
                params: {user_id: $rootScope.user.id}
            }).then(function success(response) {
                $rootScope.lists = response.data;


                if ($rootScope.lists.length > 0) {
                    var listIds = [];
                    for (var i = 0; i < $rootScope.lists.length; i++) {
                        listIds.push($rootScope.lists[i].id);
                    }

                    fillItems(listIds);
                }
            });
        }

        function fillItems(listIds) {
            if (listIds.length === 1) {
                $http({
                    method: 'GET',
                    url: baseURL + '/get_items_for_list',
                    Authorization: $cookies.get('X-JWT-AUTH'),
                    params: {list_id: listIds[0]}
                }).then(function success(response) {
                    $rootScope.items = response.data;
                });

            } else {
                $http({
                    method: 'POST',
                    url: baseURL + '/get_items_for_lists',
                    Authorization: $cookies.get('X-JWT-AUTH'),
                    data: listIds
                }).then(function success(response) {
                    $rootScope.items = response.data;
                });

            }
        }

        return {
            loginUser: function (user) {
                $http({
                    method: 'POST',
                    url: baseURL + '/login_user',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function (obj) {
                        var str = [];
                        for (var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {username: user.username, password: user.password}
                }).then(
                    function success(response) {
                        $rootScope.user.username = user.username;
                        $rootScope.user.id = response.data;
                        if ($cookies.get('X-JWT-AUTH') !== undefined) {
                            $location.path('/list');
                            fillLists();

                            return response.status;
                        }
                    },

                    function error(response) {
                        return response.status;
                    })
            }

        }
    })
    .run(function ($rootScope, editableOptions) {
        editableOptions.theme = 'bs3';
        $rootScope.user = {id: 0, username: ""};
        $rootScope.items = [];

        $rootScope.lists = [];
        $rootScope.curListId = 0;
        $rootScope.curItem = {id: 0, name: "", done: false, listId: 0};


    })

    .controller('loginCtrl', function ($scope, $rootScope, appFactory,
                                       $http, baseURL, $cookies, $location, $uibModalInstance) {
        $scope.alerts = ["User already exists", "Wrong login or password", "Server error", "User not found"];

        $scope.alertType = "";
        $scope.createUser = function (user) {
            $uibModalInstance.close();
            $http({
                method: 'POST',
                url: baseURL + '/create_user',
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

                    if ($cookies.get('X-JWT-AUTH') !== undefined) {
                        $location.path('/list');
                    } else {

                    }
                },

                function error(response) {
                    if (response.status === 302) {
                        $scope.alertType = appFactory.alerts[0];
                    }

                    if (response.status === 500) {
                        $scope.alertType = appFactory.alerts[2];
                    }
                });

        };

        $scope.loginUser = function (user) {
            var status = appFactory.loginUser(user);

            if (status === 202) {
                $scope.alertType = ""
            }

            if (status === 401) {
                $scope.alertType = $scope.alerts[1];
            }

            if (status === 404) {
                $scope.alertType = $scope.alerts[3];
            }

            if (status === 500) {
                $scope.alertType = $scope.alerts[2];
            }
            $uibModalInstance.close();
        }
    })
    .controller('modalCtrl', function ($scope, $rootScope, $http, baseURL, $uibModalInstance, $cookies) {

        $scope.createList = function (newList) {
            $uibModalInstance.close();
            $http({
                method: 'POST',
                url: baseURL + '/create_update_list',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: $cookies.get('X-JWT-AUTH')
                },
                withCredentials: true,
                data: {name: newList.name, userId: $rootScope.user.id}
            }).then(function success(response) {
                $rootScope.lists.push({

                    id: response.data,
                    name: newList.name,
                    userId: $rootScope.user.id
                });
            });

        };

        $scope.addItem = function (item) {
            $uibModalInstance.close();
            $http({
                method: 'POST',
                url: baseURL + '/create_update_item',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: $cookies.get('X-JWT-AUTH')
                },
                withCredentials: true,
                data: {name: item.name, done: item.done, listId: $rootScope.curListId}
            }).then(function success(response) {
                $rootScope.items.push({
                    id: response.data,
                    name: item.name,
                    done: item.done,
                    listId: $rootScope.curListId
                });
            });
        };

        $scope.deleteItem = function () {
            $uibModalInstance.close();
            $http({
                method: 'GET',
                url: baseURL + '/delete_item',
                Authorization: $cookies.get('X-JWT-AUTH'),
                params: {item_id: $rootScope.curItem.id}
            }).then(function success(response) {
                var index = $rootScope.items.indexOf($rootScope.curItem);
                if (index > -1) {
                    $rootScope.items.splice(index, 1);
                }
            });

        };

        $scope.cancelDeleteItem = function () {
            $uibModalInstance.close();
            $rootScope.curItem = {id: 0, name: "", done: false, listId: 0};
        };
    })
    .controller('listCtrl', function ($scope, $rootScope, $http, baseURL, $uibModal, $cookies) {

        $scope.loginRequired = function () {
            if ($cookies.get('X-JWT-AUTH') !== undefined && $cookies.get('USER') !== undefined) {

                console.log($cookies.get('USER'));

                $http({
                    method: 'GET',
                    url: baseURL + '/find_user',
                    params:{user_id:$cookies.get('USER')},
                    transformRequest: function (obj) {
                        var str = [];
                        for (var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {username: $cookies.get('USER')}
                }).then(function success(response) {
                       console.log(response.status)
                       // appFactory.loginUser(user);
                    },
                    function (response) {
                        if (response.status === 404) {
                            $scope.loginWindow();
                        }
                    });

            } else {
                $scope.loginWindow();

            }
        };

        $scope.loginWindow = function () {
            $uibModal.open(
                {
                    templateUrl: 'loginModal.html',
                    controller: 'loginCtrl',
                    keyboard: false,
                    size: 'md'
                }
            );
        };

        $scope.listWindow = function () {
            $uibModal.open(
                {
                    templateUrl: 'listCreateModal.html',
                    controller: 'modalCtrl',
                    keyboard: true,
                    size: 'md'
                }
            );
        };


        $scope.itemCreateWindow = function (listId) {
            $rootScope.curListId = listId;
            $uibModal.open(
                {
                    templateUrl: 'itemCreateModal.html',
                    controller: 'modalCtrl',
                    keyboard: true,
                    size: 'md'
                }
            );
        };


        $scope.itemDeleteWindow = function (item) {
            $rootScope.curItem = item;
            $uibModal.open(
                {
                    templateUrl: 'itemDeleteModal.html',
                    controller: 'modalCtrl',
                    keyboard: false,
                    size: 'md'
                }
            );
        };

        $scope.updateList = function (list) {
            $http({
                method: 'POST',
                url: baseURL + '/create_update_list',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: $cookies.get('X-JWT-AUTH')
                },
                withCredentials: true,
                data: {id: list.id, name: list.name, userId: list.userId}
            }).then(function success(response) {
                if (response.status === 200) {
                    for (var i = 0; i < $rootScope.lists.length; i++) {
                        if ($rootScope.lists[i].id === list.id) {
                            $rootScope.lists[i].name = list.name;
                        }
                    }
                }

            });


        };


        $scope.updateItem = function (item) {
            $http({
                method: 'POST',
                url: baseURL + '/create_update_item',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: $cookies.get('X-JWT-AUTH')
                },
                withCredentials: true,
                data: {
                    id: item.id,
                    name: item.name, done: item.done, listId: item.listId
                }
            }).then(function success(response) {
                if (response.status === 200) {
                    for (var i = 0; i < $rootScope.items.length; i++) {
                        if ($rootScope.items[i].id === item.id) {
                            $rootScope.items[i].name = item.name;
                            $rootScope.items[i].done = item.done;
                        }
                    }
                }

            });


        };

        function removeByAttr(arr, attr, value) {
            var i = arr.length;
            while (i--) {
                if (arr[i]
                    && arr[i].hasOwnProperty(attr)
                    && (arguments.length > 2 && arr[i][attr] === value )) {

                    arr.splice(i, 1);

                }
            }
            return arr;
        }

        $scope.deleteAllListsForUser = function (userId) {
            $http({
                method: 'GET',
                url: baseURL + '/delete_lists_for_user',
                Authorization: $cookies.get('X-JWT-AUTH'),
                params: {user_id: userId}
            }).then(function success(response) {
                $rootScope.lists = [];
            });

        };

        $scope.clearList = function (listId) {
            $http({
                method: 'GET',
                url: baseURL + '/clear_list',
                Authorization: $cookies.get('X-JWT-AUTH'),
                params: {list_id: listId}
            }).then(function success(response) {
                removeByAttr($rootScope.items, 'listId', listId)
            });


        };

        $scope.deleteList = function (listId) {
            $http({
                method: 'GET',
                url: baseURL + '/delete_list',
                Authorization: $cookies.get('X-JWT-AUTH'),
                params: {list_id: listId}
            }).then(function success(response) {
                removeByAttr($rootScope.items, 'listId', listId);
                removeByAttr($rootScope.lists, 'id', listId);
            });

        };


    });
