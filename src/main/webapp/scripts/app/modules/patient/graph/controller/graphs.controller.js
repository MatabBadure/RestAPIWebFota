'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state, patientDashBoardService, StorageService, dateService, graphUtil) {
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
      $scope.yAxisRange = {};
      $scope.getHmrRunRateAndScore();
      //$scope.patientId = StorageService.get('patientID');
      $scope.fromTimeStamp = dateService.getnDaysBackTimeStamp(7);
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);
      if ($state.current.name === 'patientdashboard') {
        $scope.weeklyChart();
      }
    };
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

    $scope.xAxisTickFormatFunction = function(format){
      return function(d){
        return "abc"
        /*switch(format) {
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
        }*/
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
          if(value.startTime === e.point[0] && value.hmr !== 0 ){
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
          if(value.start === e.point.timeStamp){
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
        tickVals.push(d);
        return tickVals;
       /* var values = d[0].values;
        for(var i in values){
          tickVals.push(values[i][0]);
        }
        return tickVals;
      };*/
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
      $scope.removeGraph();
    };

    $scope.getNonDayHMRGraphData = function() {
      patientDashBoardService.getHMRGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        //Will get response data from real time API once api is ready
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData.actual === undefined){
          $scope.graphData = [];
        } else {
          $scope.yAxisRange = graphUtil.getYaxisRangeLineGraph($scope.completeGraphData);
          $scope.completeGraphData = graphUtil.sortGraphData($scope.completeGraphData);
          $scope.graphData = graphUtil.convertIntoHMRLineGraph($scope.completeGraphData);
          console.log(JSON.stringify($scope.graphData));
          $scope.graphData = [{"values":[[1,29567],[2,29567],]}]
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
          console.log(JSON.stringify($scope.graphData));
          $scope.graphData = [{"values":[[1420061400000,null],[1420075800000,null],[1420090200000,28987],[1420104600000,28997],[1420119000000,null],[1420133400000,null]]}]
         }
      }).catch(function(response) {
        $scope.graphData = [];
      });
    };

    $scope.getComplianceGraphData = function(format) {
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
          if($scope.completeComplianceData.actual.length > 1){
            $scope.completeComplianceData = graphUtil.sortGraphData($scope.completeComplianceData);  
          }
          $scope.completeComplianceData = graphUtil.getCompleteGraphData($scope.completeComplianceData,$scope.format,$scope.fromTimeStamp,$scope.toTimeStamp);
          $scope.completecomplianceGraphData = graphUtil.sortGraphData($scope.completeComplianceData);
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
    }

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
    console.log(JSON.stringify($scope.complianceGraphData))
    console.log($scope.complianceGraphData)
    console.log(JSON.stringify($scope.complianceGraphData))
  /*  $scope.complianceGraphData = [
    {   
        "key": "duration",
        "values": [
           
            {
                "x": 3,
                "y": 46
            },
            {
                "x": 4,
                "y": 46
            },
            {
                "x": 5,
                "y": 36
            },
            {
                "x": 6,
                "y": 36
            },
        ],
        "type": "area",
        "yAxis": 1
    },
    {
        "key": "frequency",
        "values": [
            
            {
                "x": 3,
                "y": 79
            },
            {
                "x": 4,
                "y": 66
            },
            {
                "x": 5,
                "y": 51
            },
            {
                "x": 6,
                "y": 51
            }
        ],
        "type": "area",
        "yAxis": 2
    }
];*/
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
  };

  $scope.drawComplianceGraph = function() {
    d3.select('#complianceGraph svg').selectAll("*").remove();
      nv.addGraph(function() {
      var chart = nv.models.multiChart()
      .margin({top: 30, right: 100, bottom: 50, left: 100})
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
          
          /*switch(d) {
              case 1:
                  return "Monday";
                  break;
              case 2:
                  return "Tuesday";
                  break;
              case 3:
                  return "Wednesday";
                  break;
              case 4:
                  return "Thursday";
                  break;
              case 5:
                  return "FriDay";
                  break;
              case 6:
                  return "Saturday";
                  break;
              default:
                  break;
          }*/
        });
      chart.yAxis1.tickFormat(d3.format(',.0f'));
      chart.yAxis2.tickFormat(d3.format(',.0f'));
      //$scope.putComplianceGraphLabel(chart);
      var data =  $scope.complianceGraphData
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
      var circle = d3.select('svg').selectAll("circle")
      if(d3.select('svg').selectAll("circle").length !== 0){
        //d3.select('svg').selectAll("circle")[0][0].classList.push('custom-class');
            console.log(circle);
      }
      var svg = d3.select('#complianceGraph svg');
      function drawCircle(x, y, size) {
        console.log('Drawing circle at', x, y, size);
        svg.append("circle")
          .style("fill", "red")
          .attr("cx", x)
          .attr("cy", y)
          .attr("r", size);
      }
        drawCircle(3.6, 126.45, 5);
        drawCircle(300, 400, 15);
        d3.select('#complianceGraph svg').selectAll('.y1.axis').selectAll('.nvd3.nv-wrap.nv-axis').append('g').attr('class','tick').attr('transform','translate(0,200)').append('text').text('20');
      return chart;
    });
  } 
    $scope.init();
});

