'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state) {

  /*-----Date picker for dashboard----*/
/* $scope.date = {
  startDate: '08/12/2015', 
  endDate: '08/18/2015',
  opens: 'center',
  parentEl: '#dp3',
};*/

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

    $scope.init = function() {
      var currentRoute = $state.current.name;
      if ($state.current.name === 'patientdashboard') {
        $scope.hmrWeeklyChart();
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

    $scope.init();

});
