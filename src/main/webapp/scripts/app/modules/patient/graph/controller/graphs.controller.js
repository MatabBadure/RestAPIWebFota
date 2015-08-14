'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state) {

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
    }

    $scope.init();

});
