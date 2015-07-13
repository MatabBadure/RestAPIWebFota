'use strict';

angular.module('hillromvestApp')
.factory('Doctor', function ($http,localStorageService) {
  var token = localStorage.getItem('token');
  return {
    createDoctor: function (data) {
      return $http.post('api/doctor', data, {
        headers: {
          'Content-Type' : 'application/json',
          'Accept' : 'application/json',
          'x-auth-token' : token
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
          'x-auth-token' : token
        }
      }).success(function (response) {
        return response;
      });
    }
  };
});