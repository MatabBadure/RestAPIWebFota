'use strict';

describe('Controller: ResetFinishController ', function() {

  var scope, timeout, state, date, httpBackend;

  beforeEach(module('hillromvestApp'));

  var accountDetailsURL;
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


    $controller('ResetFinishController', {
      $scope: scope,
      $state: state,
      Auth: Auth,
      $timeout: timeout
    });


  }));

  it('should show error if passwords do not match', function() {
    //GIVEN
    var event = {preventDefault: jasmine.createSpy()}

    // spyOn(Event, 'preventDefault').and.callThrough(function(){});
    scope.resetAccount.password = 'password1';
    scope.resetAccount.confirmPassword = 'password2';
    //WHEN
    scope.form = {
      $invalid: false
    }
    scope.finishReset(event);
    //THEN
    expect(scope.doNotMatch).toBe('ERROR');
  });

});
