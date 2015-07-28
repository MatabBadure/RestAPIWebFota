'use strict';
/* jshint unused:false */

/**
 * @ngdoc overview
 * @name hillromvestApp
 * @description
 *
 * Main configuration of the application.
 */

angular.module('hillromvestApp').value('globalConfig', {
  locale: 'en-US',
  siteKey: '6LcXjQkTAAAAAMZ7kb5v9YZ8vrYKFJmDcg2oE-SH',
  passRegEx: '/^(?=^.{8,}$)(?=.*\\d)(?=.*[!@#$%^&*]+)(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$/'
});