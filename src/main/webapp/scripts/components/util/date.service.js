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
      getWeekOfMonth: function(d) {
        var d = new Date(d);
        var date = d.getDate();
        var day = d.getDay();
        var weekOfMonth = Math.ceil((date - 1 - day) / 7) === -0 ? 0 : Math.ceil((date - 1 - day) / 7);
        return weekOfMonth;
      },
      getDateFromTimeStamp: function(data) {
        var date = new Date(data);
        return this.getDay(date.getDate()) + '/' + this.getMonth(date.getMonth(date)) + '/' + this.getYear(date.getFullYear(date))
      },
      getDateDiffIndays: function(fromTimeStamp,toTimeStamp) {
        return Math.floor((toTimeStamp - fromTimeStamp)/(1000*60*60*24));
      },
      getTimeStampForTimeSlot: function(date,timeSlot) {
        return this.getStartTimeStampOfDay(new Date(date).getTime()) + (((timeSlot*4)-1)*60*60*1000);
      },
      getStartTimeStampOfDay: function(timeStamp) {
        return timeStamp - (5.5*60*60*1000);
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
        console.log(diffDays);
        return diffDays;
      }
      
    };
  });
