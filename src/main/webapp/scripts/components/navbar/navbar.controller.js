'use strict';

angular.module('hillromvestApp')
    .controller('NavbarController', function ($scope, $location, $state, Auth, Principal) {
        $scope.$state = $state;
    });
