var usersList = [
  {
    'title' : 'Mr.',
    'firstName' : 'John',
    'lastName' : 'Smith',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'James',
    'lastName' : 'Williams',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'ACCT_SERVICES', 'value' : 'Account Service'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'David',
    'lastName' : 'Jones',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'ASSOCIATES', 'value' : 'Associates'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'CLINIC_ADMIN', 'value' : 'Clinic Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'Joseph',
    'lastName' : 'Taylor',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'ACCT_SERVICES', 'value' : 'Acct Services'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }, {
    'title' : 'Mr.',
    'firstName' : 'William',
    'lastName' : 'Davis',
    'middleName' : 'MiddleName',
    'email' : 'email',
    'role' : {'key' : 'SUPER_ADMIN', 'value' : 'Super Admin'}
  }];


var clinicsList = [
  {
    "id": 13,
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
        "id": 65,
        "name": "abc"
      },
      {
        "id": 66,
        "name": "xyz"
      },
      {
        "id": 67,
        "name": "pqrs"
      },
      {
        "id": 68,
        "name": "qwerty"
      }
    ]
  },
  {
    "id": 69,
    "name": "Neevtech",
    "parent": "Razorfish",
    "address": "RazorFish",
    "zipcode": 560048,
    "city": "bangalore",
    "state": "karnataka",
    "phoneNumber": 9740932492,
    "faxNumber": null,
    "hillromId": null,
    "parentClinic": null,
    "npiNumber": null,
    "users": [],
    "patients": [],
    "deleted": false,
    "childClinics": [
      {
        "id": 70,
        "name": "12345678"
      }
    ]
  }];

var doctorsList = [
  { 'firstName' : 'Johny', 'lastName' : 'Dep', 'name' : 'Johny Dep', 'email' : 'JohnyDep@gmail.com', 'hospital' : 'Appolo hospital'},
  { 'name' : 'James williams', 'email' : 'JamesWilliams@gmail.com', 'hospital' : 'Manipal hospital'},
  { 'name' : 'David Jones', 'email' : 'davijones@gmail.com', 'hospital' : 'abc hospital'},
  { 'name' : 'William Davis', 'email' : 'williamdavis@gmail.com', 'hospital' : 'mno hospital'},
  { 'name' : 'Joseph taylor', 'email' : 'josephtaylor@gmail.com', 'hospital' : 'xyz hospital'},
  { 'name' : 'David Jones', 'email' : 'davijones@gmail.com', 'hospital' : 'abc hospital'}
];

