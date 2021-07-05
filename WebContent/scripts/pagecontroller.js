(function() {

    //Componenti pagina
    var asteDisponibili, asteVinte, asteVisualizzate, mieAsteAperte, mieAsteChiuse, dettaglioAsta, nuovaAsta, alertContainer,
        pageOrchestrator = new PageOrchestrator();


    window.addEventListener("load", () => {
        //TODO: contrllo dei cookie


        if (sessionStorage.getItem("username") == null) {
            window.location.href = "indexJS.html";
        } else {
            //inizializza i componenti
            var user = getCookie("username");

            pageOrchestrator.start();
            pageOrchestrator.reset();

            if (user != sessionStorage.getItem("username") || user == "") {
                // Non siamo noi o non siamo collegati
                console.log("Prima volta");
                // Salvo il mio utente nei cookie
                setCookie("username", sessionStorage.getItem("username"), 30);

                asteDisponibili.show();
                asteVinte.show();
            } else {

                var action = getCookie("action");
                console.log("action: " + action);

                if (action == "") {
                    pageOrchestrator.reset();
                    asteDisponibili.show();
                    asteVinte.show();

                } else if (action == 0) {
                    // Apro la lista di aste visitate
                    console.log("Moastro le aste disponibili");
                    pageOrchestrator.reset();
                    asteVisualizzate.show(getCookie("aucList"));
                    asteDisponibili.show();
                    asteVinte.show();

                } else if (action == 1) {
                    console.log("mostro le mie aste");
                    // Apro le mie aste
                    document.getElementById("my-auc-btn").click();
                    asteDisponibili.reset();
                    asteVinte.reset();
                }
            }

        }
    }, false);

    var logo = document.getElementById("site-logo");
    logo.addEventListener('mouseover', (e) => {
        e.target.style.cursor = "pointer";
    });
    logo.addEventListener("click", () => {
        pageOrchestrator.reset();
        asteDisponibili.show();
        document.getElementById("page-title").innerHTML = "<h1>Aste disponibili</h1>";
    });

    var btn_vendo = document.getElementById("btn-vendo");
    btn_vendo.addEventListener('mouseover', (e) => {
        e.target.style.cursor = "pointer";
    });
    btn_vendo.addEventListener("click", () => {
        pageOrchestrator.reset();
        nuovaAsta.show();
    });

    var btn_my_auc = document.getElementById("my-auc-btn");
    btn_my_auc.addEventListener('mouseover', (e) => {
        e.target.style.cursor = "pointer";
    });
    btn_my_auc.addEventListener("click", () => {
        pageOrchestrator.reset();
        mieAsteAperte.show();
        mieAsteChiuse.show();
    });

    var btn_new_auc = document.getElementById("new-auc-btn");
    btn_new_auc.addEventListener("click", () => {
        var form = document.getElementById("new-auc-form");

        if (form.checkValidity()) {
            makeCall("POST", "CreateAuctionJS", form,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {
                            case 200:
                                pageOrchestrator.reset();
                                setCookie("action", 1, 30);
                                mieAsteAperte.show();
                                break;
                            case 400: // bad request
                                document.getElementById("id_alert").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("id_alert").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("id_alert").textContent = message;
                                break;
                        }
                    } else {
                        form.reportValidity();
                    }
                });
        }
    });

    //var btn_searc = document.getElementById("search-btn");
    var btn_searc = document.getElementById("search-btn");
    btn_searc.addEventListener("click", () => {
        var search_form = document.getElementById("searchbar");

        makeCall("GET", "SearchJS?keywordSearch=" + search_form.value, null,
            function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        var listaAste = JSON.parse(req.responseText);
                        console.log(listaAste);
                        if (listaAste.length == 0) {
                            console.log("Lista vuota");
                            alertContainer.textContent = "Nessuna asta trovata";
                            asteDisponibili.reset();
                            return;
                        }
                        asteDisponibili.update(listaAste);
                    }
                } else {
                    alertContainer.textContent = message;
                }
            }
        );
    });



    function WelcomeMessage(username, messageContainer) {
        this.username = username;

        this.show = function() {
            messageContainer.textContent = this.username;
        }
    }

    function AsteDisponibili(alertContainer, listContainer, listContainerBody) {

        console.log("ASTEDISP OK");

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listContainer.style.display = "none";
        }

        this.show = function(listId = null) {
            var self = this;

            makeCall("GET", "GetAvailableAuctionJS?list=" + encodeURIComponent(listId), null,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var listaAste = JSON.parse(req.responseText);
                            console.log(listaAste);
                            if (listaAste.length == 0) {
                                console.log("Lista vuota");
                                self.alertContainer.textContent = "Nessuna asta disponibile";
                                return;
                            }
                            self.update(listaAste);
                        }
                    } else {
                        self.alertContainer.textContent = message;
                    }
                }
            );
        };

        this.update = function(listaAste) {

            console.log(listaAste);

            listContainerBody.innerHTML = "";
            listaAste.forEach(element => {

                var row = createTableRow(element);
                this.listContainerBody.append(row);

                var id = "auction_titolo" + element.idAsta;
                //var link_dettalio_asta = document.getElementById(id);

                var link_dettalio_asta = document.getElementsByClassName(id);

                for (let i = 0; i < link_dettalio_asta.length; i++) {

                    link_dettalio_asta[i].addEventListener('mouseover', (e) => {
                        e.target.style.cursor = "pointer";
                    });

                    link_dettalio_asta[i].addEventListener("click", () => {
                        pageOrchestrator.reset();
                        dettaglioAsta.show(element.idAsta);
                    });
                }


            });

            document.getElementById("page-title").innerHTML = "<h1>Aste disponibili</h1>";
            //rendo visibile la tabella alla fine della creazione
            this.listContainer.style.display = "block";
        }

    }

    function AsteVinte(alertContainer, listContainer, listContainerBody) {

        console.log("ASTEVINTE OK");

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listContainer.style.display = "none";
        }

        this.show = function(listId = null) {
            var self = this;

            makeCall("GET", "GetAvailableAuctionJS?list=win", null,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var listaAste = JSON.parse(req.responseText);
                            console.log(listaAste);
                            if (listaAste.length == 0) {
                                console.log("Lista vuota");
                                self.alertContainer.textContent = "Nessuna asta disponibile";
                                return;
                            }
                            self.update(listaAste);
                        }
                    } else {
                        self.alertContainer.textContent = message;
                    }
                }
            );
        };

        this.update = function(listaAste) {

            console.log(listaAste);

            listContainerBody.innerHTML = "";
            listaAste.forEach(element => {

                var row = createTableRow(element);
                this.listContainerBody.append(row);

                var id = "auction_titolo" + element.idAsta;
                //var link_dettalio_asta = document.getElementById(id);

                var link_dettalio_asta = document.getElementsByClassName(id);

                for (let i = 0; i < link_dettalio_asta.length; i++) {

                    link_dettalio_asta[i].addEventListener('mouseover', (e) => {
                        e.target.style.cursor = "pointer";
                    });

                    link_dettalio_asta[i].addEventListener("click", () => {
                        pageOrchestrator.reset();
                        dettaglioAsta.show(element.idAsta);
                    });
                }


            });

            document.getElementById("page-title").innerHTML = "<h1>Aste disponibili</h1>";
            //rendo visibile la tabella alla fine della creazione
            this.listContainer.style.display = "block";
        }

    }

    function MieAsteAperte(alertContainer, listContainer, listContainerBody) {

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listContainer.style.display = "none";
        }

        this.show = function() {
            var self = this;
            console.log("MIEASTEAPERTE SHOW")
            makeCall("GET", "GetMyAuctionJS", null,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var listaAste = JSON.parse(req.responseText);
                            //Aste aperte
                            listaAste = listaAste[0];
                            if (listaAste.length == 0) {
                                console.log("NON CI SONO MIE ASTE APERTE");
                                self.alertContainer.textContent = "Nessuna asta disponibile";
                                return;
                            }
                            self.update(listaAste);
                        }
                    } else {
                        console.log("ERROR in MIE ASTE APERTE");
                        self.alertContainer.textContent = message;
                    }
                }
            );
        };

        this.update = function(listaAste) {

            console.log("MIE ASTE APERTE " + listaAste);
            listContainerBody.innerHTML = "";
            listaAste.forEach(element => {

                //creazione della tabella
                var row = createTableRow(element);
                this.listContainerBody.append(row);

                if (element.auctionStatus == "SCADUTA") {
                    row.lastChild.lastChild.lastChild.lastChild.classList.add("errorMessage");
                }

                var id = "auction_titolo" + element.idAsta;
                var link_dettalio_asta = document.getElementById(id);

                link_dettalio_asta.addEventListener('mouseover', (e) => {
                    e.target.style.cursor = "pointer";
                });

                link_dettalio_asta.addEventListener("click", () => {
                    pageOrchestrator.reset();
                    dettaglioAsta.show(element.idAsta);
                });

            });

            document.getElementById("page-title").innerHTML = "<h1>Le mie aste</h1>";
            //rendo visibile la tabella alla fine della creazione
            this.listContainer.style.display = "block";
        }

    }

    function MieAsteChiuse(alertContainer, listContainer, listContainerBody) {

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listContainer.style.display = "none";
        }

        this.show = function() {
            var self = this;
            makeCall("GET", "GetMyAuctionJS", null,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var listaAste = JSON.parse(req.responseText);
                            //Aste chiuse
                            listaAste = listaAste[1];
                            if (listaAste.length == 0) {
                                self.alertContainer.textContent = "Nessuna asta disponibile";
                                return;
                            }
                            self.update(listaAste);
                        }
                    } else {
                        self.alertContainer.textContent = message;
                    }
                }
            );
        };

        this.update = function(listaAste) {

            console.log("MIE ASTE CHIUSUE " + listaAste);
            listContainerBody.innerHTML = "";
            listaAste.forEach(element => {
                //codice di creazione della tabella
                var row = createTableRow(element);
                this.listContainerBody.append(row);

                var id = "auction_titolo" + element.idAsta;
                var link_dettalio_asta = document.getElementById(id);

                link_dettalio_asta.addEventListener('mouseover', (e) => {
                    e.target.style.cursor = "pointer";
                });

                link_dettalio_asta.addEventListener("click", () => {
                    pageOrchestrator.reset();
                    dettaglioAsta.show(element.idAsta);
                });

            });

            //rendo visibile la tabella alla fine della creazione
            this.listContainer.style.display = "block";
        }

    }

    function DettaglioAsta(alertContainer, detContainer) {

        this.alertContainer = alertContainer;
        this.detContainer = detContainer;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.detContainer.style.display = "none";
        }

        this.show = function(id) {
            var self = this;
            makeCall("GET", "GetArticleDetailsJS?auctionId=" + id, null,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asta = JSON.parse(req.responseText);
                            if (asta.length == 0) {
                                self.alertContainer.textContent = "Nessuna asta disponibile";
                                return;
                            }
                            self.update(asta);
                        }
                    } else {
                        self.alertContainer.textContent = message;
                    }
                }
            );
        }

        this.update = function(product) {
            console.log(product[0]);

            var auction = product[0];
            var offerList = product[2];
            var min_offer = product[5];

            // Aggiungo alla lista cookie
            setCookie("action", 0, 30);
            if (sessionStorage.getItem("username") != auction.proprietario) {
                addVisitedAuctionList(auction.idAsta);
            }

            console.log("ASTA " + auction);

            document.getElementById("prod-name").innerHTML = auction.article.nome;
            document.getElementById("auc-own-name").innerHTML = auction.proprietario;
            document.getElementById("auc-exp").innerHTML = auction.scadenza;
            document.getElementById("auc-desc").innerHTML = auction.article.descrizione;
            document.getElementById("auc-curr").innerHTML = auction.prezzo_start;
            document.getElementById("min-offer").innerHTML = min_offer;

            document.getElementById("close-auction-id").value = auction.idAsta;

            document.getElementById("close-auc-btn").addEventListener("click", (e) => {
                var asta = document.getElementById("close-auction-form");
                makeCall("POST", "CloseAuctionJS", asta,
                    function(req) {
                        if (req.readyState == XMLHttpRequest.DONE) {
                            var message = req.responseText;
                            if (req.status == 200) {
                                /*var listaAste = JSON.parse(req.responseText);
                                console.log(listaAste);
                                if (listaAste.length == 0) {
                                    console.log("Lista vuota");
                                    alertContainer.textContent = "Nessuna asta disponibile";
                                    return;
                                }
                                
                                self.update(listaAste);*/
                                pageOrchestrator.reset();
                                dettaglioAsta.show(auction.idAsta);
                            }
                            if (req.status = 400) {
                                console.log("Errore 400");
                            }
                        } else {
                            alertContainer.textContent = message;
                        }
                    }
                );
            })

            var imgReqUrl = "http://localhost:8080/TIW-2021/GetImage?image=" + auction.article.immagine;

            var img = document.createElement("img");
            img.src = imgReqUrl;
            img.classList.add("product-image");

            document.getElementById("prod-img").innerHTML = "";
            document.getElementById("prod-img").append(img);

            var btn_offer = document.getElementById("offer-btn");
            btn_offer.addEventListener("click", () => {

                var form = document.getElementById("makeoffer");

                makeCall("POST", "MakeOfferJS", form,
                    function(req) {

                        if (req.readyState == XMLHttpRequest.DONE) {
                            var message = req.responseText;
                            switch (req.status) {
                                case 200:
                                    pageOrchestrator.reset();
                                    dettaglioAsta.show(auction.idAsta);
                                    break;
                                case 400: // bad request
                                    document.getElementById("id_alert").textContent = message;
                                    break;
                                case 401: // unauthorized
                                    document.getElementById("id_alert").textContent = message;
                                    break;
                                case 500: // server error
                                    document.getElementById("id_alert").textContent = message;
                                    break;
                            }
                        } else {
                            form.reportValidity();
                        }

                    }
                );

            });

            document.getElementById("asta-offer-asta").value = auction.idAsta;
            document.getElementById("asta-offer-min").value = auction.rialzo_min;
            document.getElementById("asta-offer-offerente").value = sessionStorage.getItem("username");
            document.getElementById("asta-offer-corrente").value = auction.prezzo_start

            //Box offerte
            var offerBox = document.getElementById("box-offerte");
            offerBox.innerHTML = "";
            offerList.forEach(element => {

                var riga_offerta = createOfferRow(element);
                offerBox.append(riga_offerta);

            });

            document.getElementById("no-winner").innerHTML = "";

            if (auction.proprietario !== sessionStorage.getItem("username")) {
                document.getElementById("owner-zone").style.display = "none";
            } else {
                document.getElementById("owner-zone").style.display = "block";
                if (auction.auctionStatus === 'SCADUTA') {
                    document.getElementById("close-auc-btn").style.display = "block";
                } else {
                    document.getElementById("close-auc-btn").style.display = "none";
                }
            }
            if (auction.auctionStatus !== 'CHIUSA') {
                document.getElementById("auc-result").style.display = "none";
            }

            if (auction.auctionStatus === 'CHIUSA') {
                if (offerList == undefined || offerList.length == 0 || offerList == null) {
                    //Non ci sono offerte
                    document.getElementById("auc-result").style.display = "none";
                    document.getElementById("no-winner").innerHTML = "Asta scaduta senza offerte";
                } else {
                    document.getElementById("auc-result").style.display = "block";
                    var winner = product[6];
                    console.log("Winner: " + winner);
                    document.getElementById("auc-winner-name").innerHTML = winner.nome + " " + winner.cognome;

                    document.getElementById("auc-final-price").innerHTML = auction.prezzo_start;
                    document.getElementById("auc-winner-address").innerHTML = winner.indirizzo;
                }
            }


            detContainer.style.display = "block";
        }



    }

    function NuovaAsta(formContainer) {

        this.formContainer = formContainer
            //TODO
        this.reset = function() {
            //Nascondo l'intera tabella
            this.formContainer.style.display = "none";
        }

        this.show = function() {

            document.getElementById("page-title").innerHTML = "<h1>Nuova asta</h1>";
            this.formContainer.style.display = "block";

        }

    }

    function PageOrchestrator() {

        alertContainer = document.getElementById("id_alert");

        this.test = function() {
            alert("FUNZIONA");
        }

        //Crea tutti i componenti
        this.start = function() {
            welcomeMessage = new WelcomeMessage(
                sessionStorage.getItem("username"),
                document.getElementById("id_username")
            );
            welcomeMessage.show();

            asteDisponibili = new AsteDisponibili(
                alertContainer,
                document.getElementById("av-auc-list-container"),
                document.getElementById("av-auc-list-body")
            );
            //asteDisponibili.show();

            asteVinte = new AsteVinte(
                alertContainer,
                document.getElementById("aste-vinte"),
                document.getElementById("winned-auc-list-body")
            );

            asteVisualizzate = new AsteDisponibili(
                alertContainer,
                document.getElementById("aste-visualizzate"),
                document.getElementById("visited-auc-list-body")
            );

            mieAsteAperte = new MieAsteAperte(
                alertContainer,
                document.getElementById("mie-aste"),
                document.getElementById("my-open-auc-list-body")
            );

            //mieAsteAperte.show();

            //Forse non serve
            mieAsteChiuse = new MieAsteChiuse(
                alertContainer,
                document.getElementById("mie-aste"),
                document.getElementById("my-closed-auc-list-body")
            );

            //TODO
            dettaglioAsta = new DettaglioAsta(
                alertContainer,
                document.getElementById("dettaglio")
            );
            nuovaAsta = new NuovaAsta(
                document.getElementById("form-new-auc")
            );

            //nuovaAsta.show();

        }

        //Nasconde tutti i componenti
        this.reset = function() {
            console.log("PO RESET");
            alertContainer.textContent = "";

            asteDisponibili.reset();
            asteVisualizzate.reset();
            mieAsteAperte.reset();
            mieAsteChiuse.reset();
            dettaglioAsta.reset();
            nuovaAsta.reset();
            asteVinte.reset();

        }

    }






})();