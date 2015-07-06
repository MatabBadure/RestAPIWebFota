'use strict';

angular.module('hillromvestApp')
    .factory('PatientInfo', function ($resource, DateUtils) {
        return $resource('api/patientInfos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.dob = DateUtils.convertLocaleDateFromServer(data.dob);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.dob = DateUtils.convertLocaleDateToServer(data.dob);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.dob = DateUtils.convertLocaleDateToServer(data.dob);
                    return angular.toJson(data);
                }
            }
        });
    });
