[HUSK Å LEGGE TIL MAVEN BADGE HER]
# PGR203 Avansert Java eksamen

## Beskrivelse av programmet
Dette programmet består av en http-server, FlywayDB scripts for opprettelse av db-tabeller, DAO-er for lagring og henting av data fra databasen og kontroller klasser for å utføre api-kall. Den består også av fire html sider:
* index.html - Hovedsiden viser spørsmålene som er lagret i databasen, samt svaralternativer med checkbox-er hvor svarene lagres i databasen når man trykker submit-knappen ved hvert spørsmål. Under spørreskjemaet ligger hyperlinker til de andre html sidene.
---
* newQuestionnaire.html - "Add new questions" fra hovedsiden. Her kan man legge til nye spørsmål i databasen/spørreskjemaet. Hvert spørsmål har en tittel, tekst og to svaralternativer som man skriver i hver sin tekstboks før man trykker submit. Hvis man vil legge til flere svaralternativer kan man legge til disse på addOption.html siden.
---
* addOption.html - "Add new option to question" fra hovedsiden. Her kan man legge til ekstra svaralternativer til et spørsmål. Lagrede spørsmål listes ut med hver sin tekstboks og submit knapp.
---
* editQuestions.html - "Edit question" fra hovedsiden. Her kan man redigere tittel og tekst på spørsmål. Det er to tekstbokser og submit knapper for hvert spørsmål, og man kan bare redigere et felt om gangen.
---

### Ekstra-punkter
* Redirect ved POST request - Vi løste dette ved å lage en metode i HttpMessage som tar inn clientSocket og location/filbane ("/index.html") som parametere, skriver en respons til klienten med 303 statuskode og Location headeren satt til parameterverdien location.
---
* Favorittikon - Siden det er en binærfil så måtte vi ha en ny måte å skrive skrive den ut til nettsiden vår. 
---
* .css håndtering med <!DOCTYPE html> - Vi la til en else if sjekk for .css i handleClient metoden i HttpServer slik at Content-Type headeren blir satt til text/css.
---
* Kan dere lage noen diagrammer som illustrerer hvordan programmet deres virker - Disse ligger i pgr203-exam-willidachili/doc mappen, vi valgte å lage to diagrammer som ikke var helt like som vist i forelesning.
---
* UML diagram som dokumenterer datamodell og/eller arkitektur - Dette er slik vår Database ser ut i programmet. Vi valgte å lage nettsiden med tanke på anonymitet så Answer tabellen skal kun holde på svar som blir gjennomført gjennom index.html. På den måten så trenger den egentlig ingen egen primærnøkkel siden vi tenkte at den skal kun liste ut antall duplikater av option_id. Med en slik spørring: ``` SELECT COUNT(*) FROM Answers LEFT JOIN Option on Answer.option_id = Options.option_id join Questions on Option.question_id = Questions.question_id WHERE Questions.question_id = x GROUP BY Option.option_id``` Dette ville ha resultert i et resultset som forteller hvor mange svar hvert enkelt svarsalternetiv et spørsmål har fått. Answers tabellen har en primærnøkkel nå kun med tanke på potensielt videre bygging av programmet da en vil kunne legge til bruker med cookie. Så man vil da kun trenge å sette reference med FK på response_id til den nye bruker tabellen og flytte Serial Primary key på bruker tabellen på response_id der.
<div align=center>
<img src="https://user-images.githubusercontent.com/71970061/141373174-849c8ec7-78ea-4c2d-9b84-6f560fc6a708.PNG"/>
</div>

---
* Implementere update commando på database via nettsiden - editQuestions.html tar i bruk UPDATE commando for å oppdatere den nye tittelen 
---
* God bruk av DAO-/Controller-pattern - Vi gjorde sam lært i forelesning og så fordelene med det. Så vi antar da at vi fikk brukt det godt og rikitg
---
* Godt forklarende README.md med illustrerende bilder - Vi prøvde å få det tydelig og oversiktig ikke bare for deg, men for vår egendel også.
---
## Beskriv hvordan programmet skal testes:
I rot-katalogen (hvor du har pgr203.properties og target katalog med .jar filen) kjører du denne kommandoen via kommandolinje:
```
java -jar target\pgr203-exam-willidachili-1.0-SNAPSHOT.jar
```

## Korreksjoner av eksamensteksten i Wiseflow:

**DET ER EN FEIL I EKSEMPELKODEN I WISEFLOW:**

* I `addOptions.html` skulle url til `/api/tasks` vært `/api/alternativeAnswers` (eller noe sånt)

**Det er viktig å være klar over at eksempel HTML i eksamensoppgaven kun er til illustrasjon. Eksemplene er ikke tilstrekkelig for å løse alle ekstraoppgavene og kandidatene må endre HTML-en for å være tilpasset sin besvarelse**


## Sjekkliste

## Vedlegg: Sjekkliste for innlevering

* [x] Dere har lest eksamensteksten
* [ ] Dere har lastet opp en ZIP-fil med navn basert på navnet på deres Github repository
* [x] Koden er sjekket inn på github.com/pgr203-2021-repository
* [x] Dere har committed kode med begge prosjektdeltagernes GitHub konto (alternativt: README beskriver arbeidsform)

