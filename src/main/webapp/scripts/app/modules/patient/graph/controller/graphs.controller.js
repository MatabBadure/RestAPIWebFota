'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state, patientDashBoardService, StorageService, dateService, graphUtil, patientService, UserService, $stateParams, notyService, $timeout) {
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
      $scope.handlelegends();
      $scope.toTimeStamp = new Date().getTime();
      $scope.compliance.secondaryYaxis = 'pressure';
      $scope.compliance.primaryYaxis = 'duration';
      $scope.hmrRunRate = 0;
      $scope.adherenceScore = 0;
      $scope.missedtherapyDays = 0;
      $scope.minFrequency = 0;
      $scope.maxFrequency = 0;
      $scope.minPressure = 0;
      $scope.maxPressure = 0;
      $scope.minDuration = 0;
      $scope.maxDuration = 0;
      $scope.yAxisRangeForHMRLine = {};
      $scope.yAxisRangeForCompliance = {};
      $scope.yAxis1Min = 0;
      $scope.yAxis2Min = 0;
      $scope.getHmrRunRateAndScore();
      $scope.getMissedTherapyDaysCount();
      //$scope.patientId = StorageService.get('patientID');
      $scope.fromTimeStamp = dateService.getnDaysBackTimeStamp(7);
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);   
      $scope.getPatientById(localStorage.getItem('patientID'));
      var currentRoute = $state.current.name;
      $scope.showNotes = false;
      $scope.patientTab = currentRoute;
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


    angular.element('#edit_date').datepicker({    
          endDate: '+0d',   
          autoclose: true}).    
          on('changeDate', function(ev) {   
          var selectedDate = angular.element('#edit_date').datepicker("getDate");   
          var _month = (selectedDate.getMonth()+1).toString();    
          _month = _month.length > 1 ? _month : '0' + _month;   
          var _day = (selectedDate.getDate()).toString();   
          _day = _day.length > 1 ? _day : '0' + _day;   
          var _year = (selectedDate.getFullYear()).toString();    
          var dob = _month+"/"+_day+"/"+_year;    
          $scope.patient.dob = dob;   
          var age = dateService.getAge(selectedDate);   
          angular.element('.age').val(age);   
          $scope.patient.age = age;   
          if (age === 0) {    
            $scope.form.$invalid = true;    
          }   
          angular.element("#edit_date").datepicker('hide');   
          $scope.$digest();   
        });   
    

  /*-----Date picker for dashboard----*/

    $scope.calculateDateFromPicker = function(picker) {
      $scope.fromTimeStamp = new Date(picker.startDate._d).getTime();
      $scope.toTimeStamp = new Date(picker.endDate._d).getTime();
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);
      if ($scope.fromDate === $scope.toDate ) {
        $scope.fromTimeStamp = $scope.toTimeStamp;
      }
    };

    $scope.removeGraph = function() {
      d3.selectAll('#complianceGraph svg').selectAll("*").remove();
      d3.selectAll('#HMRLineGraph svg').selectAll("*").remove();
      d3.selectAll('svg').selectAll("*").remove();
    }

    $scope.hmrBarSetMinMax = function() {
      var values = [];
      values.push($scope.yAxisRangeForHMRBar.min);
      values.push($scope.yAxisRangeForHMRBar.max);
      return values;
    }

    $scope.hmrLineSetMinMax = function() {
      var values = [];
      values.push($scope.yAxisRangeForHMRLine.min);
      values.push($scope.yAxisRangeForHMRLine.max);
      return values;
    }

    $scope.drawGraph = function() {
      var days = dateService.getDateDiffIndays($scope.fromTimeStamp,$scope.toTimeStamp);
      if(days === 0){
        $scope.format = 'dayWise';
        $scope.hmrLineGraph = false;
        $scope.hmrBarGraph = true;
        $scope.removeGraph();
        $scope.getDayHMRGraphData();
      } else if(days <= 7) {
        $scope.weeklyChart($scope.fromTimeStamp);
      } else if ( days > 7 && days <= 32 ) {
        $scope.monthlyChart($scope.fromTimeStamp);
      } else if ( days > 32) {
         $scope.yearlyChart($scope.fromTimeStamp);
      }
    };

    $scope.opts = {
      eventHandlers: {'apply.daterangepicker': function(ev, picker) {
        $scope.calculateDateFromPicker(picker);
        $scope.drawGraph();
        }
      },
      opens: 'left'
    }

  $scope.dates = {startDate: null, endDate: null};
    
    $scope.getHmrRunRateAndScore = function() {
      patientDashBoardService.getHMRrunAndScoreRate($scope.patientId, $scope.toTimeStamp).then(function(response){
        if(response.status === 200 ){
          $scope.hmrRunRate = response.data.hmrRunRate;
          $scope.adherenceScore = response.data.score;
        }
      }).catch(function(response) {
      });
    }

    $scope.getMissedTherapyDaysCount = function() {
      patientDashBoardService.getMissedTherapyDaysCount($scope.patientId).then(function(response){
        if(response.status === 200 ){
          $scope.missedtherapy = response.data;
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
    $scope.isActivePatientTab = function(tab) {
      console.log('hello there' + $scope.patientTab.indexOf(tab));
      if ($scope.patientTab.indexOf(tab) !== -1) {
        return true;
      } else {
        return false;
      }
    };

    $scope.switchPatientTab = function(status){
      $scope.patientTab = status;
      $state.go(status, {'patientId': $stateParams.patientId});
    };

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
                /*'<p> Treatment/Day ' + value.treatmentsPerDay + '</p>' +
                '<p> Frequency ' + value.weightedAvgFrequency + '</p>' +
                '<p> Pressure ' + value.weightedAvgPressure + '</p>' +
                '<p> Cough Pauses ' + value.normalCoughPauses + '</p>';*/




                '<ul class="graph_ul">' +
                  '<li><span class="pull-left">' + 'Treatment/Day ' +'</span><span class="pull-right value">' + value.treatmentsPerDay +'</span></li>' +
                  '<li><span class="pull-left">' + 'Frequency' + '</span><span class="pull-right value">' + value.weightedAvgFrequency  + '</span></li>' +
                  '<li><span class="pull-left">' + 'Pressure' +'</span><span class="pull-right value">' + value.weightedAvgPressure  +'</span></li>' +
                  '<li><span class="pull-left">' + 'Cough Pauses' +'</span><span class="pull-right value">' + value.normalCoughPauses +'</span></li>' +
                '</ul>';
          }
        });
      return toolTip;   
      }
    };

    $scope.toolTipContentBarChart = function(){
      return function(key, x, y, e, graph) {
        var toolTip = '';
        angular.forEach($scope.completeGraphData, function(value) {
          if(value.startTime === e.point[0] && value.hmr !== 0 ){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.startTime) + '</h6>' +
                /*'<p> Frequency ' + value.frequency + '</p>' +
                '<p> Pressure ' + value.pressure + '</p>' +
                '<p> Cough Pauses ' + (value.normalCaughPauses + value.programmedCaughPauses) + '</p>';*/

                '<ul class="graph_ul">' +
                  '<li><span class="pull-left">' + 'Frequency' + '</span><span class="pull-right value">' + value.frequency  + '</span></li>' +
                  '<li><span class="pull-left">' + 'Pressure' +'</span><span class="pull-right value">' + value.pressure +'</span></li>' +
                  '<li><span class="pull-left">' + 'Cough Pauses' +'</span><span class="pull-right value">' + (value.normalCaughPauses + value.programmedCaughPauses) +'</span></li>' +
                '</ul>';
          }
        });
      return toolTip;   
      }
    };

    $scope.toolTipContentForCompliance = function(data){
      return function(key, x, y, e, graph) {
        var toolTip = '';
        angular.forEach(data, function(value) {
          if(value.start === e.point.timeStamp){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.start) + '</h6>' +
                /*'<p> Treatment/Day ' + value.treatmentsPerDay + '</p>' +
                '<p> Frequency ' + value.weightedAvgFrequency + '</p>' +
                '<p> Pressure ' + value.weightedAvgPressure + '</p>' +
                '<p> Caugh Pauses ' + value.normalCoughPauses + '</p>';
*/

                '<ul class="graph_ul">' +
                  '<li><span class="pull-left">' + 'Treatment/Day' + '</span><span class="pull-right value">' + value.treatmentsPerDay + '</span></li>' +
                  '<li><span class="pull-left">' + 'Frequency' +'</span><span class="pull-right value">' + value.weightedAvgFrequency +'</span></li>' +
                  '<li><span class="pull-left">' + 'Pressure' +'</span><span class="pull-right value">' + value.weightedAvgPressure +'</span></li>' +
                  '<li><span class="pull-left">' + 'Cough Pauses' +'</span><span class="pull-right value">' + value.normalCoughPauses +'</span></li>' +
                '</ul>';
          }
        });
      return toolTip;   
      }
    };

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

    $scope.yAxisFormatFunction = function() {
      return function(d) {
        var tickVals = [];
        var values = d[0].values;
        for(var i in values){
          tickVals.push(values[i][0]);
        }
        return tickVals;
      };
    }

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
      $scope.removeGraph();
    };

    $scope.getNonDayHMRGraphData = function() {
      patientDashBoardService.getHMRGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        //Will get response data from real time API once api is ready
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData.actual === undefined){
          $scope.graphData = [];
        } else {
          $scope.yAxisRangeForHMRLine = graphUtil.getYaxisRangeLineGraph($scope.completeGraphData);
          //$scope.completeGraphData = graphUtil.sortGraphData($scope.completeGraphData);
          //$scope.completeGraphData = graphUtil.getCompleteGraphData($scope.completeGraphData,$scope.format,$scope.fromTimeStamp,$scope.toTimeStamp);
          $scope.graphData = graphUtil.convertIntoHMRLineGraph($scope.completeGraphData);
          console.log('HMR Non-Day graph data : ' + JSON.stringify($scope.graphData));
          console.log($scope.yAxisRangeForHMRLine);
          $scope.customizationInLineGraph = function() {

            /* Mark red color for missed therapy  -- start --*/
         var circlesInHMR = d3.select('#HMRLineGraph svg').select('.nv-scatterWrap').select('.nv-group.nv-series-0').selectAll('circle')[0];
         var count = 0;
         var missedTherapyCircles = [];
         angular.forEach($scope.completeGraphData.actual,function(value){
          if(value.missedTherapy === true){
            missedTherapyCircles.push(circlesInHMR[count]);
          }
          count++;
         })
         console.log(missedTherapyCircles);
         angular.forEach(missedTherapyCircles,function(circle){
          d3.select('#HMRLineGraph svg').select('.nv-scatterWrap').select('.nv-group.nv-series-0').append('circle')
          .attr('cx',circle.attributes.cx.value)
          .attr('cy',circle.attributes.cy.value)
          .attr('r',4.5)
          .attr('class','missed_therapy_node');
         })

         /* Mark red color for missed therapy  -- end --*/
          };

          var circleSelectorInHMR = d3.select('#HMRLineGraph svg').select('.nv-scatterWrap').select('.nv-group.nv-series-0').selectAll('circle')[0];
          var circleCount;
          if(circleSelectorInHMR !== undefined) {
            circleCount = circleSelectorInHMR.length;
          }
          var count = 10;
          $scope.waitFunction = function waitHandler() {
            circleSelectorInHMR = d3.select('#HMRLineGraph svg').select('.nv-scatterWrap').select('.nv-group.nv-series-0').selectAll('circle')[0];
            if(circleSelectorInHMR !== undefined) {
            circleCount = circleSelectorInHMR.length;
            }
            console.log('count :' + count);
            console.log('circle count :' + circleCount);

            if(circleCount > 0 || count === 0 ) {
              console.log('wait over !!');
              $scope.customizationInLineGraph();
              return false;
            } else {
              count --;
            }
            $timeout(waitHandler, 1000);
          }
          $scope.waitFunction();
        }
      }).catch(function(response) {
        $scope.graphData = [];
      });
    };

    $scope.reCreateComplianceGraph = function() {
      $scope.removeGraph();
      $scope.handlelegends();
      $scope.createComplianceGraphData();
      $scope.drawComplianceGraph();
    };

    $scope.handlelegends = function() {
      var count = 0 ;
      if($scope.compliance.pressure === true ){
        count++;
      }
      if($scope.compliance.duration === true ){
        count++;
      }
      if($scope.compliance.frequency === true ){
        count++;
      }
      if(count === 2 ) {
        if($scope.compliance.pressure === false ){
          $scope.pressureIsDisabled = true;
          $scope.frequencyIsDisabled = false;
          $scope.durationIsDisabled = false;
        }
        if($scope.compliance.frequency === false ){
          $scope.pressureIsDisabled = false;
          $scope.frequencyIsDisabled = true;
          $scope.durationIsDisabled = false;
        }
        if($scope.compliance.duration === false ){
          $scope.pressureIsDisabled = false;
          $scope.frequencyIsDisabled = false;
          $scope.durationIsDisabled = true;
        }
      } else if(count === 1 ) {
        if($scope.compliance.pressure === true ){
          $scope.pressureIsDisabled = true;
          $scope.frequencyIsDisabled = false;
          $scope.durationIsDisabled = false;
        }
        if($scope.compliance.frequency === true ){
          $scope.pressureIsDisabled = false;
          $scope.frequencyIsDisabled = true;
          $scope.durationIsDisabled = false;
        }
        if($scope.compliance.duration === true ){
          $scope.pressureIsDisabled = false;
          $scope.frequencyIsDisabled = false;
          $scope.durationIsDisabled = true;
        }
      }

    }

    $scope.getDayHMRGraphData = function() {
      patientDashBoardService.getHMRBarGraphPoints($scope.patientId, $scope.fromTimeStamp).then(function(response){
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData.actual === undefined){
           $scope.hmrBarGraphData = [];
         } else {
          $scope.completeGraphData = graphUtil.formatDayWiseDate($scope.completeGraphData.actual);
          $scope.yAxisRangeForHMRBar = graphUtil.getYaxisRangeBarGraph($scope.completeGraphData);
          $scope.hmrBarGraphData = graphUtil.convertIntoHMRBarGraph($scope.completeGraphData);
          console.log('HMR Day graph data' + JSON.stringify($scope.hmrBarGraphData));
          console.log($scope.yAxisRangeForHMRBar);

          $scope.customizationForBarGraph = function() {

           d3.select('#hmrBarGraph svg').selectAll('rect.nv-bar')
              .attr("x", 40)
              .attr("width", 70);
          };

          var barCount= d3.select('#hmrBarGraph svg').selectAll('.nv-group .nv-bar')[0].length;
          var count = 5;
          $scope.waitFunction = function waitHandler() {
             barCount = d3.select('#hmrBarGraph svg').selectAll('.nv-group .nv-bar')[0].length;
            if(barCount > 0 || count === 0 ) {
              $scope.customizationForBarGraph();
              return false;
            } else {
              count --;
            }
            $timeout(waitHandler, 1000);
          }
          $scope.waitFunction();

          //$scope.hmrBarGraphData = [{"values":[[1420061400000,null],[1420075800000,null],[1420090200000,28987],[1420104600000,28997],[1420119000000,null],[1420133400000,null]]}]
         }
      }).catch(function(response) {
        $scope.hmrBarGraphData = [];
      });
    };

    $scope.getComplianceGraphData = function(format) {
      patientDashBoardService.getHMRGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        $scope.completeComplianceData = response.data;
        console.log("server response" + JSON.stringify($scope.completeComplianceData));
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
          //$scope.completeComplianceData = graphUtil.sortGraphData($scope.completeComplianceData);  
          $scope.yAxisRangeForCompliance = graphUtil.getYaxisRangeComplianceGraph($scope.completeComplianceData);
          console.log("recommended setting value : " + JSON.stringify($scope.yAxisRangeForCompliance));
          //$scope.completeComplianceData = graphUtil.getCompleteGraphData($scope.completeComplianceData,$scope.format,$scope.fromTimeStamp,$scope.toTimeStamp);
          //$scope.completecomplianceGraphData = graphUtil.sortGraphData($scope.completeComplianceData);
          //console.log(JSON.stringify($scope.completeComplianceData));
          $scope.completecomplianceGraphData = graphUtil.convertIntoComplianceGraph($scope.completeComplianceData.actual);          
          $scope.yAxis1Max = $scope.yAxisRangeForCompliance.maxDuration;
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
    };

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
    };

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
    };
   
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
    };
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
    var count = 0;
    $scope.compliance.secondaryYaxis = '';
    $scope.compliance.primaryYaxis = '';
    angular.forEach($scope.completecomplianceGraphData, function(value) {
      if(value.key.indexOf("pressure") >= 0 && $scope.compliance.pressure === true){
        value.yAxis = ++count;
        value.color = 'rgb(255, 127, 14)';
        if(count === 1){
          $scope.yAxis1Max = $scope.yAxisRangeForCompliance.maxPressure;
          $scope.compliance.primaryYaxis = 'pressure';
        } else if(count === 2){
          $scope.yAxis2Max = $scope.yAxisRangeForCompliance.maxPressure;
          $scope.compliance.secondaryYaxis = 'pressure';
        }
        $scope.complianceGraphData.push(value);
      }
      if(value.key.indexOf("duration") >= 0 && $scope.compliance.duration === true){
        value.yAxis = ++count;
        value.color = 'rgb(31, 119, 180)';
        if(count === 1){
          $scope.yAxis1Max = $scope.yAxisRangeForCompliance.maxDuration;
          $scope.compliance.primaryYaxis = 'duration';
        } else if(count === 2){
          $scope.yAxis2Max = $scope.yAxisRangeForCompliance.maxDuration;
          $scope.compliance.secondaryYaxis = 'duration';
        }
        $scope.complianceGraphData.push(value);
      }
      if(value.key.indexOf("frequency") >= 0  && $scope.compliance.frequency === true){
        value.yAxis = ++count;
        value.color = 'rgb(55, 163, 180)';
        if(count === 1){
          $scope.yAxis1Max = $scope.yAxisRangeForCompliance.maxFrequency;
          $scope.compliance.primaryYaxis = 'frequency';
        } else if(count === 2){
          $scope.yAxis2Max = $scope.yAxisRangeForCompliance.maxFrequency;
          $scope.compliance.secondaryYaxis = 'frequency';
        }
        $scope.complianceGraphData.push(value);
      }
    });
    if( $scope.compliance.frequency === false && $scope.compliance.duration === false && $scope.compliance.pressure === false){
      $scope.yAxis1Max = 0;
      $scope.yAxis2Max = 0;
    }
    console.log('Data for compliance graph : ' + JSON.stringify($scope.complianceGraphData));
  };

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
  };
  $scope.getMinMaxForComplianceGraph = function(){
    switch($scope.compliance.primaryYaxis) {
        case "duration":
            $scope.yAxis1MaxMark = $scope.maxDuration;
            $scope.yAxis1MinMark = $scope.minDuration;
            break;
        case "pressure":
            $scope.yAxis1MaxMark = $scope.maxPressure;
            $scope.yAxis1MinMark = $scope.minPressure;
            break;
        case "frequency":
            $scope.yAxis1MaxMark = $scope.maxFrequency;
            $scope.yAxis1MinMark = $scope.minFrequency;
            break;
        default:
            break;
    }
    switch($scope.compliance.secondaryYaxis) {
        case "duration":
            $scope.yAxis2MaxMark = $scope.maxDuration;
            $scope.yAxis2MinMark = $scope.minDuration;
            break;
        case "pressure":
            $scope.yAxis2MaxMark = $scope.maxPressure;
            $scope.yAxis2MinMark = $scope.minPressure;
            break;
        case "frequency":
            $scope.yAxis2MaxMark = $scope.maxFrequency;
            $scope.yAxis2MinMark = $scope.minFrequency;
            break;
        default:
            break;
    }
  };
  $scope.drawComplianceGraph = function() {
    d3.select('#complianceGraph svg').selectAll("*").remove();
      nv.addGraph(function() {
      var chart = nv.models.multiChart()
      .margin({top: 30, right: 100, bottom: 50, left: 100})
      .showLegend(false)
      .color(d3.scale.category10().range());
     // chart.noData("Nothing to see here.");
      chart.tooltipContent($scope.toolTipContentForCompliance($scope.completeComplianceData.actual));
      //this function to put x-axis labels
      chart.xAxis.tickFormat(function(d) {
          if(d % 1 === 0) {
            var timeStamp = $scope.completecomplianceGraphData[0].values[d-1].timeStamp;
            switch($scope.format) {
                case "weekly":
                    return d3.time.format('%A')(new Date(timeStamp));
                    break;
                case "monthly":
                    return 'week ' + dateService.getWeekOfMonth(timeStamp);
                    break;
                case "yearly":
                    return d3.time.format('%B')(new Date(timeStamp));
                    break;
                default:
                    break;
            }
          }
        });
      chart.yAxis1.tickFormat(d3.format('d'));
      chart.yAxis2.tickFormat(d3.format('d'));
      chart.yDomain1([$scope.yAxis1Min,$scope.yAxis1Max]);
      chart.yDomain2([$scope.yAxis2Min,$scope.yAxis2Max]); 
      var data =  $scope.complianceGraphData;
         angular.forEach(data, function(value) {
              if(value.yAxis === 1){
                chart.yAxis1.axisLabel(value.key);
              }
               if(value.yAxis === 2){
                chart.yAxis2.axisLabel(value.key);
              }
        });
        d3.select('#complianceGraph svg')
      .datum($scope.complianceGraphData)
      .transition().duration(500).call(chart);

        if( $scope.compliance.frequency === false && $scope.compliance.duration === false && $scope.compliance.pressure === false){

        } else {

          /* Mark red color for missed therapy  -- start --*/
         var circlesInCompliance = d3.select('#complianceGraph svg').select('.nv-group.nv-series-0').selectAll('circle')[0];
         var count = 0;
         var missedTherapyCircles = [];
         angular.forEach($scope.completeComplianceData.actual,function(value){
          if(value.missedTherapy === true){
            missedTherapyCircles.push(circlesInCompliance[count]);
          }
          count++;
         })
         console.log(missedTherapyCircles);
         angular.forEach(missedTherapyCircles,function(circle){
          d3.select('#complianceGraph svg').selectAll('.nv-group.nv-series-0').append('circle')
          .attr('cx',circle.attributes.cx.value)
          .attr('cy',circle.attributes.cy.value)
          .attr('r',4.5)
          .attr('class','missed_therapy_node');
         })

         /* Mark red color for missed therapy  -- end --*/
         var bgHeight = d3.select('#complianceGraph svg').selectAll('.x .tick line').attr("y2");;
         var bgWidth = d3.select('#complianceGraph svg ').selectAll('.y1 .tick line').attr("x2");
         d3.select('#complianceGraph svg .nv-axis g').append('rect')
                  .attr("height", Math.abs(bgHeight))
                  .attr("width", bgWidth)
                  .attr("x" , 0)
                  .attr("y" , bgHeight)
                  .attr("class" , "svg_bg");

        var y1AxisMark = d3.select('#complianceGraph svg').selectAll('.y1.axis').selectAll('.nvd3.nv-wrap.nv-axis');
        var y2AxisMark = d3.select('#complianceGraph svg').selectAll('.y2.axis').selectAll('.nvd3.nv-wrap.nv-axis');
        var y1AxisMinMax = d3.select('#complianceGraph svg').selectAll('.y1.axis').selectAll('.nvd3.nv-wrap.nv-axis').select('.nv-axisMaxMin').attr("transform");
        var y2AxisMinMax = d3.select('#complianceGraph svg').selectAll('.y2.axis').selectAll('.nvd3.nv-wrap.nv-axis').select('.nv-axisMaxMin').attr("transform");
        var maxTransform = parseInt(y1AxisMinMax.split(',')[1].replace(y1AxisMinMax,')',''));
        $scope.y1AxisTransformRate = parseInt(y1AxisMinMax.split(',')[1].replace(y1AxisMinMax,')',''))/($scope.yAxis1Max - $scope.yAxis1Min);
        $scope.y2AxisTransformRate = parseInt(y2AxisMinMax.split(',')[1].replace(y2AxisMinMax,')',''))/($scope.yAxis2Max - $scope.yAxis2Min);
        var y1LineLength = d3.select('#complianceGraph svg').selectAll('.y1.axis').selectAll('.nvd3.nv-wrap.nv-axis').selectAll('line').attr('x2');
        var y2LineLength = d3.select('#complianceGraph svg').selectAll('.y2.axis').selectAll('.nvd3.nv-wrap.nv-axis').selectAll('line').attr('x2');
        $scope.getMinMaxForComplianceGraph();
        var y1AxisMinTransform = maxTransform - parseInt($scope.y1AxisTransformRate * $scope.yAxis1MinMark);
        var y1AxisMaxTransform = maxTransform - parseInt($scope.y1AxisTransformRate * $scope.yAxis1MaxMark);
        var y2AxisMinTransform = maxTransform - parseInt($scope.y2AxisTransformRate * $scope.yAxis2MinMark);
        var y2AxisMaxTransform = maxTransform - parseInt($scope.y2AxisTransformRate * $scope.yAxis2MaxMark);

        y1AxisMark.append('g').
        attr('class','minRecommendedLevel').
        attr('transform','translate(0, '+ y1AxisMinTransform + ')').
        append('text').
        text($scope.yAxis1MinMark).
        style('fill','red');

        y1AxisMark.append('g').
        attr('class','maxRecommendedLevel').
        attr('transform','translate(0,'+ y1AxisMaxTransform + ')').
        append('text').
        text($scope.yAxis1MaxMark).
        style('fill','green');

        y2AxisMark.append('g').
        attr('class','minRecommendedLevel').
        attr('transform','translate(0,'+ y2AxisMinTransform + ')').
        append('text').
        text($scope.yAxis2MinMark).
        style('fill','red');

        y2AxisMark.append('g').
        attr('class','maxRecommendedLevel').
        attr('transform','translate(0,'+ y2AxisMaxTransform + ')').
        append('text').
        text($scope.yAxis2MaxMark).
        style('fill','green');
      }
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
      var date = new Date().getTime();      
      UserService.getNotesOfUser(localStorage.getItem('patientID'), date).then(function(response){
        $scope.showNotes = true; 
        $scope.notes = response.data;          
        //scrollPageToTop("add_note_container");        
      }).catch(function(){
        $scope.notes = "";    
      });
    };

    $scope.updateNote = function(){
      if($scope.editedNoteText && $scope.editedNoteText.length > 0){
        var data = {};
        data.noteText = $scope.editedNoteText;
        UserService.updateNote($scope.notes.id, new Date().getTime(), data).then(function(response){
          $scope.notes = response.data; 
          $scope.editNote = false;            
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
      $scope.textNote = "";
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
      $scope.getPatientNotification();
    };

    $scope.openEditNote = function(){
      $scope.editNote = true;
      $scope.editedNoteText = $scope.notes.note;
    };

    $scope.cancelEditNote = function(){
      $scope.editNote = false;
    };

    $scope.getPatientNotification = function(){
      UserService.getPatientNotification(localStorage.getItem("patientID"), new Date().getTime()).then(function(response){                  
        $scope.patientNotifications = response.data;//notifications;//response.data;
        angular.forEach($scope.patientNotifications, function(notification, index) {
          var notificationType = notification.notificationType; 
          if(notificationType.indexOf("HMR_NON_COMPLIANCE AND SETTINGS_DEVIATION") > -1){
            $scope.patientNotifications[index].message = apiresponse.HMR_NON_COMPLIANCE_AND_SETTINGS_DEVIATION;
            $scope.patientNotifications[index].class = "icon-lungs";
          }else if(notificationType.indexOf("HMR_NON_COMPLIANCE") > -1){
             $scope.patientNotifications[index].message = apiresponse.HMR_NON_COMPLIANCE;
             $scope.patientNotifications[index].class = "icon-lungs";
          }else if(notificationType.indexOf("MISSED_THERAPY") > -1){
             $scope.patientNotifications[index].message = apiresponse.MISSED_THERAPY ;
             $scope.patientNotifications[index].class = "icon-lungs";
          }
        });
      }).catch(function(){});
    }

    function scrollPageToTop(divId){
      $('html, body').animate({
          scrollTop: $("#"+divId).offset().top
      }, 2000);
    }

    $scope.init();
});

