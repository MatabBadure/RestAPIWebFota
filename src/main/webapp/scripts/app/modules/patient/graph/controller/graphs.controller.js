'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state, patientDashBoardService, StorageService, dateService, graphUtil, patientService, UserService, $stateParams, dateService, notyService) {
    var chart;
    $scope.init = function() {
      $scope.hmrLineGraph = true;
      $scope.hmrBarGraph = false;
      $scope.hmrGraph = true;
      $scope.format = 'weekly';
      $scope.patientId = 160;
      $scope.compliance = {};
      $scope.compliance.pressure = true;
      $scope.compliance.duration = true;
      $scope.compliance.frequency = false;
      $scope.toTimeStamp = new Date().getTime();
      $scope.compliance.secondaryYaxis = 'frequency';
      $scope.hmrRunRate = 0;
      $scope.adherenceScore = 0;
      $scope.missedtherapyDays = 0;
      $scope.minFrequency = 0;
      $scope.maxFrequency = 0;
      $scope.minPressure = 0;
      $scope.maxPressure = 0;
      $scope.minDuration = 0;
      $scope.maxDuration = 0;
      $scope.getHmrRunRateAndScore();
      //$scope.patientId = StorageService.get('patientID');
      $scope.fromTimeStamp = dateService.getnDaysBackTimeStamp(7);
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);
      if ($state.current.name === 'patientdashboard') {
        $scope.weeklyChart();
      }$scope.getPatientById(localStorage.getItem('patientID'));
      var currentRoute = $state.current.name;
      if ($state.current.name === 'patientdashboard') {
        $scope.initPatientDashboard();        
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
    };
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

    $scope.calculateDateFromPicker = function(picker) {
      $scope.fromTimeStamp = new Date(picker.startDate._d).getTime();
      $scope.toTimeStamp = new Date(picker.endDate._d).getTime();
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);
    };

    $scope.removeGraph = function() {
      d3.select('svg').selectAll("*").remove();
    }
    $scope.drawGraph = function() {
      var days = dateService.getDateDiffIndays($scope.fromTimeStamp,$scope.toTimeStamp);
      if(days === 0){
        $scope.format = 'dayWise';
        $scope.hmrLineGraph = false;
        $scope.hmrBarGraph = true;
        $scope.getDayHMRGraphData();
      } else if(days <= 7) {
        $scope.weeklyChart($scope.fromTimeStamp);
      } else if ( days > 7 && days < 36 ) {
        $scope.monthlyChart($scope.fromTimeStamp);
      } else if ( days >= 36) {
         $scope.yearlyChart($scope.fromTimeStamp);
      }
    };

    $scope.opts = {
      eventHandlers: {'hide.daterangepicker': function(ev, picker) {
        $scope.calculateDateFromPicker(picker);
        $scope.drawGraph();
        }
      }
    }

  $scope.dates = {startDate: null, endDate: null};
    
    $scope.getHmrRunRateAndScore = function() {
      patientDashBoardService.getHMRrunAndScoreRate($scope.patientId, $scope.toTimeStamp).then(function(response){
        //Will get response data from real time API once api is ready
        if(response.status === 200 ){
          $scope.hmrRunRate = response.data.hmrRunRate;
          $scope.adherenceScore = response.data.score;
        }
      }).catch(function(response) {
      });
    }

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

    /*$scope.init = function() {
      $scope.getPatientById(localStorage.getItem('patientID'));
      var currentRoute = $state.current.name;
      if ($state.current.name === 'patientdashboard') {
        $scope.initPatientDashboard();        
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
    };*/


    $scope.xAxisTickFormatFunction = function(format){
      return function(d){
        switch(format) {
          case "weekly":
              return d3.time.format('%A')(new Date(d));
              break;
          case "dayWise":
              return dateService.getTimeIntervalFromTimeStamp(d);
              break;
          case "monthly":
              return 'week ' + dateService.getWeekOfMonth(d);
              break;
          case "yearly":
              return d3.time.format('%B')(new Date(d));
              break;
          default:
              break;
        }
    }
  };

    $scope.toolTipContentFunction = function(){
      return function(key, x, y, e, graph) {
        var toolTip = '';
        angular.forEach($scope.completeGraphData.actual, function(value) {
          if(value.timestamp === e.point[0]){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.timestamp) + '</h6>' +
                '<p> Treatment/Day ' + value.treatmentsPerDay + '</p>' +
                '<p> Frequency ' + value.weightedAvgFrequency + '</p>' +
                '<p> Pressure ' + value.weightedAvgPressure + '</p>' +
                '<p> Cough Pauses ' + value.normalCoughPauses + '</p>';
          }
        });
      return toolTip;   
      }
    }

    $scope.toolTipContentBarChart = function(){
      return function(key, x, y, e, graph) {
        var toolTip = '';
        angular.forEach($scope.completeGraphData, function(value) {
          if(value.startTime === e.point[0]){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.startTime) + '</h6>' +
                '<p> Frequency ' + value.frequency + '</p>' +
                '<p> Pressure ' + value.pressure + '</p>' +
                '<p> Cough Pauses ' + (value.normalCaughPauses + value.programmedCaughPauses) + '</p>';
          }
        });
      return toolTip;   
      }
    }

    $scope.toolTipContentForCompliance = function(data){
      return function(key, x, y, e, graph) {
        var toolTip = '';
        angular.forEach(data, function(value) {
          if(value.start === e.point.x){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.start) + '</h6>' +
                '<p> Treatment/Day ' + value.treatmentsPerDay + '</p>' +
                '<p> Frequency ' + value.weightedAvgFrequency + '</p>' +
                '<p> Pressure ' + value.weightedAvgPressure + '</p>' +
                '<p> Caugh Pauses ' + value.normalCoughPauses + '</p>';
          }
        });
      return toolTip;   
      }
    }

    $scope.xAxisTickValuesFunction = function(){
      
    return function(d){
        var tickVals = [];
        var values = d[0].values;
        for(var i in values){
          tickVals.push(values[i][0]);
        }
        return tickVals;
      };
    };

    $scope.xAxisTickValuesBarChart = function() {
      return function(d){
        var tickVals = [];
        var values = d[0].values;
        for(var i in values){
          tickVals.push(values[i][0]);
        }
        return tickVals;
      };
    };

   /* $scope.$on('elementClick.directive', function(angularEvent, event) {
      console.log(event);
      $scope.createGraphData();
      $scope.$digest();
    });*/
    $scope.showHmrGraph = function() {
      $scope.complianceGraph = false;
      $scope.hmrGraph = true;
    };

    $scope.getNonDayHMRGraphData = function() {
      patientDashBoardService.getHMRGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        //Will get response data from real time API once api is ready
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData.actual === undefined){
          $scope.graphData = [];
        } else {
          $scope.graphData = graphUtil.convertIntoHMRLineGraph($scope.completeGraphData);
        }
      }).catch(function(response) {
        $scope.graphData = [];
      });
    };

    $scope.getDayHMRGraphData = function() {
      patientDashBoardService.getHMRBarGraphPoints($scope.patientId, $scope.fromTimeStamp).then(function(response){
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData.actual === undefined){
           $scope.graphData = [];
         } else {
          $scope.completeGraphData = graphUtil.formatDayWiseDate($scope.completeGraphData.actual);
           $scope.graphData = graphUtil.convertIntoHMRBarGraph($scope.completeGraphData);
         }
      }).catch(function(response) {
        $scope.graphData = [];
      });
    };

    $scope.getComplianceGraphData = function() {
      patientDashBoardService.getHMRGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        //Will get response data from real time API once api is ready
        $scope.completeComplianceData = response.data;
        if($scope.completeComplianceData.actual === undefined){
          $scope.complianceGraphData = [];
        } else {
          //recommended values
          $scope.minFrequency = $scope.completeComplianceData.recommended.minFrequency;
          $scope.maxFrequency = $scope.completeComplianceData.recommended.maxFrequency;
          $scope.minPressure = $scope.completeComplianceData.recommended.minPressure;
          $scope.maxPressure = $scope.completeComplianceData.recommended.maxPressure;
          $scope.minDuration = $scope.completeComplianceData.recommended.minMinutesPerTreatment * $scope.completeComplianceData.recommended.treatmentsPerDay;
          $scope.maxDuration = $scope.completeComplianceData.recommended.maxMinutesPerTreatment * $scope.completeComplianceData.recommended.treatmentsPerDay;
          $scope.completecomplianceGraphData = graphUtil.convertIntoComplianceGraph($scope.completeComplianceData.actual);
          $scope.createComplianceGraphData();
          $scope.drawComplianceGraph();
        }
      }).catch(function(response) {
        $scope.complianceGraphData = [];
      });
    };

    $scope.calculateTimeDuration = function(durationInDays) {
      $scope.toTimeStamp = new Date().getTime();
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);
      $scope.fromTimeStamp = dateService.getnDaysBackTimeStamp(durationInDays);;
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
    }

    // Weekly chart
    $scope.weeklyChart = function(datePicker) {
      $scope.removeGraph();
      if(datePicker === undefined){
        $scope.calculateTimeDuration(7);
      }
      $scope.format = $scope.groupBy = 'weekly';
      if($scope.hmrGraph) {
        $scope.hmrLineGraph = true;
        $scope.hmrBarGraph = false;
        $scope.getNonDayHMRGraphData();
      } else if ($scope.complianceGraph) {
        $scope.getComplianceGraphData();
      }
    }

    // Yearly chart
    $scope.yearlyChart = function(datePicker) {
      $scope.removeGraph();
       if(datePicker === undefined){
        $scope.calculateTimeDuration(365);
      }
       $scope.format = $scope.groupBy = 'yearly';
        if($scope.hmrGraph) {
          $scope.hmrLineGraph = true;
          $scope.hmrBarGraph = false;
          $scope.getNonDayHMRGraphData();
      } else if ($scope.complianceGraph) {
          $scope.getComplianceGraphData();
      }
    }
   
    // Monthly chart
    $scope.monthlyChart = function(datePicker) {
      $scope.removeGraph();
      if(datePicker === undefined){
        $scope.calculateTimeDuration(30);
      }
      $scope.format = $scope.groupBy = 'monthly';
      if($scope.hmrGraph) {
        $scope.hmrLineGraph = true;
        $scope.hmrBarGraph = false;
        $scope.getNonDayHMRGraphData();
      } else if ($scope.complianceGraph) {
        $scope.getComplianceGraphData();
      }
    }
    //hmrDayChart
    $scope.dayChart = function() {
      $scope.removeGraph();
       if($scope.hmrGraph) {
        $scope.format = 'dayWise';
        $scope.hmrLineGraph = false;
        $scope.hmrBarGraph = true;
        $scope.fromTimeStamp = new Date().getTime();
        $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
        $scope.toTimeStamp = $scope.fromTimeStamp;
        $scope.toDate = $scope.fromDate
        $scope.getDayHMRGraphData();
      }
    };


    $scope.showComplianceGraph = function() {
      $scope.complianceGraph = true;
      $scope.hmrGraph = false;
      if($scope.fromTimeStamp === $scope.toTimeStamp){
        $scope.calculateTimeDuration(7);
      }
      $scope.getComplianceGraphData();
  };

  $scope.createComplianceGraphData = function() {
    delete $scope.complianceGraphData ;
    $scope.complianceGraphData = [];
    angular.forEach($scope.completecomplianceGraphData, function(value) {
          if(value.key.indexOf("pressure") >= 0 && $scope.compliance.secondaryYaxis === 'pressure'){
            value.yAxis = 2
            $scope.complianceGraphData.push(value);
          }
          if(value.key.indexOf("duration") >= 0){
            value.yAxis = 1
            $scope.complianceGraphData.push(value);
          }
          if(value.key.indexOf("frequency") >= 0  && $scope.compliance.secondaryYaxis === 'frequency'){
            value.yAxis = 2
            $scope.complianceGraphData.push(value);
          }
    });
    console.log('compliance graph data!')
    console.log($scope.complianceGraphData)
  }

  $scope.putComplianceGraphLabel = function(chart) {
    var data =  $scope.complianceGraphData
     angular.forEach(data, function(value) {
          if(value.yAxis === 1){
            chart.yAxis1.axisLabel(value.key);
          }
           if(value.yAxis === 2){
            chart.yAxis2.axisLabel(value.key);
          }
    });
  }

  $scope.reCreateComplianceGraph = function() {
    console.log('selected choice:' + $scope.compliance.secondaryYaxis);
    $scope.createComplianceGraphData();
    $scope.drawComplianceGraph();
  };

  $scope.formatXtickForCompliance = function(format,d){
        switch(format) {
          case "weekly":
              return d3.time.format('%A')(new Date(d));
              break;
          case "monthly":
              return 'week ' + dateService.getWeekOfMonth(d);
              break;
          case "yearly":
              return d3.time.format('%B')(new Date(d));
              break;
          default:
              break;
        }
  }

  $scope.drawComplianceGraph = function() {
    d3.select('#complianceGraph svg').selectAll("*").remove();
      nv.addGraph(function() {
      chart = nv.models.multiChart()
      .margin({top: 30, right: 100, bottom: 50, left: 100})
      .color(d3.scale.category10().range());
     // chart.noData("Nothing to see here.");
      chart.tooltipContent($scope.toolTipContentForCompliance($scope.completeComplianceData.actual));
      chart.xAxis.tickFormat(function(d) {
        angular.forEach($scope.completeComplianceData.actual, function(value) {
          if(value.timestamp === d){
             switch($scope.format) {
              case "weekly":
                  return d3.time.format('%A')(new Date(d));
                  break;
              case "monthly":
                  return 'week ' + dateService.getWeekOfMonth(d);
                  break;
              case "yearly":
                  return d3.time.format('%B')(new Date(d));
                  break;
              default:
                  break;
            }
          }
        });
      });
      chart.yAxis1.tickFormat(d3.format(',.0f'));
      chart.yAxis2.tickFormat(d3.format(',.0f'));
      $scope.putComplianceGraphLabel(chart);
        d3.select('#complianceGraph svg')
      .datum($scope.complianceGraphData)
      .transition().duration(500).call(chart);
      return chart;
    });
  };

    /*this should initiate the list of caregivers associated to the patient*/
    $scope.initPatientCaregiver = function(){
      $scope.caregivers = [];      
      $scope.getCaregiversForPatient(localStorage.getItem('patientID'));
    };

    $scope.getPatientById = function(patientId){
      patientService.getPatientInfo(patientId).then(function(response){
        $scope.slectedPatient = response.data;
      }).catch(function(response){});
    };

    $scope.getCaregiversForPatient = function(patientId){
      patientService.getCaregiversLinkedToPatient(patientId).then(function(response){
        $scope.caregivers =  response.data.caregivers;
      }).catch(function(response){});
    };

    $scope.linkCaregiver = function(){
      $state.go('patientdashboardCaregiverAdd', {'patientId': localStorage.getItem('patientID')});
    };

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
    };

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
    };

    $scope.associateCaregiverstoPatient = function(patientId, careGiver){
        patientService.associateCaregiversFromPatient(patientId, careGiver).then(function(response){
        $scope.caregivers =  response.data.user;
        $scope.associateCareGiver = [];$scope.associateCareGiver.length = 0;
        $scope.switchPatientTab('patientdashboardCaregiver');
      }).catch(function(response){
        notyService.showMessage(response.data.ERROR,'warning' );
      });
    };

    $scope.goToCaregiverEdit = function(careGiverId){
      $state.go('patientdashboardCaregiverEdit', {'caregiverId': careGiverId});
    };

    $scope.disassociateCaregiver = function(caregiverId, index){
        patientService.disassociateCaregiversFromPatient(localStorage.getItem('patientID'), caregiverId).then(function(response){
        $scope.caregivers.splice(index, 1);
      }).catch(function(response){});
    };

    $scope.initpatientCaregiverEdit = function(caregiverId){
      $scope.careGiverStatus = "edit";
      $scope.getPatientById(localStorage.getItem('patientID'));
      $scope.editCaregiver(caregiverId);
    };

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
    };

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
    };

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
    };

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
    };

    $scope.getClinicsOfPatient = function(){
      patientService.getClinicsLinkedToPatient(localStorage.getItem('patientID')).then(function(response){
        $scope.clinics = response.data.clinics;                
      }).catch(function(){});
    };
    
    $scope.getHCPsOfPatient = function(){
      patientService.getHCPsLinkedToPatient(localStorage.getItem('patientID')).then(function(response){
        $scope.hcps = response.data.hcpUsers;                
      }).catch(function(){});
    };

    $scope.getNotes = function(){
      var date = '2015-08-21';
      UserService.getNotesOfUser(localStorage.getItem('patientID'), date).then(function(response){
        $scope.notes = response.data;               
      }).catch(function(){});
    };

    $scope.updateNote = function(){
      if($scope.editedNoteText && $scope.editedNoteText.length > 0){
        var data = {};
        data.noteText = $scope.editedNoteText;
        UserService.updateNote(localStorage.getItem('patientID'), '2015-08-21', data).then(function(response){
          $scope.notes = response.data; alert("update : "+JSON.stringify($scope.notes));               
        }).catch(function(){
          $scope.errorMsg = "Some internal error occurred. Please try after sometime.";
          notyService.showMessage($scope.errorMsg,'warning' );
          $scope.cancelEditNote();
        });
      }else{
        $scope.noteError = "Please add some text.";
      }
    };

    $scope.createNote = function(){     
        if($scope.textNote && $scope.textNote.length > 0){
          var data = {};
          data.noteText = $scope.textNote;
          data.userId = localStorage.getItem('patientID');
          UserService.createNote(localStorage.getItem('patientID'), data).then(function(response){
          $scope.addNote = false;
          $scope.textNote = "";     
          $scope.getNotes();
        }).catch(function(){});
      }else{
        $scope.noteTextError = "Please add some text.";
        return false;
      }
      
    };

    $scope.deleteNote = function(noteId){
      UserService.deleteNote(noteId).then(function(response){
      $scope.notes = "";
      //$scope.notes.length = 0;                   
      }).catch(function(){});
    };

    $scope.openAddNote = function(){
      $scope.addNote = true;
      //$("#noteText").css("display", "block");
    };

    $scope.cancelAddNote = function(){
      $scope.addNote = false;
      //$("#noteText").css("display", "block");
    };

    $scope.initPatientDashboard = function(){
      $scope.editNote = false;
      $scope.textNote = "";
      $scope.weeklyChart();
      $scope.getNotes();
    };

    $scope.openEditNote = function(){
      $scope.editNote = true;
      $scope.editedNoteText = $scope.notes.note;
    };

    $scope.cancelEditNote = function(){
      $scope.editNote = false;
    };

    $scope.init();
});

