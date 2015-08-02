
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
