'use strict';
angular.module('hillromvestApp')
  .factory('notyService', function(localStorageService, noty) {
    return {
      showMessage: function(message, type) {
        noty.showNoty({
          text: message,
          ttl: 5000,
          type: type
        });
      }
    };
  });