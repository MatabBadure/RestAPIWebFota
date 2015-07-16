'use strict';

angular.module('hillromvestApp')
  .factory('Patient', function ($http,localStorageService) {
	var token = localStorage.getItem('token');
    return {
      createPatient: function (data) {
        /*return $http.post('api/patients', data, {*/
          return $http.get('scripts/components/entities/patients/new/patient.json' /*{
          headers: {
            'Content-Type' : 'application/json',
            'Accept' : 'application/json',
            'x-auth-token' : token
          }
        }*/).success(function (response) {
          return response;
        });
      },
      deletePatient : function(id){
       
        return $http.delete('api/patients/'+id, {
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


