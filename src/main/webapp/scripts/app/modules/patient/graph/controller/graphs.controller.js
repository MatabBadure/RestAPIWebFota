'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state, patientService, UserService, $stateParams, dateService) {

  /*-----Date picker for dashboard----*/
/* $scope.date = {
  startDate: '08/12/2015', 
  endDate: '08/18/2015',
  opens: 'center',
  parentEl: '#dp3',
};*/
/*if($scope.dates && $scope.dates != 'undefined')
console.log("from and To dates :"+$scope.dates);
  /*-----Date picker for dashboard----*/

  /*---Simple pye chart JS-----*/
    $scope.percent1 = 30;
    $scope.percent2 = 75;
    $scope.percent3 = 24;
    $scope.adherence = {
        animate:{
            duration:3000,
            enabled:true
        },
        barColor:'#ffc31c',
        trackColor: '#ccc',
        scaleColor: false,
        lineWidth:12,
        lineCap:'circle'
    };
    $scope.hmr = {
        animate:{
            duration:3000,
            enabled:true
        },
        barColor:'#7e2253',
        trackColor: '#ccc',
        scaleColor: false,
        lineWidth:12,
        lineCap:'circle'
    };
  $scope.missedtherapy = {
          animate:{
              duration:3000,
              enabled:true
          },
          barColor:'#ea766b',
          trackColor: '#ccc',
          scaleColor: false,
          lineWidth:12,
          lineCap:'circle'
      };
 /*---Simple pye chart JS END-----*/
    $scope.isActive = function(tab) {
      if ($scope.patientTab.indexOf(tab) !== -1) {
        return true;
      } else {
        return false;
      }
    };

    $scope.switchPatientTab = function(status){
      $scope.patientTab = status;
      $state.go(status);
    };

    $scope.init = function() {
      $scope.getPatientById(localStorage.getItem('patientID'));
      var currentRoute = $state.current.name;
      if ($state.current.name === 'patientdashboard') {
        $scope.hmrWeeklyChart();
      }else if(currentRoute === 'patientdashboardCaregiver'){
        $scope.initPatientCaregiver();
      }else if(currentRoute === 'patientdashboardCaregiverAdd'){
        $scope.initpatientCraegiverAdd();
      }else if(currentRoute === 'patientdashboardCaregiverEdit'){
        $scope.initpatientCaregiverEdit();
      }else if(currentRoute === 'patientdashboardDeviceProtocol'){
        $scope.initPatientDeviceProtocol();
      }else if(currentRoute === 'patientdashboardClinicHCP'){
        $scope.initPatientClinicHCPs();
      }
      $scope.hmrGraphData = [
      {
          "key": "weekly",
          "values": [ [ 1025409600000 , [ {'Treatment/Day' : 21 }, {'Frequency' : 28 }, {'pressure' : 10 }, {'Caugh Pauses' : 39 }] ] ,
           [ 1028088000000 , [ {'name':'Treatment/Day','value' :21}, {'name':'Frequency', 'value': 28 }, {'name':'pressure', 'value': 10 }, {'name':'Caugh Pauses','value': 39 }]] ,
           [ 1028088000000 , [ {'name':'Treatment/Day','value' :21}, {'name':'Frequency', 'value': 28 }, {'name':'pressure', 'value': 10 }, {'name':'Caugh Pauses','value': 39 }]] ,
           [ 1028088000000 , [ {'name':'Treatment/Day','value' :21}, {'name':'Frequency', 'value': 28 }, {'name':'pressure', 'value': 10 }, {'name':'Caugh Pauses','value': 39 }]] ,
           [ 1028088000000 , [ {'name':'Treatment/Day','value' :21}, {'name':'Frequency', 'value': 28 }, {'name':'pressure', 'value': 10 }, {'name':'Caugh Pauses','value': 39 }]] ,
           [ 1028088000000 , [ {'name':'Treatment/Day','value' :21}, {'name':'Frequency', 'value': 28 }, {'name':'pressure', 'value': 10 }, {'name':'Caugh Pauses','value': 39 }]]  ]
         }
    ]    
    };

    
    $scope.toolTipContentFunction = function(){
    return function(key, x, y, e, graph) {
      var toolTipData =[];
      angular.forEach($scope.hmrGraphData[0].values, function(key, value) {
        if(key[0] === e.point[0]){
          angular.forEach(key[1], function(key, value) {
            toolTipData.push(key)
          });
        }
    });
        return  'Super New Tooltip' +
            '<h1>' + key + '</h1>' +
            '<div ng-repeat="data in toolTipData">' + '</div>'
            '<p>' +  y + ' at ' + x + '</p>'
        }
    }

    $scope.xAxisTickValuesFunction = function(){
      
    return function(d){
        var tickVals = [];
        var values = d[0].values;
        for(var i in values){
          tickVals.push(values[i][0]);
        }
        console.log('xAxisTickValuesFunction', d);
        return tickVals;
      };
    };

    $scope.$on('elementClick.directive', function(angularEvent, event){
      console.log(event);
      $scope.createGraphData();
      $scope.$digest();
    });

    // Weekly chart
    $scope.hmrWeeklyChart = function() {
      $scope.graphData = [
      {
          "key": "weekly",
          "values": [ [ 1025409600000 , 10] , [ 1028088000000 , 20] , [ 1030766400000 , 30] , [ 1033358400000 , 40] , [ 1036040400000 , 40] , [ 1038632400000 , 60] , [ 1041310800000 , 70] ]
      }
      ];
      $scope.xAxisTickValuesFunction();
      $scope.xAxisTickFormatFunction = function(){
      return function(d){
        return d3.time.format('%a')(new Date(d));
        }
      }
      $scope.xAxisTickFormatFunction();
      $scope.toolTipContentFunction();
      d3.select("g.nv-y.nv-axis").select("text.nv-axislabel").attr({y:"-3em"});
    }
    // Yearly chart
    $scope.hmrYearlyChart = function() {
      $scope.graphData = [
        {
            "key": "yearly",
            "values": [ [ 1025409600000 , 40] , [ 1028088000000 , 30] , [ 1030766400000 , 50] , [ 1033358400000 , 60] , [ 1036040400000 , 70] , [ 1038632400000 , 80] , [ 1041310800000 , 90] ]
        }
      ];
      $scope.xAxisTickValuesFunction();
      $scope.xAxisTickFormatFunction = function(){
      return function(d){
        return d3.time.format('%B')(new Date(d));
        }
      }
      $scope.xAxisTickFormatFunction();
      $scope.toolTipContentFunction();
      d3.select("g.nv-y.nv-axis").select("text.nv-axislabel").attr({y:"-3em"});

    }
    // Monthly chart
    $scope.hmrMonthlyChart = function() {
      $scope.graphData = [
        {
            "key": "Monthly",
            "values": [ [ 1025409600000 , 5] , [ 1028088000000 , 10] , [ 1030766400000 , 15] , [ 1033358400000 , 20] , [ 1036040400000 , 25] , [ 1038632400000 , 30] , [ 1041310800000 , 35] ]
        }
      ];
      $scope.xAxisTickValuesFunction();
      $scope.xAxisTickFormatFunction = function(){
      return function(d){
        return d3.time.format('%U')(new Date(d));
        }
      }
      $scope.xAxisTickFormatFunction();
      $scope.toolTipContentFunction();
      d3.select("g.nv-y.nv-axis").select("text.nv-axislabel").attr({y:"-3em"});

    }
    /*this should initiate the list of caregivers associated to the patient*/
    $scope.initPatientCaregiver = function(){
      $scope.caregivers = [];      
      $scope.getCaregiversForPatient(localStorage.getItem('patientID'));
    }

    $scope.getPatientById = function(patientId){
      patientService.getPatientInfo(patientId).then(function(response){
        $scope.slectedPatient = response.data;
      }).catch(function(response){});
    }

    $scope.getCaregiversForPatient = function(patientId){
      patientService.getCaregiversLinkedToPatient(patientId).then(function(response){
        $scope.caregivers =  response.data.caregivers;
      }).catch(function(response){});
    }

    $scope.linkCaregiver = function(){
      $state.go('patientdashboardCaregiverAdd', {'patientId': localStorage.getItem('patientID')});
    }

    $scope.initpatientCraegiverAdd = function(){
      $scope.getPatientById(localStorage.getItem('patientID'));
      $scope.careGiverStatus = "new";
      $scope.associateCareGiver = {};
      UserService.getState().then(function(response) {
        $scope.states = response.data.states;        
      }).catch(function(response) {});
      UserService.getRelationships().then(function(response) {
        $scope.relationships = response.data.relationshipLabels;
        $scope.associateCareGiver.relationship = $scope.relationships[0];
      }).catch(function(response) {});
    }

    $scope.formSubmitCaregiver = function(){
      $scope.submitted = true;
      if($scope.form.$invalid){
        return false;
      }
      var data = $scope.associateCareGiver;
      data.role = 'CARE_GIVER';
      if($scope.careGiverStatus === "new"){
        $scope.associateCaregiverstoPatient(localStorage.getItem('patientID'), data);
      }else if($scope.careGiverStatus === "edit"){
        $scope.updateCaregiver(localStorage.getItem('patientID'), $stateParams.caregiverId , data);
      }
    }

    $scope.associateCaregiverstoPatient = function(patientId, careGiver){
        patientService.associateCaregiversFromPatient(patientId, careGiver).then(function(response){
        $scope.caregivers =  response.data.user;
        $scope.associateCareGiver = [];$scope.associateCareGiver.length = 0;
        $scope.switchPatientTab('patientdashboardCaregiver');
      }).catch(function(response){
        notyService.showMessage(response.data.ERROR,'warning' );
      });
    }

    $scope.goToCaregiverEdit = function(careGiverId){
      $state.go('patientdashboardCaregiverEdit', {'caregiverId': careGiverId});
    }

    $scope.disassociateCaregiver = function(caregiverId, index){
        patientService.disassociateCaregiversFromPatient(localStorage.getItem('patientID'), caregiverId).then(function(response){
        $scope.caregivers.splice(index, 1);
      }).catch(function(response){});
    }

    $scope.initpatientCaregiverEdit = function(caregiverId){
      $scope.careGiverStatus = "edit";
      $scope.getPatientById(localStorage.getItem('patientID'));
      $scope.editCaregiver(caregiverId);
    }

    $scope.editCaregiver = function(careGiverId){
        UserService.getState().then(function(response) {
          $scope.states = response.data.states;
        }).catch(function(response) {});
        UserService.getRelationships().then(function(response) {
          $scope.relationships = response.data.relationshipLabels;
        }).catch(function(response) {});
        var caregiverId = $stateParams.caregiverId;
        patientService.getCaregiverById(localStorage.getItem('patientID'), caregiverId).then(function(response){
          $scope.associateCareGiver = response.data.caregiver.user;
          $scope.associateCareGiver.relationship = response.data.caregiver.relationshipLabel;
        }).catch(function(response){});
    }

    $scope.updateCaregiver = function(patientId, caregiverId , careGiver){
      var tempCaregiver = {};
      tempCaregiver.title = careGiver.title;
      tempCaregiver.firstName = careGiver.firstName;
      tempCaregiver.middleName = careGiver.middleName;
      tempCaregiver.lastName = careGiver.lastName;
      tempCaregiver.email = careGiver.email;
      tempCaregiver.address = careGiver.address;
      tempCaregiver.zipcode = careGiver.zipcode;
      tempCaregiver.city = careGiver.city;
      tempCaregiver.state = careGiver.state;
      tempCaregiver.relationship = careGiver.relationship;
      tempCaregiver.primaryPhone = careGiver.primaryPhone;
      tempCaregiver.mobilePhone = careGiver.mobilePhone;
      tempCaregiver.role = careGiver.role;

      patientService.updateCaregiver(patientId,caregiverId, tempCaregiver).then(function(response){
        $scope.associateCareGiver = [];$scope.associateCareGiver.length = 0;
        $scope.switchPatientTab('patientdashboardCaregiver');
      }).catch(function(response){});
    }
    $scope.initPatientDeviceProtocol = function(){     
      patientService.getDevices(localStorage.getItem('patientID')).then(function(response){
        angular.forEach(response.data.deviceList, function(device){
          var _date = dateService.getDate(device.createdDate);
          var _month = dateService.getMonth(_date.getMonth());
          var _day = dateService.getDay(_date.getDate());
          var _year = dateService.getYear(_date.getFullYear());
          var date = _month + "/" + _day + "/" + _year;
          device.createdDate = date;
          device.days = dateService.getDays(_date);
        });
        $scope.devices = response.data.deviceList;
      }).catch(function(response){});
      $scope.getProtocols(localStorage.getItem('patientID'));    
    }
    $scope.getProtocols = function(patientId){
      patientService.getProtocol(patientId).then(function(response){
        $scope.protocols = response.data.protocol;
        $scope.addProtocol = true;
        angular.forEach($scope.protocols, function(protocol){
          if(!protocol.deleted){
            $scope.addProtocol = false;
          }
        });
      }).catch(function(){});
    };

    $scope.initPatientClinicHCPs = function(){
      $scope.getClinicsOfPatient();
      $scope.getHCPsOfPatient();
    }
    $scope.getClinicsOfPatient = function(){
      patientService.getClinicsLinkedToPatient(localStorage.getItem('patientID')).then(function(response){
        $scope.clinics = response.data.clinics;                
      }).catch(function(){});
    }
    
    $scope.getHCPsOfPatient = function(){
      patientService.getHCPsLinkedToPatient(localStorage.getItem('patientID')).then(function(response){
        $scope.hcps = response.data.hcpUsers;                
      }).catch(function(){});
    }

    $scope.init();

});
