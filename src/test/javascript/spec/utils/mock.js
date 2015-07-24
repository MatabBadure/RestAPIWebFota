var token = 'token',
  timeStamp = '1420070400000',
  loginURL = 'api/authenticate?cacheBuster=' + timeStamp,
  accountDetailsURL = 'api/account?cacheBuster=' + timeStamp,
  resetPassInitURL = 'api/account/reset_password/init?cacheBuster=' + timeStamp,
  recaptchaURL = 'api/recaptcha?cacheBuster=' + timeStamp,
  securityQuestionsURL = 'api/securityQuestions?cacheBuster=' + timeStamp,
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
    },
    'securityQuestions': {
      'success': {
        [{
          "id": 3,
          "question": "In which city was your mother born"
        }, {
          "id": 5,
          "question": "What is first name of your oldest cousin?"
        }, {
          "id": 4,
          "question": "What was your kindergarten teacher's last name?"
        }, {
          "id": 1,
          "question": "Your favorite pet's name"
        }, {
          "id": 2,
          "question": "Your mother's maiden name"
        }]
      }
    }
  }
};
