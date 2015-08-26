'use strict';

angular.module('hillromvestApp')
.directive('activeMenu', function($translate, $locale, tmhDynamicLocale) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var language = attrs.activeMenu;

      scope.$watch(function() {
        return $translate.use();
      }, function(selectedLanguage) {
        if (language === selectedLanguage) {
          tmhDynamicLocale.set(language);
          element.addClass('active');
        } else {
          element.removeClass('active');
        }
      });
    }
  };
})

.directive('activeLink', function(location) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var clazz = attrs.activeLink;
      var path = attrs.href;
      path = path.substring(1); //hack because path does bot return including hashbang
      scope.location = location;
      scope.$watch('location.path()', function(newPath) {
        if (path === newPath) {
          element.addClass(clazz);
        } else {
          element.removeClass(clazz);
        }
      });
    }
  };
});


angular.module('hillromvestApp')
.directive('navigationBar', function (Auth, Principal, $state, Account, $location) {
  return {
    templateUrl: 'scripts/components/navbar/navbar.html',
    restrict: 'E',

    controller: function ($scope) {
      $scope.isActive = function(tab) {
        var path = $location.path();
        if (path.indexOf(tab) !== -1) {
          return true;
        } else {
          return false;
        }
      };

      $scope.signOut = function(){
        Account.get().$promise
        .then(function (account) {
          $scope.isAuthenticated = true;

        })
        .catch(function() {
          $scope.isAuthenticated = false;
          $state.go('login');
        });
      }

      $scope.logout = function(){
        Auth.signOut().then(function(data) {
          Auth.logout();
          localStorage.clear();
          $scope.signOut();
        }).catch(function(err) {
        });
      }

      $scope.account = function(){
        var userRole = localStorage.getItem('role');        
        if(userRole === "PATIENT"){
          $state.go("patientProfile");
        }
      }

      $scope.goToHomePage = function(){
        var userRole = localStorage.getItem('role'); 
        if(userRole === "ADMIN"){
          $state.go("patientUser");
        }else if(userRole === "PATIENT"){
          $state.go("patientdashboard");
        }
      }
    }
  };
});

angular.module('hillromvestApp')
.directive('navbarPopover', function(Auth, $state, Account, $compile) {
    return {
        restrict: 'A',
        template: "<span id='pop-over-link' class='padding-right cursor-pointer'>{{username}}</span><span id='icon-arrow' class='hillrom-icon icon-arrow-down cursor-pointer'></span>" +
                  "<span style='display:none' id='pop-over-content'><div ng-click='account()'><span class='hillrom-icon icon-user-account'></span><span>Account</span></div><div ng-click='logout()'><span class='hillrom-icon icon-logout'></span><span>Logout </span></div></span>",
        link: function(scope, elements, attrs) {
            $("#pop-over-link, #icon-arrow").popover({
                'placement': 'bottom',
                'trigger': 'click',
                'html': true,
                'content': function() {
                    return $compile($("#pop-over-content").html())(scope);
                }
            });
        },
        controller: function ($scope) {
          $scope.username = localStorage.getItem('userFirstName');
        }
    }
});

angular.module('hillromvestApp')
.directive('navigationBarPatient', function (Auth, Principal, $state, Account, $location) {
  return {
    templateUrl: 'scripts/components/navbar/navbarpatientuser.html',
    restrict: 'E',

    controller: function ($scope) {
      $scope.isActive = function(tab) {
        var path = $location.path();
        if (path.indexOf(tab) !== -1) {
          return true;
        } else {
          return false;
        }
      };
    }
  };
});