
  <h3 align="center">Push Web Android Application</h3>
  
  <p align="center">
    Aplikacja webowa pozwalająca na wysyłanie powiadomień typu push 
    <br />
    <a href="https://push-web-application.herokuapp.com/">Aplikacja webowa</a>
    ·
    <a href="https://sebastiankulig.github.io/PushWebApplication/">Dokumentacja aplikacji webowej</a>
    ·
    <a href="https://github.com/karolwn/push_app">Aplikacja mobilna</a>
     ·
    <a href="https://karolwn.github.io/push_app/">Dokumentacja aplikacji mobilnej</a>

  </p>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Spis treści</summary>
  <ol>
    <li>
      <a href="#o-projekcie">O projekcie</a>
    </li>
    <li>
      <a href="#architektura">Architektura</a>
    </li>
    <li><a href="#użyte-technologie">Użyte technologie</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## O projekcie
Aplikacja pozwala na rejestrowanie użytkowników w usłudze Firebase Cloud Messaging, subskrybowanie na konkretne topici oraz obsługa powiadomień push wysłanych z aplikacji web tj. odbiór oraz odpowiedź.

Ekran startowy aplikacji
<br/>
<img src="images/startup.png">

Ekran logowania. Użytkownik ma opcję logowania poprzez adres email lub jako gość.
<br/>
<img src="images/login.png">

Tworzenie nowego użytkownika <br/>
<img src="images/login_new.png">

Logowanie na istniejące konto.<br/>
<img src="images/login_return.png">

Ekran główny aplikacji.<br/>
<img src="images/home.png">

Ekran z formularzem odpowiedzi.<br/>
<img src="images/response.png">

Ekran z informacjami o aplikacji.<br/>
<img src="images/about.png">

Popup potwierdzający usunięcie konta.<br/>
<img src="images/delete.png">

Odebrane powiadomienie.<br/>
<img src="images/notification.png">

Zaktualizowany ekran odpowiedzi.<br/>
<img src="images/notification2.png">

## Architektura
<img src="images/sequence.svg">

### Użyte technologie
- Java 8
- Firebase Authentication
- Firebase Realtime Database
- Firebase Cloud Messaging
- Android SDK, API 29
