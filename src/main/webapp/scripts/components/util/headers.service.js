'use strict';
angular.module('hillromvestApp')
  .factory('headerService', function(localStorageService) {
    return {
      getHeader: function() {
        var token = localStorage.getItem('token'),
          header = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'x-auth-token': token
          };
        return header;
      }
    };
  });
