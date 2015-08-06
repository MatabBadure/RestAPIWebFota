'use strict';
/**
 * @ngdoc service
 * @name UserService
 * @description
 *
 */
angular.module('hillromvestApp')
  .factory('UserService', function($http, localStorageService, headerService) {
    return {

      /**
       * @ngdoc method
       * @name createUser
       * @description
       *
       */
      createUser: function(data) {
        return $http.post('api/user', data, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      /**
       * @ngdoc method
       * @name deleteUser
       * @description
       *
       */
      deleteUser: function(id) {
        return $http.delete('api/user/' + id, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      /**
       * @ngdoc method
       * @name editUser
       * @description
       *
       */
      editUser: function(data) {
        return $http.put('api/user/' + data.id, data, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      getState: function() {
        return $http.get('scripts/app/modules/admin/hill-rom-user/services/state.json')
          .success(function(response) {
            return response;
          });
      },

      getUsers: function(url, searchString, sortOption, pageNo, offset) {
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

        return $http.get(url + searchString + '&page=' + pageNo + '&per_page=' + offset + '&sort_by=' + sortOption + '&asc=' + sortOrder, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      },

      getUser: function(id, url) {
        var url = url || ('api/user/' + id);
        return $http.get(url, {
          headers: headerService.getHeader()
        }).success(function(response) {
          return response;
        });
      }
    };
  });
