'use strict';

angular.module('hillromvestApp')
    .service('graphUtil', function (dateService) {
      
      this.convertIntoHMRLineGraph = function(data) {
        var pointSet = [];
        var graphData = {};
        var graphDataList =[];
        angular.forEach(data, function(value) {
          var point = [];
          point.push(value.timestamp);
          point.push(Math.floor(value.duration));
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
          point.push(value.startTime);
          point.push(Math.floor(Math.floor(value.hmr/60)));
          pointSet.push(point);
        });
        graphData["values"] = pointSet;
        graphDataList.push(graphData);
      return graphDataList;
      }
      var insertData = function(data,value) {
        data.frequency = data.frequency + value.frequency;
        data.pressure = data.pressure + value.pressure;
        data.durationInMinutes = data.durationInMinutes + value.durationInMinutes;
        data.programmedCaughPauses = data.programmedCaughPauses + value.programmedCaughPauses;
        data.normalCaughPauses = data.normalCaughPauses + value.normalCaughPauses;
        data.caughPauseDuration = data.caughPauseDuration + value.caughPauseDuration;
        if(value.hmr > data.hmr){
          data.hmr = value.hmr;
        }
      }

      this.formatDayWiseDate = function(data) {
        var list = [];
        var data1 = {frequency : 0, pressure : 0, durationInMinutes : 0, programmedCaughPauses : 0, 
                      normalCaughPauses : 0, caughPauseDuration : 0, hmr : 0};
        var data2 = {frequency : 0, pressure : 0, durationInMinutes : 0, programmedCaughPauses : 0, 
                    normalCaughPauses : 0, caughPauseDuration : 0, hmr : 0};
        var data3 = {frequency : 0, pressure : 0, durationInMinutes : 0, programmedCaughPauses : 0, 
                    normalCaughPauses : 0, caughPauseDuration : 0, hmr : 0};
        var data4 = {frequency : 0, pressure : 0, durationInMinutes : 0, programmedCaughPauses : 0, 
                    normalCaughPauses : 0, caughPauseDuration : 0, hmr : 0};
        var data5 = {frequency : 0, pressure : 0, durationInMinutes : 0, programmedCaughPauses : 0, 
                    normalCaughPauses : 0, caughPauseDuration : 0, hmr : 0};
        var data6 = {frequency : 0, pressure : 0, durationInMinutes : 0, programmedCaughPauses : 0, 
                      normalCaughPauses : 0, caughPauseDuration : 0, hmr : 0};
        angular.forEach(data, function(value) {
          var timeSlot = dateService.getTimeIntervalFromTimeStamp(value.startTime);
          switch(timeSlot){
            case 'Midnight - 4 AM':
              data1.startTime = dateService.getTimeStampForTimeSlot(value.date,1);
              insertData(data1,value);
              break;
            case '4 AM - 8 AM':
              data2.startTime = dateService.getTimeStampForTimeSlot(value.date,2);
              insertData(data2,value);
              break;
            case '8 AM - 12 PM':
              data3.startTime = dateService.getTimeStampForTimeSlot(value.date,3);
              insertData(data3,value);
              break;
            case '12 PM - 4 PM':
              data4.startTime = dateService.getTimeStampForTimeSlot(value.date,4);
              insertData(data4,value);
              break;
            case '4 PM - 8 PM':
              data5.startTime = dateService.getTimeStampForTimeSlot(value.date,5);
              insertData(data5,value);
              break;
            case '8 PM - Midnight':
              data6.startTime = dateService.getTimeStampForTimeSlot(value.date,6);
              insertData(data6,value);
              break;
          }
        });
        if(data1.startTime !== undefined)
          list.push(data1);
        if(data2.startTime !== undefined)
          list.push(data2);
        if(data3.startTime !== undefined)
          list.push(data3);
        if(data4.startTime !== undefined)
          list.push(data4);
        if(data5.startTime !== undefined)
          list.push(data5);
        if(data6.startTime !== undefined)
          list.push(data6);
        return list;
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
