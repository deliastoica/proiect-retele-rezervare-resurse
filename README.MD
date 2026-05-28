# Aplicatie Distribuita pentru Rezervarea de Resurse (Sockets Java)

Acest proiect reprezinta implementarea temei 04 - Rezervarea de resurse pentru seminarul de Retele de Calculatoare. Aplicatia urmeaza o arhitectura client-server concurenta, utilizand socket-uri TCP native in Java 21 pentru comunicare si sincronizare in timp real.

## Realizator Proiect
* Nume Student: [Numele Tau]
* Grupa: [Grupa Ta]

---

## Tehnologii si Concepte Utilizate
* Limbaj: Java 21 (Eclipse Temurin)
* Arhitectura: Client-Server bazata pe fire de executie (Thread-per-client) pe server si thread dedicat de ascultare (ServerListener) pe client.
* Sincronizare: Metode si blocuri de tip synchronized in managerul de resurse pentru prevenirea situatiilor de cursa (race conditions).
* Containerizare: Docker si Docker Compose pentru portabilitatea instantei de server.
* Formatul Datelor: Standardul ISO YYYY-MM-DDTHH:MM (exemplu: 2026-06-01T14:00).

---

## Instructiuni de Rulare (Docker Compose)

Conform cerintelor din barem, serverul poate fi compilat si pornit intr-un container izolat folosind urmatoarea comanda rulata la radacina proiectului:

docker compose up --build

Dupa pornirea serverului, clientii pot fi lansati de pe masina gazda (local) utilizand urmatoarele comenzi:

# Compilare fisiere client
javac client/*.java

# Pornire client
java client.Client

---

## Ghid de Utilizare si Protocol de Comenzi

Dupa introducerea unui username valid la pornirea clientului, sunt disponibile urmatoarele comenzi text:

1. LIST - Solicita serverului starea curenta a resurselor, afisand rezervarile confirmate si blocajele temporare active.
2. LOCK [resursa] [start] [end] - Solicita blocarea temporara a unei resurse pe un interval orar.
   * Exemplu: LOCK Room1 2026-06-01T14:00 2026-06-01T16:00
3. CONFIRM [resursa] - Transforma blocarea temporara curenta detinuta de utilizator intr-o rezervare oficiala cu ID unic.
4. UNLOCK [resursa] - Anuleaza manual o blocare temporara inainte de confirmare.
5. UPDATE [id_rezervare] [nou_start] [nou_sfarsit] - Modifica intervalul orar al unei rezervari proprii (include validari automate anti-suprapunere).
6. DELETE [id_rezervare] - Sterge definitiv o rezervare din sistem.

---

## Robustete si Tratarea Deconectarilor
Aplicatia include un mecanism de siguranta in clasa ClientHandler executat in blocuri de tip finally. In cazul in care un client se deconecteaza brusc (inchidere terminal, pierdere conexiune), serverul intercepteaza evenimentul asincron, curata toate structurile de tip Lock active asociate acelui utilizator si賈notifica restul retelei, prevenind blocarea resurselor la infinit.

---

## Video Demonstrativ
Link-ul catre prezentarea functionala si explicarea codului sursa (durata maxima 5-7 minute):
[INTRODUCE AICI LINK-UL DE YOUTUBE SAU DRIVE DUPA CE FILMEZI]