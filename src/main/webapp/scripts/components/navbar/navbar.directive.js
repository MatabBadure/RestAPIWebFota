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
.directive('navigationBar', function (Auth, Principal, $state, Account) {
  return {
    templateUrl: 'scripts/components/navbar/navbar.html',
    restrict: 'E',

    controller: function ($scope) {

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
      $scope.signOut();
      $scope.logout = function(){
        Auth.signOut().then(function(data) {
          Auth.logout();
          $scope.signOut();
        }).catch(function(err) {  
        });
      }
    }
  };
});

angular.module('hillromvestApp')
.directive('customPopover', function () {
  return {
    restrict: 'A',
    template: '<span class="padding-right">Jose Rubert</span><span class="hillrom-icon icon-arrow-down"></span>',
    link: function (scope, el, attrs) {
      $(el).popover({
          trigger: 'click',
          html: true,
          content: attrs.popoverHtml,
          placement: attrs.popoverPlacement
      });
    }
  };
});