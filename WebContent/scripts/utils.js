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

function createTableRow(rowData)
{
    var row = document.createElement("tr");
    var auc_list_image = document.createElement("td");
    var auc_title = document.createElement("div");
    var auc_owner = document.createElement("div");

    var pricebox = document.createElement("div");
    var auc_price = document.createElement("div");
    var auc_timer = document.createElement("div");

    // Set del prezzo dell'asta
    auc_price.innerHTML = "Offerta corrente: <span id='auction_prezzo_start'>"+rowData.prezzo_start+"</span>&euro;";
    auc_price.classList.add("auc-price");

    // Set della scadenza
    auc_timer.innerHTML = "Scadenza: <span id='auction_scadenza'>"+rowData.scadenza+"</span>";
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
    auc_title.innerHTML = "<a class='detail-link'><span class ='auction-name' id='auction_titolo'>" + rowData.article.nome + "</span></a>";
    auc_title.classList.add("auc-title");

    var second_td = document.createElement("td");
    second_td.append(auc_title);
    second_td.append(auc_owner);
    second_td.append(pricebox);

    // Img
    auc_list_image.classList.add("auc-list-image");

    var img = "<img class='auction-img' id='auction_immagine' src = ''>";

    auc_list_image.innerHTML = img;


    row.append(auc_list_image);
    row.append(second_td);

    return row;


}

/*
<td class="auc-list-image">
                            <img class="auction-img" id="auction_immagine">
                        </td>
<td>
    <div class="auc-title"><a class="detail-link"><span class ="auction-name" id="auction_titolo"></span></a></div>
    <div class="auc-owner" id="auction_proprietario"></div>
    <div class="pricebox">
        <div class="auc-price">Offerta corrente: <span id="auction_prezzo_start"></span>&euro;</div>
        <div class="auc-timer">Scadenza: <span id="auction_scadenza"></span></div>
    </div>
</td>*/