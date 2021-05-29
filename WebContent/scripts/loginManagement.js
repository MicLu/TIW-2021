/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

    document.getElementById("loginbutton").addEventListener('click', (e) => {

        var form = e.target.closest("form"); //Form con usernam e psw

        if (form.checkValidity()) {
            makeCall("POST", 'LoginJS', e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {
                            case 200:
                                //salva variabile a livello di sessione
                                sessionStorage.setItem('username', message);
                                //Reindirizzo l'utente
                                window.location.href = "homeJS.html";
                                break;
                            case 400: // bad request
                                document.getElementById("errorMsg").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("errorMsg").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("errorMsg").textContent = message;
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });

})();