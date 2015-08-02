'use strict';
angular.module('hillromvestApp')
  .factory('ClinicService', function($http, localStorageService, headerService) {
    var token = localStorage.getItem('token');
    return {
      createClinic: function(data) {
        return $http.post('api/clinics', data, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      updateClinic: function(data) {
        return $http.put('api/clinics/' + data.id, data, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      deleteClinic: function(id) {
        return $http.delete('api/clinics/' + id, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      getClinics: function(searchString, sortOption, pageNo, offset) {
        if (searchString === undefined) {
          searchString = '';
        }
        var sortOrder;
        if (sortOption === "") {
          sortOption = "createdAt";
          sortOrder = false;
        } else {
          sortOrder = true;
        };
        return $http.get('api/clinics/search?searchString=' + searchString + '&page=' + pageNo + '&per_page=' + offset + '&sort_by=' + sortOption + '&asc=' + sortOrder, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      getAllClinics: function(url) {
        var url = url || '/api/clinics';
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      getClinic: function(id) {
        return $http.get('/api/clinics/' + id, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      }
    };
  });
