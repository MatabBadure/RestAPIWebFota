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
      }
    };
  });