'use strict';
/**
 * @ngdoc service
 * @name UserService
 * @description
 *
 */
angular.module('hillromvestApp')
  .factory('patientService', function ($http, localStorageService, headerService) {
    return {

      /**
      * @ngdoc method
      * @name editUser
      * @description
      *
      */
      getPatientList : function(keyword,pageIndex,countPerPage){
        return $http.get('api/patientInfos/search?searchString=' +
         keyword + '&page=' + pageIndex + '&per_page=' + countPerPage,  {
          headers: headerService.getHeader()
        }).success(function (data, status, headers, config) {
           return {'response' : data, 'status' : status, 'headers' : headers, 'config' : config};
        });
      },

      /**
      * @ngdoc method
      * @name editUser
      * @description
      *
      */
      getPatientInfo : function(id){
        return $http.get('api/user/' + id + '/patient',  {
          headers: headerService.getHeader()
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
      editPatient : function (id) {
        return $http.put('api/patientInfos/' + id,  {
          headers: headerService.getHeader()
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
      associateHCPToPatient : function(data,id){
        var url = 'api/patient/' + id + '/associatehcp';
        return $http.put(url, data, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },


      getDoctorsLinkedToPatient : function(id){
        var url = 'api/patient/' + id + '/hcp';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

      disassociateDoctorFromPatient : function(id){
        var url = 'api/patient/' + id + '/hcp';
        return $http.delete(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

    };
  });