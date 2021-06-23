(function() {

    //Componenti pagina
    var asteDisopnibili, mieAsteAperte, mieAsteChiuse, dettaglioAsta, nuovaAsta,
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

    function WelcomeMessage(username, messageContainer) {
        this.username = username;

        this.show = function() {
            messageContainer.textContent = this.username;
        }
    }

    function AsteDisponibili(alertContainer, listContainer, listContainerBody) {

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listcontainer.style.visibility = "hidden";
        }

        this.show = function() {
            var self = this;
            makeCall("GET", "GetAvailableAuctionJS", null,
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
        };

        this.update = function(listaAste) {


            listaAste.array.forEach(element => {
                //codice di creazione della tabella
            });

            //rendo visibile la tabella alla fine della creazione
            this.listContainerBody.style.visibility = "visible";
        }

    }

    function MieAsteAperte(alertContainer, listContainer, listContainerBody) {

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listcontainer.style.visibility = "hidden";
        }

        this.show = function() {
            var self = this;
            makeCall("GET", "GetMyAuctionJS", null,
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
        };

        this.update = function(listaAste) {


            listaAste.array.forEach(element => {
                //codice di creazione della tabella
            });

            //rendo visibile la tabella alla fine della creazione
            this.listContainerBody.style.visibility = "visible";
        }

    }

    //Forse non serve perchè tanto la servlet GetMyAuctionJS restituisce già 
    //due liste di mie aste aperte e mie aste chiuse
    /* function MieAsteChiuse(alertContainer, listContainer, listContainerBody) {

        this.alertContainer = alertContainer;
        this.listContainer = listContainer;
        this.listContainerBody = listContainerBody;

        this.reset = function() {
            //Nascondo l'intera tabella
            this.listcontainer.style.visibility = "hidden";
        }

        this.show = function() {
            var self = this;
            makeCall("GET", "GetAvailableAuctionJS", null,
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
        };

        this.update = function(listaAste) {
            //codice di creazione della tabella

            listaAste.array.forEach(element => {
                
            });

            //rendo visibile la tabella alla fine della creazione
            this.listContainerBody.style.visibility = "visible";
        }

    } */

    function DettaglioAsta() {

        //TODO
        this.reset = function() {
            //Nascondo l'intera tabella
            //this.listcontainer.style.visibility = "hidden";
        }

    }

    function NuovaAsta() {

        //TODO
        this.reset = function() {
            //Nascondo l'intera tabella
            //this.listcontainer.style.visibility = "hidden";
        }

    }

    function PageOrchestrator() {

        var alertContainer = document.getElementById("id_alert");

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

            mieAsteAperte = new MieAsteAperte(
                alertContainer,
                document.getElementById("my-open-auc-list-container"),
                document.getElementById("my-open-auc-list-body")
            );

            //Forse non serve
            mieAsteChiuse = new MieAsteChiuse(
                alertContainer,
                document.getElementById("my-closed-auc-list-container"),
                document.getElementById("my-closed-auc-list-body")
            );

            //TODO
            dettaglioAsta = new DettaglioAsta();
            nuovaAsta = new NuovaAsta();

        }

        //Nasconde tutti i componenti
        this.reset = function() {

            alertContainer.textContent = "";

            asteDisopnibili.reset();
            mieAsteAperte.reset();
            //forse non serve
            mieAsteChiuse.reset();
            dettaglioAsta.reset();
            nuovaAsta.reset();

        }

    }






})();