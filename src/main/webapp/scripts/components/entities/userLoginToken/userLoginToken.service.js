'use strict';

angular.module('hillromvestApp')
    .factory('UserLoginToken', function ($resource, DateUtils) {
        return $resource('api/userLoginTokens/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.createdTime = DateUtils.convertDateTimeFromServer(data.createdTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
