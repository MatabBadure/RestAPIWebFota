'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope) {
  $scope.options = {
            chart: {
                type: 'stackedAreaChart',
                height: 450,
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
        ]
});
