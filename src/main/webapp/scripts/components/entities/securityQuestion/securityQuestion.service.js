'use strict';

angular.module('hillromvestApp')
    .factory('SecurityQuestion', function ($resource, DateUtils) {
        return $resource('api/securityQuestions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
