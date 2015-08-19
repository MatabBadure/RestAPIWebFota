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
        return new Date().getTime - (1000*60*60*24*n);
      },
      getWeekOfMonth: function(d) {
        var d = new Date(d);
        var date = d.getDate();
        var day = d.getDay();
        var weekOfMonth = Math.ceil((date - 1 - day) / 7);
        return weekOfMonth;
      },
      getDateFromTimeStamp: function(data) {
        var date = new Date(data);
        return this.getDay(date.getDate()) + '/' + this.getMonth(date.getMonth(date)) + '/' + this.getYear(date.getFullYear(date))
      },
      getTimeIntervalFromTimeStamp: function(data) {
        var date = new Date(data);
        var hours = this.getDay(date.getHours().toString());
        switch(hours) {
          case '00':
              return '12 PM - 2 AM';
              break;
          case '01':
              return '12 AM - 2 AM';
              break;
          case '02':
              return '2 AM - 4 AM';
              break;
          case '03':
              return '2 AM - 4 AM';
              break;
          case '04':
              return '4 AM - 6 AM';
              break;
          case '05':
              return '4 AM - 6 AM';
              break;
          case '06':
              return '6 AM - 8 AM';
              break;
          case '07':
              return '6 AM - 8 AM';
              break;
          case '08':
              return '8 AM - 10 AM';
              break;
          case '09':
              return '8 AM - 10 AM';
              break;
          case '10':
              return '10 AM - 12 AM';
              break;
          case '11':
              return '10 AM - 12 AM';
              break;
          case '12':
              return '12 AM - 2 PM';
              break;
          case '13':
              return '12 AM - 2 PM';
              break;
          case '14':
              return '2 PM - 4 PM';
              break;
          case '15':
              return '2 PM - 4 PM';
              break;
          case '16':
              return '4 PM - 6 PM';
              break;
          case '17':
              return '4 PM - 6 PM';
              break;
          case '18':
              return '6 PM - 8 PM';
              break;
          case '19':
              return '6 PM - 8 PM';
              break;
          case '20':
              return '8 PM - 10 PM';
              break;
          case '21':
              return '8 PM - 10 PM';
              break;
          case '22':
              return '10 PM - 12 PM';
              break;
          case '23':
              return '10 PM - 12 PM';
              break;
          default:
              break;
        }
      }
      
    };
  });