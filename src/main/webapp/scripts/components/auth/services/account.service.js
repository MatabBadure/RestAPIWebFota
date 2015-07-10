'use strict';

angular.module('hillromvestApp')
    .factory('Account', function Account($resource) {
        return $resource('api/account', {}, {
            'get': { method: 'GET', headers:{
            	'x-auth-token':localStorage.getItem('token')
            },params: {}, isArray: false,
                interceptor: {
                    response: function(response) {
                        // expose response
                        return response;
                    }
                }
            }
        });
    });
