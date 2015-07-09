'use strict';

angular.module('hillromvestApp')
  .factory('User', function ($http) {
    return {
      createUser: function (data) {
        return $http.post('api/hillromteamuser', data, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : 'admin@localhost:1436370910897:fd29d697fb51f9e04fb5ff8daf778ac7'
          }
        }).success(function (response) {
          return response;
        });
      }
    };
  });