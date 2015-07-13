'use strict';

describe('Controllers Tests ', function() {

  beforeEach(module('hillromvestApp'));

  describe('LoginController', function() {
    var scope,
      timeout,
      state,
      date,
      httpBackend;

    beforeEach(inject(function($rootScope, $controller, $state, $timeout, Auth, $httpBackend) {
      scope = $rootScope.$new();
      state = $state;
      timeout = $timeout;
      httpBackend = $httpBackend;
      date = new Date('2015-01-01T00:00:00Z');
      spyOn(window, 'Date').and.callFake(function() {
        return date;
      });
      httpBackend.whenGET(/\.*.html/).respond({});
      httpBackend.whenGET(/\.*.json/).respond({});
      httpBackend.whenGET(accountDetailsURL).respond(200, responses.authenticate.success);
      $controller('LoginController', {
        $scope: scope,
        $state: state,
        Auth: Auth,
        $timeout: timeout
      });

    }));

    it('should login successfully', function() {
      httpBackend.whenPOST(loginURL).respond(200, responses.authenticate.success);
      scope.username = 'temp';
      scope.password = 'temp';
      scope.user.captcha = '';
      scope.authenticate();
      httpBackend.flush();

      // auth token should set to false
      expect(scope.authenticationError).toBe(false);

      // current url should be patient after successful login
      expect(state.current.url).toBe('/patient');
      expect(state.current.parent).toBe('entity');

      //
      expect(localStorage.getItem('token')).toBe('undefined');
    });


    it('Should Fail the Login', function() {
      httpBackend.whenPOST(loginURL).respond(401, responses.authenticate.success);
      scope.username = 'temp';
      scope.password = 'temp';
      scope.user.captcha = '';
      scope.authenticate();
      httpBackend.flush();

      // auth token should set to false
      expect(scope.authenticationError).toBe(true);

      //
      expect(localStorage.getItem('token')).toBe(null);
    });
  });
});
