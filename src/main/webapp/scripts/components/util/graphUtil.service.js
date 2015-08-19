'use strict';

angular.module('hillromvestApp')
    .service('graphUtil', function () {
      
      this.convertIntoHMRLineGraph = function(data) {
        var pointSet = [];
        var graphData = {};
        var graphDataList =[];
        angular.forEach(data, function(value) {
          var point = [];
          point.push(value.timeStamp);
          point.push(Math.floor(value.secondsSpentInTreatment/(60)));
          pointSet.push(point);
        });
        graphData["values"] = pointSet;
        graphDataList.push(graphData);
      return graphDataList;
      }

      this.convertIntoHMRBarGraph = function(data) {
        var pointSet = [];
        var graphData = {};
        var graphDataList =[];
        angular.forEach(data, function(value) {
          var point = [];
          point.push(value.start);
          point.push(Math.floor(value.secondsSpentInTreatment/(60)));
          pointSet.push(point);
        });
        graphData["values"] = pointSet;
        graphDataList.push(graphData);
      return graphDataList;
      }

      this.convertIntoComplianceGraph = function(data) {
        var graphDataList =[];
        var pressureValues = [];
        var frequencyValues = [];
        var durationValues = [];
        var pressureObject = {};
        var frequencyObject = {};
        var durationObject = {};
        angular.forEach(data, function(value) {
          var pressurePoint = {};
          var durationPoint = {};
          var frequencyPoint = {};
          pressurePoint.x = value.date;
          pressurePoint.y = value.therapyData.weightedAvgPressure
          pressureValues.push(pressurePoint);
          durationPoint.x = value.date;
          durationPoint.y = Math.floor(value.therapyData.secondsSpentInTreatment/(60))
          durationValues.push(durationPoint);
          frequencyPoint.x = value.date;
          frequencyPoint.y = value.therapyData.weightedAvgFrequency
          frequencyValues.push(frequencyPoint);
        });
        pressureObject.values = pressureValues;
        pressureObject.key = 'pressure';
        pressureObject.yAxis = 2;
        pressureObject.type = 'area';
        frequencyObject.values = frequencyValues;
        frequencyObject.key = 'frequency';
        frequencyObject.yAxis = 2;
        frequencyObject.type = 'area';
        durationObject.values = durationValues;
        durationObject.key = 'duration';
        durationObject.yAxis = 1;
        durationObject.type = 'area';

        graphDataList.push(durationObject);
        graphDataList.push(pressureObject);
        graphDataList.push(frequencyObject);
      return graphDataList;
      }
    });
