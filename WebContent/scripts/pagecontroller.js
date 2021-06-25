(function() {

    //Componenti pagina
    var asteDisponibili, mieAsteAperte, mieAsteChiuse, dettaglioAsta, nuovaAsta,
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
    btn_vendo.addEventListener("click", () => {
        pageOrchestrator.reset();
        nuovaAsta.show();
    });

    var btn_my_auc = document.getElementById("my-auc-btn");
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
    })

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
                //codice di creazione della tabella
                var row = createTableRow(element);
                this.listContainerBody.append(row);
            });

            document.getElementById("page-title").innerHTML = "<h1>Le mie aste</h1>";
            //rendo visibile la tabella alla fine della creazione
            this.listContainer.style.display = "block";
        }

    }

    //Forse non serve perchè tanto la servlet GetMyAuctionJS restituisce già 
    //due liste di mie aste aperte e mie aste chiuse
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
            });

            //rendo visibile la tabella alla fine della creazione
            this.listContainer.style.display = "block";
        }

    }

    function DettaglioAsta(detContainer) {

        this.detContainer = detContainer;
        //TODO
        this.reset = function() {
            //Nascondo l'intera tabella
            this.detContainer.style.display = "none";
        }

        this.show = function(id) {
            var self = this;
            makeCall("GET", "GetArticleDetailsJS?id=" + id, null,
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var listaAste = JSON.parse(req.responseText);
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
        }

        this.update = function(product) {
            console.log(product);
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

        var alertContainer = document.getElementById("id_alert");

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