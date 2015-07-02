'use strict';

angular.module('hillromvestApp')
    .factory('PatientVestDeviceRawLog', function ($resource, DateUtils) {
        return $resource('api/patientVestDeviceRawLogs/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.spReceiveTime = DateUtils.convertDateTimeFromServer(data.spReceiveTime);
                    data.hubReceiveTime = DateUtils.convertDateTimeFromServer(data.hubReceiveTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
