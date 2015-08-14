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

    /*$scope.createGraphData = function() {
      $scope.graphData = [
      {
          "key": "Monthly",
            "values": [ [ 1025409600000 , 10] , [ 1028088000000 , 20] , [ 1030766400000 , 30] , [ 1033358400000 , 40] , [ 1036040400000 , 50] , [ 1038632400000 , 60] , [ 1041310800000 , 70] ]
     }
       ];
    };*/

  /*$scope.options = {
            chart: {
                type: 'stackedAreaChart',
                height: 300,
                margin : {
                    top: 20,
                    right: 20,
                    bottom: 60,
                    left: 40
                },
                useInteractiveGuideline: false,
                interactive: true,
                tooltips: true,
                tooltipContent: function (key) { //return html content
                    return '<h6>' + 'treatment/Day 2' + '</h6>' +
                    '<h6>' + 'frequency 3' + '</h6>' +'<h6>' + 'pressure 67' + '</h6>' 
                          
                },
                x: function(d){return d[0];},
                y: function(d){return d[1];},
                 stacked: {
                  dispatch: {
                    //chartClick: function(e) {console.log("! chart Click !")},
                    elementClick: function(e) {
                        console.log(e)
                        console.log("! element Click !")},
                  },
                  "color" : function (d, i) {
                    var key = i === undefined ? d : i;
                    if(d.values[1][1] == 0){
                        return 'red';
                    } else {
                        return 'blue';
                    }
                  }
                },
                xAxis: {
                    showMaxMin: false,
                    tickFormat: function(d) {
                        return d3.time.format('%a')(new Date(d))
                    }
                },
                yAxis: {
                    tickFormat: function(d){
                        return d3.format(',.2f')(d);
                    }
                },
                "styles": {
                    "classes": {
                      "with-3d-shadow": true,
                      "with-transitions": true,
                      "gallery": false
                    },
                    "css": {}
                },
                "controlOptions": [
                ],
            }
        };

        $scope.data = [
            {
                "key" : "North America" ,
                "values" : [ [ 1438300800000 , 10.174315530051] , [ 1440979200000 , 0] , [ 1443571200000 , 5.366462219461] ]
            },
            {
                "key" : "South America" ,
                "values" : [ [ 1438300800000 , 10.174315530051] , [ 1440979200000 , 0.631084213898] , [ 1443571200000 , 5.366462219461] ]
            }
        ]*/
});
