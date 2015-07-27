'use strict';

angular.module('hillromvestApp')
    .controller('LogoutController', function ($scope, Auth, $state, Principal) {
    	Auth.signOut().then(function(data) {
	      Auth.logout();
	      $state.go('login');
	    }).catch(function(err) {
	      console.log("logout failure");
	    });
    });
