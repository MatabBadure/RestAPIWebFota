'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope) {

    $scope.graphData = [
      {
          "key": "Weekly",
            "values": [ [ 1025409600000 , 0] , [ 1028088000000 , 6.3382185140371] , [ 1030766400000 , 5.9507873460847] , [ 1033358400000 , 11.569146943813] , [ 1036040400000 , 5.4767332317425] , [ 1038632400000 , 0.50794682203014] , [ 1041310800000 , 5.5310285460542] ]
     }
       ];
    $scope.toolTipContentFunction = function(){
    return function(key, x, y, e, graph) {
        return  'Super New Tooltip' +
            '<h1>' + key + '</h1>' +
            '<p>' +  y + ' at ' + x + '</p>'
        }
    }

    $scope.xAxisTickFormatFunction = function(){
    return function(d){
      var weekNumber = d3.time.format('%a')(new Date(d));
        return weekNumber;
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

    $scope.createGraphData = function() {
      $scope.graphData = [
      {
          "key": "Monthly",
            "values": [ [ 1025409600000 , 10] , [ 1028088000000 , 20] , [ 1030766400000 , 30] , [ 1033358400000 , 40] , [ 1036040400000 , 50] , [ 1038632400000 , 60] , [ 1041310800000 , 70] ]
     }
       ];
    };

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
