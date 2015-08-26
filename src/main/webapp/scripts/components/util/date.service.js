'use strict';
angular.module('hillromvestApp')
  .factory('dateService', function(localStorageService, noty) {
    return {
      getAge: function(dob) {
        var currentDate = new Date(),
         years = currentDate.getFullYear() - dob.getFullYear(),
         age = 0;

        age = years;
        if(years === 0) {
          age = 1;
        }
        if(years < 0) {
          age = 0;
        }
        return age;
      },

      getDate: function(date) {
        return new Date(date);
      },
      getMonth: function(month) {
        month = (month + 1).toString();
        month = month.length > 1 ? month : '0' + month;
        return month;
      },
      getDay: function(day) {
        day = (day).toString();
        day = day.length > 1 ? day : '0' + day;
        return day;
      },
      getYear:function(year) {
        return (year).toString();
      },
      getnDaysBackTimeStamp:function(n) {
        return new Date().getTime() - (1000*60*60*24*n);
      },
      getDayOfYear: function(date) {
        var date = new Date(date);
        var start = new Date(date.getFullYear(), 0, 0);
        var diff = date - start;
        var oneDay = 1000 * 60 * 60 * 24;
        var day = Math.floor(diff / oneDay);
        return day
      },
      getWeekOfMonth: function(d) {
        var dayCount = this.getDayOfYear(d);
        var d = new Date(d);
        var date = d.getDate();
        var day = d.getDay();
        var weekOfMonth = Math.ceil((dayCount) / 7);
        return weekOfMonth;
      },
      getDateFromTimeStamp: function(data) {
        var date = new Date(data);
        return this.getDay(date.getDate()) + '/' + this.getMonth(date.getMonth(date)) + '/' + this.getYear(date.getFullYear(date))
      },
      getDateDiffIndays: function(fromTimeStamp,toTimeStamp) {
        return Math.ceil((toTimeStamp - fromTimeStamp)/(1000*60*60*24));
      },
      getTimeStampForTimeSlot: function(date,timeSlot) {
        return this.getStartTimeStampOfDay(new Date(date).getTime()) + (((timeSlot*4)-1)*60*60*1000);
      },
      getStartTimeStampOfDay: function(timeStamp) {
        return timeStamp - (5.5*60*60*1000);
      },
      getListOfDaysInTimeStamp: function(fromTimestamp,toTimeStamp) {
        var daysList = [];
        while (fromTimestamp <= toTimeStamp) {
          daysList.push(fromTimestamp)
          fromTimestamp = fromTimestamp + 24*60*60*1000;
        }
        return daysList;
      },
      getListOfWeeksInTimeStamp: function(fromTimestamp, toTimeStamp) {
        var weekList = [];
        while (fromTimestamp <= toTimeStamp) {
          weekList.push(fromTimestamp)
          fromTimestamp = fromTimestamp + 7*24*60*60*1000;
        }
        return weekList;
      },
      getDaysInMonth: function(monthNumber) {
        switch(monthNumber) {
          case 1:
            return 31;
            break;
          case 2:
            return 28;
            break;
          case 3:
            return 31;
            break;
          case 4:
            return 30;
            break;
          case 5:
            return 31;
            break;
          case 6:
            return 30;
            break;
          case 7:
            return 31;
            break;
          case 8:
            return 31;
            break;
          case 9:
            return 30;
            break;
          case 10:
            return 31;
            break;
          case 11:
            return 30;
            break;
          case 12:
            return 31;
            break;
        }
      },
      getListOfMonthsInTimeStamp: function(fromTimestamp, toTimeStamp) {
        var monthList = [];
        while (fromTimestamp <= toTimeStamp) {
          monthList.push(fromTimestamp)
          var monthNumber = new Date(fromTimestamp).getMonth() + 1;
          var noOfDays = this.getDaysInMonth(monthNumber);
          fromTimestamp = fromTimestamp + noOfDays*24*60*60*1000;
        }
        return monthList;
      },
      sortTimeStamp: function(data,option) {
        for(var i = 0; i < data.length; i++){
          for(var j = 0; j < data.length; j++){
           switch(option){
            case 'ASC':
            if(data[i] < data[j]){
              var k = data[i];
              data[i] = data[j];
              data[j] = k;
            }
            case 'DESC':
            if(data[i] > data[j]){
              var k = data[i];
              data[i] = data[j];
              data[j] = k;
            }
          }
        }
      }
      return data;
    },
      getTimeIntervalFromTimeStamp: function(data) {
        var date = new Date(data);
        var hours = this.getDay(date.getHours().toString());
        switch(hours) {
          case '00':
              return 'Midnight - 4 AM';
              break;
          case '01':
              return 'Midnight - 4 AM';
              break;
          case '02':
              return 'Midnight - 4 AM';
              break;
          case '03':
              return 'Midnight - 4 AM';
              break;
          case '04':
              return '4 AM - 8 AM';
              break;
          case '05':
              return '4 AM - 8 AM';
              break;
          case '06':
              return '4 AM - 8 AM';
              break;
          case '07':
              return '4 AM - 8 AM';
              break;
          case '08':
              return '8 AM - 12 PM';
              break;
          case '09':
              return '8 AM - 12 PM';
              break;
          case '10':
              return '8 AM - 12 PM';
              break;
          case '11':
              return '8 AM - 12 PM';
              break;
          case '12':
              return '12 PM - 4 PM';
              break;
          case '13':
              return '12 PM - 4 PM';
              break;
          case '14':
              return '12 PM - 4 PM';
              break;
          case '15':
              return '12 PM - 4 PM';
              break;
          case '16':
              return '4 PM - 8 PM';
              break;
          case '17':
              return '4 PM - 8 PM';
              break;
          case '18':
              return '4 PM - 8 PM';
              break;
          case '19':
              return '4 PM - 8 PM';
              break;
          case '20':
              return '8 PM - Midnight';
              break;
          case '21':
              return '8 PM - Midnight';
              break;
          case '22':
              return '8 PM - Midnight';
              break;
          case '23':
              return '8 PM - Midnight';
              break;
          default:
              break;
        }
      },

      getDays: function(date){
        var oneDay = 24*60*60*1000;
        var currentDate = new Date();
        var diffDays = Math.floor((currentDate.getTime() - date.getTime())/oneDay);
        return diffDays;
      }
    };
  });
