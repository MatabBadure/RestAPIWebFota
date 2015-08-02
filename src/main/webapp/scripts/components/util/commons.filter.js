
angular.module('hillromvestApp')
.filter("languageFromKey", [function() {
   return function(input) {
    if(input === "en"){
    	return "English";
    }else if(input === "fr"){
    	return "French";
    }
  };
}]);
angular.module('hillromvestApp')
.filter("patientStatus", [function() {
   return function(input) {
    if(input === true){
    	return "Inactive";
    }else {
    	return "Active";
    }
  };
}]);