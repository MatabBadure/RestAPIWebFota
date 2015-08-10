'use strict';
/**
 * @ngdoc service
 * @name UserService
 * @description
 *
 */
angular.module('hillromvestApp')
  .factory('DoctorService', function ($http, localStorageService, headerService) {
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
      },

      /**
      * @ngdoc method : getDoctors
      * @name getDoctors
      * @description : To get list of doctors available in the the clinics sent in the array.
      *
      */
      getDoctorsInClinic : function(filterArray) {
        var url = 'api/clinics/hcp';
        url = url + '?filter=';
        var flag = false;
        angular.forEach(filterArray, function(filter) {
                if (flag === true){
                  url = url + ',id:' + filter.id;
                } else{
                  url = url + 'id:' + filter.id;
                }
                flag = true;
        });
        return $http.get(url , {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      }
    };
  });
