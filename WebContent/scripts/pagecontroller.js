(function() { // avoid variables ending up in the global scope

    var title = document.getElementById("current-selection-title");

    var openauc = document.getElementById("my-open-auc-list");
    var closedauc = document.getElementById("my-closed-auc-list");
    var allauc = document.getElementById("av-auc-list");

    // Le mie aste
    document.getElementById("my-auc-btn").addEventListener('click', (e) => {

        // display my open and closed auction
        title.innerHTML = "Le mie aste";

        // Make js call

        openauc.classList.remove("hidden-auc-list");
        closedauc.classList.remove("hidden-auc-list");
        allauc.classList.add("hidden-auc-list");

        
    });
    


})();

    function isLogged()
	{
		var username = sessionStorage.getItem('username');
		if (username == null || username == "") alert("No login");
		else alert("logged");
	}