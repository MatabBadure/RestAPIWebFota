'use strict';

angular.module('hillromvestApp')
    .factory('User', function ($rootScope, $state, $q, $translate, $http) {
        return {
          createUser: function (data) {
            console.log('Create USer Service...!');
            return $http.post('api/hillromteamuser', data, {
              headers: {
                  "Content-Type": "application/json",
                  "Accept": "application/json",
                  "x-auth-token":"admin@localhost:1436351641968:d582f2b284afac34b45669489f314c3d"
              }
            }).success(function (response) {
              console.log('In success Block...!');
              return response;
            });
          }
        };
      });