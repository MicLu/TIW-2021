/**
 * AJAX call management
 */

function makeCall(method, url, formElement, cback, reset = true) {

    console.log("TEST");
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

function createTableRow(rowData) {
    var row = document.createElement("tr");
    var auc_list_image = document.createElement("td");
    var auc_title = document.createElement("div");
    var auc_owner = document.createElement("div");

    var pricebox = document.createElement("div");
    var auc_price = document.createElement("div");
    var auc_timer = document.createElement("div");

    // Set del prezzo dell'asta
    auc_price.innerHTML = "Offerta corrente: <span id='auction_prezzo_start'>" + rowData.prezzo_start + "</span>&euro;";
    auc_price.classList.add("auc-price");

    // Set della scadenza
    auc_timer.innerHTML = "Scadenza: <span id='auction_scadenza'>" + rowData.scadenza + "</span>";
    auc_timer.classList.add("auc-timer");

    // Build pricebox
    pricebox.classList.add("pricebox");
    pricebox.append(auc_price);
    pricebox.append(auc_timer);

    // Owner
    /*
    auc_owner.innerHTML = rowData.;
    auc_owner.classList.add("auc-owner");*/

    // Title
    auc_title.innerHTML = "<span class='detail-link'><span class ='auction-name' id='auction_titolo" + rowData.idAsta + "'>" + rowData.article.nome + " [" + rowData.article.idArticolo + "]" + "</span></span >";
    auc_title.classList.add("auc-title");

    var second_td = document.createElement("td");
    second_td.append(auc_title);
    second_td.append(auc_owner);
    second_td.append(pricebox);

    // Img
    auc_list_image.classList.add("auc-list-image");

    var img = "<img class='auction-img' id='auction_immagine' src = 'http://localhost:8080/TIW-2021/GetImage?image=" + rowData.article.immagine + "'>";

    auc_list_image.innerHTML = img;


    row.append(auc_list_image);
    row.append(second_td);

    return row;


}

function getImage(path) {
    var self = this;
    makeCall("GET", "GetImage?image=" + path, null,
        function(req) {
            if (req.readyState == XMLHttpRequest.DONE) {
                var message = req.responseText;
                if (req.status == 200) {
                    var image = JSON.parse(req.responseText);

                    return image;
                }
            } else {
                console.log("IMAGE ERROR");
            }
        }
    );
}

function createOfferRow(offer) {

    var row = document.createElement("div");
    row.classList.add("offerta")

    var div1 = document.createElement("div");
    div1.innerHTML = offer.offerenteCompleto; //nome offerente

    var div2 = document.createElement("div");
    div2.style = "text-align: end;";
    div2.innerHTML = offer.valore + "€"; //offerta

    row.append(div1);
    row.append(div2);

    return row;
}

function setCookie(cname, cvalue, exdays) {
    const d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    let expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function checkCookie() {
    let user = getCookie("username");
    if (user != "") {
        alert("Welcome again " + user);
    } else {
        user = prompt("Please enter your name:", "");
        if (user != "" && user != null) {
            setCookie("username", user, 365);
        }
    }
}

function addVisitedAuctionList(id) {
    var cookieList = getCookie("aucList");

    if (cookieList == "") {
        var list = [];
        list.push(id);
        list = JSON.stringify(list);
    } else {
        var list = JSON.parse(cookieList);
        if (!list.includes(id)) {
            list.push(id);
        }
        list = JSON.stringify(list);
    }

    setCookie("aucList", list, 30);


}