### README.md

* [ ] `README.md` inneholder en korrekt link til Github Actions
* [x] `README.md` beskriver prosjektets funksjonalitet, hvordan man bygger det og hvordan man kjører det
* [x] `README.md` beskriver eventuell ekstra leveranse utover minimum
* [x] `README.md` inneholder et diagram som viser datamodellen

### Koden

* [x] `mvn package` bygger en executable jar-fil
* [x] Koden inneholder et godt sett med tester
* [x] `java -jar target/...jar` (etter `mvn package`) lar bruker legge til og liste ut data fra databasen via webgrensesnitt
* [x] Serveren leser HTML-filer fra JAR-filen slik at den ikke er avhengig av å kjøre i samme directory som kildekoden
* [x] Programmet leser `dataSource.url`, `dataSource.username` og `dataSource.password` fra `pgr203.properties` for å connecte til databasen
* [x] Programmet bruker Flywaydb for å sette opp databaseskjema
* [x] Server skriver nyttige loggmeldinger, inkludert informasjon om hvilken URL den kjører på ved oppstart

### Funksjonalitet

* [x] Programmet kan opprette spørsmål og lagrer disse i databasen (påkrevd for bestått)
* [x] Programmet kan vise spørsmål (påkrevd for D)
* [x] Programmet kan legge til alternativ for spørsmål (påkrevd for D)
* [x] Programmet kan registrere svar på spørsmål (påkrevd for C)
* [x] Programmet kan endre tittel og tekst på et spørsmål (påkrevd for B)

### Ekstraspørsmål (dere må løse mange/noen av disse for å oppnå A/B)

* [ ] Før en bruker svarer på et spørsmål hadde det vært fint å la brukeren registrere navnet sitt. Klarer dere å implementere dette med cookies? Lag en form med en POST request der serveren sender tilbake Set-Cookie headeren. Browseren vil sende en Cookie header tilbake i alle requester. Bruk denne til å legge inn navnet på brukerens svar
* [x] Når brukeren utfører en POST hadde det vært fint å sende brukeren tilbake til dit de var før. Det krever at man svarer med response code 303 See other og headeren Location
* [ ] Når brukeren skriver inn en tekst på norsk må man passe på å få encoding riktig. Klarer dere å lage en <form> med action=POST og encoding=UTF-8 som fungerer med norske tegn? Klarer dere å få æøå til å fungere i tester som gjør både POST og GET?
* [ ] Å opprette og liste spørsmål hadde vært logisk og REST-fult å gjøre med GET /api/questions og POST /api/questions. Klarer dere å endre måten dere hånderer controllers på slik at en GET og en POST request kan ha samme request target?
* [ ] Vi har sett på hvordan å bruke AbstractDao for å få felles kode for retrieve og list. Kan dere bruke felles kode i AbstractDao for å unngå duplisering av inserts og updates?
* [ ] Dersom noe alvorlig galt skjer vil serveren krasje. Serveren burde i stedet logge dette og returnere en status code 500 til brukeren
* [ ] Dersom brukeren går til http://localhost:8080 får man 404. Serveren burde i stedet returnere innholdet av index.html
* [x] Et favorittikon er et lite ikon som nettleseren viser i tab-vinduer for en webapplikasjon. Kan dere lage et favorittikon for deres server? Tips: ikonet er en binærfil og ikke en tekst og det går derfor ikke an å laste den inn i en StringBuilder
* [ ] I forelesningen har vi sett på å innføre begrepet Controllers for å organisere logikken i serveren. Unntaket fra det som håndteres med controllers er håndtering av filer på disk. Kan dere skrive om HttpServer til å bruke en FileController for å lese filer fra disk?
* [x] Kan dere lage noen diagrammer som illustrerer hvordan programmet deres virker?
* [ ] JDBC koden fra forelesningen har en feil ved retrieve dersom id ikke finnes. Kan dere rette denne?
* [x] I forelesningen fikk vi en rar feil med CSS når vi hadde `<!DOCTYPE html>`. Grunnen til det er feil content-type. Klarer dere å fikse det slik at det fungerer å ha `<!DOCTYPE html>` på starten av alle HTML-filer?
* [ ] Klarer dere å lage en Coverage-rapport med GitHub Actions med Coveralls? (Advarsel: Foreleser har nylig opplevd feil med Coveralls så det er ikke sikkert dere får det til å virke)
* [ ] FARLIG: I løpet av kurset har HttpServer og tester fått funksjonalitet som ikke lenger er nødvendig. Klarer dere å fjerne alt som er overflødig nå uten å også fjerne kode som fortsatt har verdi? (Advarsel: Denne kan trekke ned dersom dere gjør det feil!)
* [x] UML diagram som dokumenterer datamodell og/eller arkitektur (presentert i README.md)

Mulige ekstrapoeng fortalt i forelesninger som vi tokk med:
* [x] Implementere update commando på database via nettsiden. (dato: 6/10)
* [x] God bruk av DAO-pattern
* [x] God bruk av Controller-pattern

Annet:
* [x] Godt forklarende README.md med illustrerende bilde
