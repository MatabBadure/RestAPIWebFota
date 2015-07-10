'use strict';

angular.module('hillromvestApp')
  .factory('Doctor', function ($http) {
    return {
      createDoctor: function (data) {
        return $http.post('api/doctor', data, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            // will be replaced by token generated at time of login
            'x-auth-token' : 'admin@localhost.com#1436534173652#b1bd4dead149d3acc757fc0a4e29c520'

          }
        }).success(function (response) {
          return response;
        });
      },
      deleteDoctor : function(id){
       
        return $http.delete('api/doctor/'+id, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            // will be replaced by token generated at time of login
            'x-auth-token' : 'admin@localhost.com#1436534173652#b1bd4dead149d3acc757fc0a4e29c520'
          }
        }).success(function (response) {
          return response;
        });
      }
    };
  });