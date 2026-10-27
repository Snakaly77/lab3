# Laboratorium 3: Tłumaczenie aplikacji

Aplikacja mobilna prezentująca mechanizmy lokalizacji w systemie Android zgodnie z wytycznymi Konspektu 3.

---

## 1. Wybór domyślnego zestawu zasobów (Zadanie 1)

Domyślnym zestawem zasobów tekstowych (umieszczonym w katalogu `res/values/strings.xml` bez żadnego kwalifikatora) jest **język angielski**, natomiast wersja polska znajduje się w katalogu z kwalifikatorem `res/values-pl/strings.xml`.

Katalog `values` pełni w architekturze Androida rolę zestawu awaryjnego (*fallback*). Gdy z aplikacji korzysta użytkownik posługujący się językiem, dla którego brak dedykowanego tłumaczenia (np. niemieckim czy japońskim), system automatycznie wczyta napisy z katalogu domyślnego. Umieszczenie tam języka angielskiego zapewnia czytelność i zrozumiałość interfejsu dla użytkowników międzynarodowych.

### Zrzuty ekranu aplikacji w obu wersjach językowych:

| Wersja polska | Wersja angielska |
| :---: | :---: |
| <img width="300" alt="Wersja polska" src="https://github.com/user-attachments/assets/8f2776f6-e986-4c44-9c81-0a06830883a8" /> | <img width="300" alt="Wersja angielska" src="https://github.com/user-attachments/assets/d7a51409-d172-4ae8-a80e-1e8b820ca9d6" /> |

---

## 2. Refaktoryzacja losowania żartów (Zadanie 2)

* **Ile miejsc w kodzie trzeba zmienić, aby dodać czwarty żart w wersji z osobnymi zasobami?**  
  Wymaga to modyfikacji kodu źródłowego programu: zwiększenia zakresu losowania (np. z 3 na 4) oraz dodania kolejnej gałęzi w strukturze sterującej (`when`/`if`), która przypisuje wylosowaną liczbę do nowego identyfikatora zasobu (`R.string.joke4`).
* **W jaki sposób wylosować jeden z trzech zasobów, skoro każdy ma inną nazwę?**  
  Wymaga to sztucznego tworzenia listy identyfikatorów w kodzie (np. `listOf(R.string.joke1, R.string.joke2, R.string.joke3)`) lub instrukcji mapującej, co niepotrzebnie wiąże kod z warstwą danych.
* **Co się stanie, gdy tłumacz doda w jednym języku piąty żart, a w drugim nie?**  
  Przy osobnych zasobach brak odpowiednika w drugim pliku może prowadzić do błędu kompilacji lub pominięcia żartu.

**Wnioski z wdrożenia `<string-array>`:**  
Zastąpienie pojedynczych zasobów tablicą zasobów (`string-array`) i pobieranie jej w kodzie eliminuje powyższe problemy. Kod aplikacji losuje pozycję na podstawie rozmiaru aktualnie wczytanej tablicy (`jokes.indices.random()`), dzięki czemu rozszerzenie bazy żartów odbywa się wyłącznie w plikach XML zasobów, bez modyfikacji kodu programu.

---

## 3. Licznik i formy liczby mnogiej (Zadanie 3)

### Ograniczenia prostego warunku pojedynczego
W języku angielskim występuje prosty podział binarny: forma pojedyncza dla $n = 1$ (*1 joke*) oraz forma mnoga dla pozostałych wartości (*2 jokes*, *5 jokes*). Zastosowanie warunku `if (n == 1)` w języku polskim jest błędem merytorycznym, ponieważ polszczyzna operuje na nieliniowych regułach gramatycznych (zgodnie ze standardem CLDR):
* **`one`** ($n = 1$): *1 żart*
* **`few`** (liczby z końcówkami 2, 3, 4, poza nastkami 12–14): *2 żarty*, *22 żarty*
* **`many`** (liczby 0, 5–21 oraz pozostałe końcówki): *5 żartów*, *12 żartów*, *25 żartów*
* **`other`** (ułamki dziesiętne): *1,5 żartu*

Wykorzystanie zasobu `<plurals>` oraz funkcji `pluralStringResource` pozwala systemowi Android na automatyczny dobór poprawnej formy fleksyjnej bez warunków logicznych w kodzie.

### Zrzuty ekranu odmiany liczebników:
| Forma „żarty” (np. 2 / 22) | Forma „żartów” (np. 5 / 12) |
| :---: | :---: |
| <img width="456" height="643" alt="Zrzut ekranu 2026-10-27 110151" src="https://github.com/user-attachments/assets/a2531fa2-25bc-4209-8d57-e517330d8a7f" /> | <img width="447" height="657" alt="Zrzut ekranu 2026-10-27 110203" src="https://github.com/user-attachments/assets/8328c5bb-3361-4609-8f79-667e6af4bf62" />|

---

## 4. Zmiana języka z poziomu aplikacji (Zadanie 4)

Zdefiniowano plik `res/xml/locales_config.xml` powiązany z atrybutem `android:localeConfig` w pliku manifestu. Do obsługi przełączania języka wybrano wariant oparty na usłudze systemowej **`LocaleManager`** (`applicationLocales`). 

Wybór ten jest uzasadniony budowaniem aplikacji pod nowoczesne wersje Androida (API 37). API systemowe pozwala na natywną obsługę per-app language bez konieczności wprowadzania zależności do biblioteki `AppCompat` oraz bez potrzeby dziedziczenia aktywności z klasy `AppCompatActivity` zamiast standardowej `ComponentActivity`. Wybór użytkownika jest trwale zapamiętywany przez system operacyjny.

---

## 5. Testowanie układu w pseudojęzyku `en-XA` (Zadanie 5)

W pliku `build.gradle.kts` włączono obsługę pseudojęzyków (`isPseudoLocalesEnabled = true` w bloku `debug`), a interfejs poddano testom pod kątem odporności na sztucznie wydłużone łańcuchy znaków ze znakami diakrytycznymi (`en-XA`).

### Zidentyfikowane problemy i wprowadzone poprawki:
1. **Okrągły przycisk akcji:** Przy stałych, sztywnych wymiarach wydłużony tekst `[Šĥöŵ å ĵöķé one two]` wychodził poza obręb koła. Rozwiązano to przez zmianę ze sztywnego rozmiaru na minimalny elastyczny wymiar (`defaultMinSize(minWidth = 140.dp, minHeight = 140.dp)`), dodanie wewnętrznego odstępu `PaddingValues(16.dp)` oraz wyśrodkowanie tekstu (`TextAlign.Center`).
2. **Przełączniki i układ pionowy:** Wydłużone etykiety filtrów i treści powodowały ryzyko ucinania elementów na mniejszych ekranach. Wprowadzono wyśrodkowanie tekstu oraz przewijanie pionowe całego ekranu (`verticalScroll(rememberScrollState())`).

### Zrzut ekranu w pseudojęzyku `en-XA`:
![Pseudojęzyk en-XA](sciezka_do_obrazka_en_xa.png)
