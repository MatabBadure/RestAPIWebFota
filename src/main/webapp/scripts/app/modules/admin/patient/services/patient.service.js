'use strict';
/**
 * @ngdoc service
 * @name patientService
 * @description A service that calls REST apis to perform operations related to patients.
 *
 */
angular.module('hillromvestApp')
  .factory('patientService', function ($http, localStorageService, headerService) {
    return {

      /**
      * @ngdoc method
      * @name getPatients
      * @description To get list of patients.
      *
      */
      getPatients: function(searchString, sortOption, pageNo, offset) {
        var url = admin.patient.searchURL;
        var sortOrder;
        if (searchString === undefined) {
          searchString = '';
        }
        if (sortOption === "") {
          sortOption = "createdAt";
          sortOrder = false;
        } else {
          sortOrder = true;
        };
        url = url + '?searchString=' + searchString + '&page=' + pageNo + '&per_page=' + offset + '&sort_by=' + sortOption + '&asc=' + sortOrder;
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      /**
      * @ngdoc method
      * @name getPatientInfo
      * @description To get individual patient's information based on patient ID.
      *
      */
      getPatientInfo : function(id){
        var url = admin.hillRomUser.baseURL + '/' + id + '/patient';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

      /**
      * @ngdoc method
      * @name associateHCPToPatient
      * @description To associate HCP to patient.
      *
      */
      associateHCPToPatient : function(data,id){
        var url = admin.patient.baseURL + '/' + id + '/associatehcp';
        return $http.put(url, data, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

      /**
      * @ngdoc method
      * @name getDoctorsLinkedToPatient
      * @description To get list of HCPs linked to patient.
      *
      */
      getHCPsLinkedToPatient : function(id){
        var url = admin.patient.baseURL + '/' + id + '/hcp';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },

      /**
      * @ngdoc method
      * @name disassociateDoctorFromPatient
      * @description To disassciate a HCP from patient.
      *
      */
      disassociateHCPFromPatient : function(id){
        var url = admin.patient.baseURL + id + '/hcp';
        return $http.delete(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name getClinicsLinkedToPatient
      * @description To get list of clinics associated to patient.
      *
      */
      getClinicsLinkedToPatient : function(id){
        var url = admin.patient.baseURL + id + '/clinics';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name disassociateClinicsFromPatient
      * @description To remove clinic(s) associated to patient.
      *
      */
      disassociateClinicsFromPatient : function(id, data){
        var url = admin.patient.baseURL + id + '/dissociateclinics';
        return $http.put(url, data,{
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name disassociatePatient
      * @description To remove patient.
      *
      */
      disassociatePatient : function(id){
        var url = admin.hillRomUser.baseURL + '/' + id;
        return $http.delete(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name associateClinicToPatient
      * @description To add clinics to patient.
      *
      */
      associateClinicToPatient : function(id, data){
        var url = admin.patient.baseURL + id + '/associateclinics';
        return $http.put(url, data, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name getDevices
      * @description To get devices associated to patient.
      *
      */
      getDevices : function(id){
        var url = admin.patient.baseURL + id + '/vestdevice';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name getCaregiversLinkedToPatient
      * @description To get caregivers associated to patient.
      *
      */
      getCaregiversLinkedToPatient : function(id){
        var url = admin.patient.baseURL + id + '/caregiver';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name disassociateCaregiversFromPatient
      * @description To remove caregivers associated to patient.
      *
      */
      disassociateCaregiversFromPatient : function(patientId, caregiverId ){
        ///api/patient/:patientUserId/caregiver/:id
        var url = admin.patient.baseURL + patientId + '/caregiver/' + caregiverId;
        return $http.delete(url, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      },
      /**
      * @ngdoc method
      * @name disassociateCaregiversFromPatient
      * @description To add caregivers associated to patient.
      *
      */
      associateCaregiversFromPatient : function(patientId, data){
        ///api/patient/:id/caregiver
        var url = admin.patient.baseURL + patientId + '/caregiver';
        return $http.post(url, data, {
          headers: headerService.getHeader()
        }).success(function (response) {
          return response;
        });
      }
    };
  });