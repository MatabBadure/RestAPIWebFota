'use strict';

/**
 * @ngdoc service
 * @name Doctor
 *
 * @description
 * Doctor service for Create, Edit and Delete functions
 */
angular.module('hillromvestApp')
.factory('Doctor', function ($http,localStorageService) {
  var token = localStorage.getItem('token');
  return {

    /**
    * @ngdoc method
    * @name createDoctor
    *
    * @description
    * Function to create doctor
    *
    * @param {Object} data - doctor Object
    * @returns {Object} Returns the promise
    */
    createDoctor: function (data) {
      return $http.post('api/user', data, {
        headers: {
          'Content-Type' : 'application/json',
          'Accept' : 'application/json',
          'x-auth-token' : token
        }
      }).success(function (response) {
        return response;
      });
    },

    /**
    * @ngdoc method
    * @name deleteDoctor
    *
    * @description
    * Function to delete doctor
    *
    * @param {int} id - Doctor's id
    * @returns {Object} Returns the promise
    */
    deleteDoctor : function(id){
      return $http.delete('api/user/'+id, {
        headers: {
          'Content-Type' : 'application/json',
          'Accept' : 'application/json',
          'x-auth-token' : token
        }
      }).success(function (response) {
        return response;
      });
    },

    /**
    * @ngdoc method
    * @name editDoctor
    *
    * @description
    * Function to Update doctor
    *
    * @param {Object} data - Doctor Object
    * @returns {Object} Returns the promise
    */
    editDoctor : function (data) {
      return $http.put('/api/user/'+data.id, data, {
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