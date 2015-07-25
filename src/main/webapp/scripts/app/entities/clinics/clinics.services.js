'use strict';
angular.module('hillromvestApp')
  .factory('ClinicService', function ($http,localStorageService) {
	var token = localStorage.getItem('token');
    return {
      createClinic: function (data) {
        return $http.post('api/clinics', data, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : token
          }
        }).success(function (response) {
          return response;
        });
      },

      updateClinic: function (data) {
        return $http.put('api/clinics/' + data.id, data, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : token
          }
        }).success(function (response) {
          return response;
        });
      },

      deleteClinic : function(id){

        return $http.delete('api/clinics/'+id, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : token
          }
        }).success(function (response) {
          return response;
        });
      },

      getClinics : function (searchString, pageNo, offset) {
        return $http.get('api/clinics/search?searchString=' + searchString + '&pageNo=' + pageNo + '&ofset=' + offset)
         .success(function (response) {
          return response;
        });
      }
    };
  });


