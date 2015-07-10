'use strict';

angular.module('hillromvestApp')
  .factory('ClinicService', function ($http) {
    return {
      createClinic: function (data) {
        return $http.post('api/clinics', data, {
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : 'admin@localhost.com#1436534173652#b1bd4dead149d3acc757fc0a4e29c520'
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
            'x-auth-token' : 'admin@localhost.com#1436534173652#b1bd4dead149d3acc757fc0a4e29c520'
          }
        }).success(function (response) {
          return response;
        });
      }
    };
  });


