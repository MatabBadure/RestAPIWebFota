'use strict';

angular.module('hillromvestApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
