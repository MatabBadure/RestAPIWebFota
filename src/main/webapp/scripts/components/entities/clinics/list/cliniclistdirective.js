'use strict';

angular.module('hillromvestApp')
.directive('clinicList', function(ClinicService) {
  return {
    templateUrl: 'scripts/components/entities/clinics/list/list.html',
    restrict: 'E',
    scope: {
      onSelect: '&',
      onCreate: '&'
    },
    link: function(scope, element, attrs) {
      var clinic = scope.clinic;
    },
    controller: function($scope) {
      $scope.clinics = [];

      $scope.selectClinic = function(clinic) {
        $scope.clinic = clinic;
        $scope.onSelect({'clinic': clinic});
      },

      $scope.createClinic = function(){
          $scope.onCreate();
        },

      $scope.searchClinics = function(searchItem) {
        console.log('$scope.searchItem', $scope.searchItem);
        console.log('searchItem : ', searchItem);
        ClinicService.getClinics($scope.searchItem, 1, 10).then(function (response) {
          console.log('SUCCESS Response ', response);
          $scope.clinics = response.clinics;
        }).catch(function (response){
          console.log('ERROR Response ', response);
        });

        //Mocking Data
        $scope.clinics = [
          {
            'id': 1,
            'name': "Manipal Hospitals-main",
            'address': "Old Airport Road",
            'zipcode': 560009,
            'city': "Bangalore",
            'state': "Karnataka",
            'phoneNumber': null,
            'faxNumber': null,
            'hillromId': null,
            'parentClinic': null,
            'deleted': false,
            'parent': true
          },
          {
            'id': 1,
            'name': "Manipal Hospitals-main",
            'address': "Old Airport Road",
            'zipcode': 560009,
            'city': "Bangalore",
            'state': "Karnataka",
            'phoneNumber': null,
            'faxNumber': null,
            'hillromId': null,
            'parentClinic': null,
            'deleted': false,
            'parent': true
          }, {
            'id': 1,
            'name': "Manipal Hospitals-main",
            'address': "Old Airport Road",
            'zipcode': 560009,
            'city': "Bangalore",
            'state': "Karnataka",
            'phoneNumber': null,
            'faxNumber': null,
            'hillromId': null,
            'parentClinic': null,
            'deleted': false,
            'parent': true
          }, {
            'clinicName':'Apollo Hospital.',
            'address': 'New York',
            'zip': '213321',
            'city':'New York',
            'state':'New York',
            'fax':'3423434434',
            'admin': 'Manipal'
          }
        ];
      }
    }
  };
});
