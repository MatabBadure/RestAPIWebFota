'use strict';
/**
 * @ngdoc service
 * @name UserService
 * @description
 *
 */
angular.module('hillromvestApp')
  .factory('DoctorService', function ($http, localStorageService) {
    var token = localStorage.getItem('token');
    return {

      

     
      /**
      * @ngdoc method
      * @name editUser
      * @description
      *
      */
      
      getDoctorsList : function(searchString, pageNo, offset){
        return $http.get('api/user/hcp/search?searchString=' + searchString + '&page=' + pageNo + '&per_page=' + offset, {
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
      * @name editUser
      * @description
      *
      */
      getDoctor : function (id) {
        return $http.get('api/user/' + id + '/hcp' ,{
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
