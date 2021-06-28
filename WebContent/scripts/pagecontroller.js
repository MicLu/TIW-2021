(function() {

    //Componenti pagina
    var asteDisponibili, mieAsteAperte, mieAsteChiuse, dettaglioAsta, nuovaAsta, alertContainer,
        pageOrchestrator = new PageOrchestrator();


    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "indexJS.html";
        } else {
            //inizializza i componenti
            pageOrchestrator.start();
            pageOrchestrator.reset();
        }
    }, false);

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
                                mieAsteAperte.show();
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

        this.show = function() {
            var self = this;
            makeCall("GET", "GetAvailableAuctionJS", null,
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
                var link_dettalio_asta = document.getElementById(id);

                link_dettalio_asta.addEventListener('mouseover', (e) => {
                    e.target.style.cursor = "pointer";
                });

                link_dettalio_asta.addEventListener("click", () => {
                    pageOrchestrator.reset();
                    dettaglioAsta.show(element.idAsta);
                });

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

            document.getElementById("prod-name").innerHTML = auction.article.nome;
            document.getElementById("auc-own-name").innerHTML = auction.proprietario;
            document.getElementById("auc-exp").innerHTML = auction.scadenza;
            document.getElementById("auc-desc").innerHTML = auction.article.descrizione;
            document.getElementById("auc-curr").innerHTML = auction.prezzo_start;
            document.getElementById("min-offer").innerHTML = min_offer;

            //document.getElementById("auc-btn").href = "/CloseAuctionJS?auctionId=" + auction.idAsta;
            document.getElementById("close-auc-btn").addEventListener("click", (e) => {
                makeCall("POST", "CloseAuctionJS", auction.idAsta,
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
            })

            var imgReqUrl = "http://localhost:8080/TIW-2021/GetImage?image=" + auction.article.immagine;

            var img = document.createElement("img");
            img.src = imgReqUrl;
            img.classList.add("product-image");
            document.getElementById("prod-img").append(img);


            if (!(auction.proprietario === sessionStorage.getItem("username") &&
                    auction.auctionStatus === 'SCADUTA')) {
                document.getElementById("owner-zone").style.display = "none";
            }
            if (auction.auctionStatus !== 'CHIUSA') {
                document.getElementById("auc-result").style.display = "none";
            }

            if (auction.auctionStatus === 'CHIUSA') {
                if (offerList == undefined) {
                    //Non ci sono offerte
                    document.getElementById("auc-result").innerHTML = "Asta scaduta senza offerte";
                } else {
                    var winner = product[6];
                    document.getElementById("auc-winner-name").innerHTML = winner.nome + " " + winner.cognome;;

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
            asteDisponibili.show();

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
            mieAsteAperte.reset();
            mieAsteChiuse.reset();
            dettaglioAsta.reset();
            nuovaAsta.reset();

        }

    }






})();