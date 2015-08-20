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
        angular.forEach(filterArray, function(filter, index) {

                if (flag === true){
                  url = url + ',id:' + filterArray[index];
                } else{
                  url = url + 'id:' + filterArray[index];
                }
                flag = true;
        });
        return $http.get(url , {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

      getPatientsAssociatedToHCP: function(doctorId, clinicId){
        var url="/api/hcp/"+doctorId+"/patients?filterByClinic=";
        if(clinicId != null){
          url = url + ""+clinicId;
        }
        return $http.get(url , {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

      getClinicsAssociatedToHCP : function(doctorId){
        var url ='/api/hcp/'+doctorId+'/clinics';
        return $http.get(url , {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      }
    };
  });
