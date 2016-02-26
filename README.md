# Einführung

Diese Übung soll eine Vertiefung des Wissens für mobile Anwendungen darstellen.

## Ziele

Das Ziel dieser Übung ist die Entwicklung eines serverbasierten Gruppenchats. Die Applikation soll auf den Entwicklungen der letzten beiden Übungen aufbauen und diese um folgende Funktionalität erweitern:

- Anbindung mit Hilfe eines RESTful Webservice
- Zur Teilnahme beim Gruppenchat ist eine Anmeldung erforderlich (Username/Passwort)
- Abmeldung des Users am Ende der Chatsession
- Verwendung des Observer-Pattern

## Voraussetzungen

- Grundlagen Java und XML
- Grundlegendes Verständnis über Entwicklungs- und Simulationsumgebungen
- Verständnis von RESTful Webservices
- Grundlegendes Wissen zum Design Pattern "Observer" und dessen Umsetzung

## Aufgabenstellung

Es ist eine mobile Anwendung zu implementieren, die sich mit Hilfe der Übung DezSysLabor-11 "Mobile Access to Web Services" bei einem Gruppenchat anmeldet. Nach erfolgreicher Anmeldung bekommt der Benutzer alle Meldungen, die in diesem Chat eingehen. Der Benutzer hat ebenso die Moeglichkeit Nachrichten in diesem Chat zu erstellen. Diese Nachricht wird in weiterer Folge an alle Teilnehmer versendet und in der mobilen Anwendung angezeigt. Am Ende muss sich der Benutzer vom Gruppenchat abmelden.

Es ist freigestellt, welche mobile Implementierungsumgebung dafür gewählt wird. Empfohlen wird aber eine Implementierung auf Android

# Quellen

"Android Restful Webservice Tutorial – How to call RESTful webservice in Android – Part 3"; Posted By Android Guru on May 27, 2014; online: http://programmerguru.com/android-tutorial/android-restful-webservice-tutorial-how-to-call-restful-webservice-in-android-part-3/

"Referenzimplementierung von DezSys09"; Paul Kalauner; online: https://github.com/pkalauner-tgm/dezsys09-java-webservices

Bewertung: 16 Punkte
- Anmeldung/Abmeldung Gruppenchat (2 Punkte)
- Empfangen von Nachrichten des Gruppenchats (3 Punkte)
- Erstellen von Nachrichten im Gruppenchat (3 Punkte)
- Option den Chat zu wechseln (2 Punkte)
- Korrekte Umsetzung des Observer Pattern (2 Punkte)
- Simulation bzw. Deployment auf mobilem Gerät (2 Punkte)
- Protokoll (2 Punkte)
