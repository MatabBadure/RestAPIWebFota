var usersList = [
  {
    'id': 67,
    'title' : 'Mr.',
    'firstName' : 'John',
    'lastName' : 'Ceena',
    'middleName' : 'MiddleName',
    'email' : 'syedmohammed+222@neevtech.com',
    'role' : 'ADMIN'
  }, {
    'title' : 'Mr.',
    'firstName' : 'James',
    'lastName' : 'Williams',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'ACCT_SERVICES', 'value' : 'Account Service'}
  },
  {
    'hillromId': 'HR000028',
    'title': 'Mr.',
    'firstName': 'Peter',
    'middleName': 'Smith',
    'lastName': 'Parker',
    'gender': 'male',
    'langKey': 'en',
    'zipcode': '560009',
    'city': 'Bangalore',
    'dob': '08/06/1992',
    'role': 'PATIENT'
  }
];


var clinicsList = [
  {
    "id": 14,
    "name": "Hill Rom",
    "address": "Neev",
    "zipcode": 56004,
    "city": "Bangalore",
    "phoneNumber": 9740353872,
    "faxNumber": 9942354883,
    "hillromId": 123,
    "state": "AL",
    "parent": true,
    "npiNumber": null,
    "deleted": false,
    "childClinics": [
      {
        "id": 65
      },
      {
        "id": 66
      }
    ]
  },
  {
    "id": 14,
    "name": "Neev Rom",
    "address": "Neev",
    "zipcode": 56004,
    "city": "Bangalore",
    "phoneNumber": 9740353872,
    "faxNumber": 9942354883,
    "hillromId": 123,
    "state": "AL",
    "parent": false,

    "npiNumber": null,
    "deleted": false,
    "parentClinic": {"name": "Hill Rom", "id": 13}
  }];

var doctorsList = [
  { 'firstName' : 'Johny', 'lastName' : 'Dep', 'name' : 'Johny Dep', 'email' : 'JohnyDep@gmail.com', 'hospital' : 'Appolo hospital'},
  { 'name' : 'James williams', 'email' : 'JamesWilliams@gmail.com', 'hospital' : 'Manipal hospital'},
  { 'name' : 'David Jones', 'email' : 'davijones@gmail.com', 'hospital' : 'abc hospital'},
  { 'name' : 'William Davis', 'email' : 'williamdavis@gmail.com', 'hospital' : 'mno hospital'},
  { 'name' : 'Joseph taylor', 'email' : 'josephtaylor@gmail.com', 'hospital' : 'xyz hospital'},
  { 'name' : 'David Jones', 'email' : 'davijones@gmail.com', 'hospital' : 'abc hospital'},
  { 'id' : 28, 'name' : 'DoctorName', 'email' : 'syedmohammed+122@neevtech.com', 'hospital' : 'Appolo hospital'},
  { 'id': 66, 'title': 'Dr', 'firstName': 'Syed', 'middleName': 'Mohammad', 'lastName': 'Ali', 'email': 'syedmohammed+004@neevtech.com', 'gender': null, 'zipcode': null, 'activated': false, 'langKey': 'en', 'resetKey': null, 'resetDate': null, 'termsConditionAccepted' : false, 'termsConditionAcceptedDate': null, 'deleted': false, 'lastLoggedInAt': null, 'dob': null, 'speciality': null, 'credentials': null, 'primaryPhone' : 1234567890, 'mobilePhone' : 1234567890, 'faxNumber': 1234567890, 'address': 'Address', 'city': 'Bangalore', 'state': 'AK', 'npiNumber': null
  }
];

var roleEnum = {
  ADMIN : 'ADMIN',
  PATIENT : 'PATIENT',
  HCP : 'HCP',
  ACCT_SERVICES : 'ACCT_SERVICES',
  ASSOCIATES : 'ASSOCIATES',
  HILLROM_ADMIN : 'HILLROM_ADMIN',
  CLINIC_ADMIN : 'CLINIC_ADMIN',
  ANONYMOUS : 'ANONYMOUS'
};
