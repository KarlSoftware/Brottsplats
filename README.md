# Brottsplats
###Projekt för Datavetenskap: Storskaliga webbtjänster (DA358A)

Android appen fungerar endast tillsammans med tillhörande backend,
- [Brottsplats](https://github.com/JimmyMaksymiw/Brottsplats)
- [BrottsplatsBackend](https://github.com/JimmyMaksymiw/BrottsplatsBackend)

### Syfte
Syfte med denna applikation är att kunna se på en karta var olika brott och olyckor har skett i Sverige.
Istället för att läsa Polisens RSS-flöde med text kan man med denna tjänst se på en karta var det har hänt något.
Om du exempelvis bor i Malmö och vill veta var det hänt brott eller olyckor när dig så väljer du 
skåne län och zoomar in över malmö och appen kommer då visa dig markörer som du kan klicka på för att 
få mer information om vad som hänt och vid vilken tid. 

### Tekninsk lösning
#### Backend
API:et är skrivet i java och använder sig av ramverket Spark.
Vi har som sagt valt att använda oss utav Polisens RSS-flöde för att hämta händelser i Sverige. Härifrån tas 'title', 'description' och 'link' ut. Sedan plockas staden ut från 'title' och addressen/platsen från 'description' och används sedan för att hämta de exakta koordinaterna för just denna plats. Stad och plats skickas till Google Maps Geocoding API och svaret vi får tillbaka är de geografiska koordinaterna.
Informationen sätts ihop till ett JSON-objekt som visas nedanför. 
```
{
  "link": "http://polisen.se/Aktuellt/Handelser/Skane/2015-10-30-1255-Trafikolycka-Malmo/",
  "description": "Två personbilar i kollision, Annetorpsvägen.",
  "title": "2015-10-30 12:55, Trafikolycka, Malmö",
  "Location": {
    "lng": 12.9551285,
    "lat": 55.5681543
  }
}
```
API:et används genom att anropa dessa URI:
* /events
  * Används för att få information om brott och olyckor i Hela Sverige.
  * Returnerar ett JSON-Objekt med status och en array med JSON-objekt som exemplet ovan.
* /events/:area
  * Används för att få information om brott och olyckor i ett specifikt län Sverige.
  * Returnerar ett JSON-Objekt med status och en array med JSON-objekt som exemplet ovan.
  * Exempel:
    * /events/skane - returnerar händelser i Skåne Län.
    * /events/stockholm - returnerar händelser i Stockholms Län.
* /counties
  * Används för att få information om de olilka länen i Sverige. 
  * Returnerar ett JSON-Objekt med status och en array med JSON-objekt som innehåller länets namn, en URI(för att hämta händelser), och koordinater med länets gränser för att kunna visa hela länet på en karta.

#### Frontend



### Hur kör jag projektet?
Ladda hem både app och backend:
##### [BrottsplatsBackend](https://github.com/JimmyMaksymiw/BrottsplatsBackend)
1. Importera paketet BrottsplatsBackend i din IDE, förslagsvis IntelliJ.
2. Kontrollera att spark är importerat.
3. Starta programmet genom StartServer.java

##### [Brottsplats](https://github.com/JimmyMaksymiw/Brottsplats)
1. Importera paketet Brottsplats i din IDE, förslagsvis Android Studio.
2. Starta en Android emulator som kör minst API Level: 15 och minst har Google Play services 8.
3. Starta programmet genom MainActivity.java

### Användarmanual
