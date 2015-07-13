var token = 'token',
  timeStamp = '1420070400000',
  loginURL = 'api/authenticate?cacheBuster=' + timeStamp,
  accountDetailsURL = 'api/account?cacheBuster=' + timeStamp,
  responses = {
    'authenticate': {
      'success': {
        "id": "<ID>",
        "createdTime": "2015-07-13T10: 13: 39 Z ",
        "user ": {
          "id": "ID",
          "title": "TITLE",
          "firstName ": "Administrator",
          "middleName ": null,
          "lastName ": "Administrator",
          "email": "test@everest.com",
          "activated": true,
          "langKey": "en",
          "resetKey": null,
          "resetDate": null,
          "lastLoggedInAt": null
        }
      }
    },
    'accountDetails': {
      'success': {

      }
    }
  };
