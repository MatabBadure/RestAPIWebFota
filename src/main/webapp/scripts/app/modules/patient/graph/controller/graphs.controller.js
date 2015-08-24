'use strict';

angular.module('hillromvestApp')
.controller('graphController', function($scope, $state, patientDashBoardService, StorageService, dateService, graphUtil) {
    var chart;

    $scope.init = function() {
      $scope.hmrLineGraph = true;
      $scope.hmrBarGraph = false;
      $scope.hmrGraph = true;
      $scope.format = 'weekly';
      $scope.compliance = {};
      $scope.compliance.pressure = true;
      $scope.compliance.duration = true;
      $scope.compliance.frequency = false;
      $scope.toTimeStamp = new Date().getTime();
      //$scope.patientId = StorageService.get('patientID');
      $scope.fromTimeStamp = dateService.getnDaysBackTimeStamp(7);
      $scope.fromDate = dateService.getDateFromTimeStamp($scope.fromTimeStamp);
      $scope.toDate = dateService.getDateFromTimeStamp($scope.toTimeStamp);
      $scope.patientId = 160;
      $scope.handlelegends();
      if ($state.current.name === 'patientdashboard') {
        $scope.weeklyChart();
      }
    };

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
      } else if(days <= 7){
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

    $scope.xAxisTickFormatFunction = function(format){
      return function(d){
        switch(format) {
          case "weekly":
              return d3.time.format('%a')(new Date(d));
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
        angular.forEach($scope.completeGraphData, function(value) {
          if(value.timestamp === e.point[0]){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.timestamp) + '</h6>' +
                '<p> Treatment/Day ' + value.treatmentsPerDay + '</p>' +
                '<p> Frequency ' + value.weightedAvgFrequency + '</p>' +
                '<p> Pressure ' + value.weightedAvgPressure + '</p>' +
                '<p> Caugh Pauses ' + value.normalCoughPauses + '</p>';
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



                '<ul class="graph_ul">' +
                  '<li><span class="pull-left">' + 'Frequency' + '</span><span class="pull-right value">' + value.frequency + '</span></li>' +
                  '<li><span class="pull-left">' + 'Pressure' +'</span><span class="pull-right value">' + value.pressure +'</span></li>' +
                  '<li><span class="pull-left">' + 'Cough Pauses' +'</span><span class="pull-right value">' + (value.normalCaughPauses + value.programmedCaughPauses) +'</span></li>' +
                '</ul>';

          }
        });
      return toolTip;   
      }
    }

    $scope.toolTipContentForCompliance = function(data){
      return function(key, x, y, e, graph) {
        var toolTip = '';
        angular.forEach(data, function(value) {
          if(value.date === e.point.x){
              toolTip =
                '<h6>' + dateService.getDateFromTimeStamp(value.date) + '</h6>' +
                '<ul class="graph_ul">' +
                  '<li><span class="pull-left">' + 'Treatment/Day' + '</span><span class="pull-right value">' + value.therapyData.treatmentsPerDay + '</span></li>' +
                  '<li><span class="pull-left">' + 'Frequency' +'</span><span class="pull-right value">' + value.therapyData.weightedAvgFrequency +'</span></li>' +
                  '<li><span class="pull-left">' + 'Pressure' +'</span><span class="pull-right value">' + value.therapyData.weightedAvgPressure +'</span></li>' +
                  '<li><span class="pull-left">' + 'Cough Pauses' +'</span><span class="pull-right value">' + value.therapyData.normalCoughPauses +'</span></li>' +
                '</ul>';
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
      /*$scope.completeGraphData = HMRWeeklyGraphData;
      $scope.graphData = graphUtil.convertIntoHMRLineGraph($scope.completeGraphData);*/
      patientDashBoardService.getHMRGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        //Will get response data from real time API once api is ready
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData === []){
          $scope.graphData = [];
        } else {
          $scope.graphData = graphUtil.convertIntoHMRLineGraph($scope.completeGraphData);
        }
      }).catch(function(response) {
        $scope.graphData = [];
      });
    };

    $scope.getDayHMRGraphData = function() {
     /* $scope.completeGraphData = HMRDayGraphData;
      $scope.graphData = graphUtil.convertIntoHMRBarGraph($scope.completeGraphData);*/
      patientDashBoardService.getHMRBarGraphPoints($scope.patientId, $scope.fromTimeStamp).then(function(response){
        //Will get response data from real time API once api is ready
        $scope.completeGraphData = response.data;
        if($scope.completeGraphData === []){
           $scope.graphData = [];
         } else {
          $scope.completeGraphData = graphUtil.formatDayWiseDate($scope.completeGraphData);
           $scope.graphData = graphUtil.convertIntoHMRBarGraph($scope.completeGraphData);
         }
      }).catch(function(response) {
        $scope.graphData = [];
      });
    };

    $scope.getComplianceGraphData = function() {
      $scope.completeComplianceData = complianceGraphData;
      $scope.completecomplianceGraphData = graphUtil.convertIntoComplianceGraph($scope.completeComplianceData);
     /* patientDashBoardService.getComplianceGraphPoints($scope.patientId, $scope.fromTimeStamp, $scope.toTimeStamp, $scope.groupBy).then(function(response){
        //Will get response data from real time API once api is ready
      }).catch(function(response) {});*/
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
        $scope.createComplianceGraphData();
        $scope.drawComplianceGraph();
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
          $scope.createComplianceGraphData();
          $scope.drawComplianceGraph();
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
        $scope.createComplianceGraphData();
        $scope.drawComplianceGraph();
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
        $scope.calculateGraphDuration(7);
      }
      $scope.getComplianceGraphData();
      $scope.createComplianceGraphData();
      $scope.drawComplianceGraph();
  };

  $scope.createComplianceGraphData = function() {
    delete $scope.complianceGraphData ;
    $scope.complianceGraphData = [];
    var count = 1;
    angular.forEach($scope.completecomplianceGraphData, function(value) {
          if(value.key.indexOf("pressure") >= 0 && $scope.compliance.pressure){
            value.yAxis = count++;
            $scope.complianceGraphData.push(value);
          }
          if(value.key.indexOf("duration") >= 0 && $scope.compliance.duration){
            value.yAxis = count++;
            $scope.complianceGraphData.push(value);
          }
          if(value.key.indexOf("frequency") >= 0  && $scope.compliance.frequency){
            value.yAxis = count++;
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
      }
      if($scope.compliance.frequency === false ){
        $scope.frequencyIsDisabled = true;
      }
      if($scope.compliance.duration === false ){
        $scope.durationIsDisabled = true;
      }
    } else if(count < 2 ) {
       $scope.pressureIsDisabled = false;
       $scope.frequencyIsDisabled = false;
       $scope.durationIsDisabled = false;
    }
  }
  $scope.reDrawCompliancegraph = function() {

  };

  $scope.reCreateComplianceGraph = function() {
    $scope.handlelegends();
    $scope.complianceToggle = !$scope.complianceToggle;
    $scope.createComplianceGraphData();
    $scope.drawComplianceGraph();
  };

  $scope.formatXtickForCompliance = function(format,d){
        switch(format) {
          case "weekly":
              return d3.time.format('%a')(new Date(d));
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
      //.margin({top: 30, right: 100, bottom: 50, left: 100})
      .color(d3.scale.category10().range());
      chart.tooltipContent($scope.toolTipContentForCompliance($scope.completeComplianceData));
      chart.xAxis.tickFormat(function(d) {
        switch($scope.format) {
          case "weekly":
              return d3.time.format('%a')(new Date(d));
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
      });
      chart.yAxis1.tickFormat(d3.format(',.0f'));
      chart.yAxis2.tickFormat(d3.format(',.0f'));
      $scope.putComplianceGraphLabel(chart);
        d3.select('#complianceGraph svg')
      .datum($scope.complianceGraphData)
      .transition().duration(500).call(chart);

       //var date_now = (new Date()).getTime();

        // If we want a fixed position
        // var date_now = (new Date("2015/03/18")).getTime();

        // Get position of now date on xAxis thanks to scale func
        //var date_now_xposition = chart.xAxis.scale()(date_now);

        // When nvd3 draw chart it append a rectangle of the inner
        // size of our chart (without label axis or title)
        // let's get it to know height and width of our rectangle
        //var rect_background = document.getElementsByClassName("nv-stackedarea")[0].firstChild.firstChild;

        // draw a background rectangle to indicate the future
        // Here we will append new rectangle to ".nv-groups"
        // this allow to avoid to break nvd3 hover fearure on chart
        //var back_height = d3.select('#complianceGraph svg .x .nv-wrap').
        d3.select('#complianceGraph svg .x .nv-wrap g').append("rect")
          .attr("x", 0) // start rectangle on the good position
          .attr("y", -  270) // no vertical translate
          .attr("width", 93 +"%") // correct size
          .attr("height", 270+"px") // full height
          .attr("fill", "rgba(66,139,202, 0.2)"); // transparency color to see grid

        //nv.utils.windowResize(chart.update);


      return chart;
    });
  }
    $scope.init();
});

