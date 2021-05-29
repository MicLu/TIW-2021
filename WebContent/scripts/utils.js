/**
 * AJAX call management
 */

function makeCall(method, url, formElement, cback, reset = true) {

    var req = new XMLHttpRequest(); // visible by closure

    req.onreadystatechange = function() {
        cback(req)
    }; // closure

    req.open(method, url);

    //Dipende se GET o POST
    if (formElement == null) {
        req.send();
    } else {
        req.send(new FormData(formElement));
    }

    if (formElement !== null && reset === true) {
        formElement.reset();
    }
}