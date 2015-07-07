'use strict';

angular.module('hillromvestApp')
    .factory('User', function ($rootScope, $state, $q, $translate) {
        return {
          createUser: function (data) {
            console.log('Create USer Service...!');
            var data = {
                  'username' : credentials.username,
                  'password' : credentials.password
                };
                return $http.post('api/hillromteamuser', data, {
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json",
                        "x-auth-token":"admin@localhost:1436268128948:ea72602c7b5be210de683f586d4b8d83"
                    }
                }).success(function (response) {
                    console.log('In success Block...!');
                    return response;
                });
          }
        };
      });