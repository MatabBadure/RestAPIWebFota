'use strict';

angular.module('hillromvestApp')
  .factory('User', function ($http) {
    return {
      createUser: function (data) {
        return $http.post('api/hillromteamuser', data, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : 'admin@localhost:1436421984245:f1bf30edec0aabf93ab6d883ef01ad69'
          }
        }).success(function (response) {
          return response;
        });
      },
      deleteUser : function(id){
        id=16;
        return $http.delete('api/hillromteamuser/'+id, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : 'admin@localhost:1436421984245:f1bf30edec0aabf93ab6d883ef01ad69'
          }
        }).success(function (response) {
          return response;
        });
      }
    };
  });