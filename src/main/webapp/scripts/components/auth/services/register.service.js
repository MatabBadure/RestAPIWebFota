'use strict';

angular.module('hillromvestApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


