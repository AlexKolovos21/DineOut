# DineOut - Εφαρμογή Παραγγελιών Φαγητού

## Ομάδα Ανάπτυξης
- Frontend & UI/UX Developer
- Backend & Database Developer
- Location Services & Maps Integration Developer

## Περίληψη
Η εφαρμογή DineOut είναι μια Android εφαρμογή για online παραγγελίες φαγητού. Αναπτύχθηκε ως project για το μάθημα 
"ADVANCED TOPICS IN COMPUTER SCIENCE (CN6008_1)" του Τμήματος Πληροφορικής.

## Λειτουργικότητες
Η εφαρμογή προσφέρει:
- Προβολή εστιατορίων κοντά σου
- Online παραγγελίες φαγητού
- Παρακολούθηση της παραγγελίας σου σε πραγματικό χρόνο
- Πολλαπλοί τρόποι πληρωμής
- Διαχείριση του προφίλ σου

## Τεχνικές Προδιαγραφές

### Απαιτήσεις Συστήματος
- Android Studio (τελευταία έκδοση)
- JDK 17
- Android SDK 34
- Ελάχιστη έκδοση Android: 7.0 (API level 24)

### Τεχνολογίες που χρησιμοποιήθηκαν
- **Frontend & UI**
  - Jetpack Compose για το UI
  - Material Design 3
  - Lottie Animations για animations
  - Accompanist για permissions handling
  - Coil για image loading

- **Backend & Data**
  - Room Database για local storage
  - Kotlin Coroutines για asynchronous operations
  - MVVM Architecture Pattern
  - Hilt για dependency injection
  - DataStore για preferences

- **Location & Maps**
  - Google Maps SDK
  - Google Play Services Location
  - Geocoding API
  - Places API

- **Payment & Security**
  - Stripe SDK για πληρωμές
  - Google Pay integration
  - Secure storage για sensitive data

- **QR & Barcode**
  - ZXing για QR code generation
  - CameraX για scanning
  - ML Kit για barcode detection

- **Testing & Quality**
  - JUnit για unit testing
  - Espresso για UI testing
  - Mockito για mocking
  - Firebase Test Lab

## Οδηγίες Εγκατάστασης

### Προαπαιτούμενα
1. Εγκατάσταση Android Studio
2. Εγκατάσταση JDK 17
3. Ρύθμιση Android SDK

### Βήματα Εγκατάστασης
1. **Κλωνοποίηση του Repository**
   ```bash
   git clone https://github.com/AlexKolovos21/DineOut.git
   ```

2. **Ρύθμιση του Project**
   - Άνοιγμα του Android Studio
   - Επιλογή "Open an existing project"
   - Επιλογή του φακέλου DineOut

3. **Ρύθμιση Gradle**
   - Αναμονή για τον συγχρονισμό του Gradle
   - Ενημέρωση Gradle εάν απαιτείται

4. **Ρύθμιση Google Maps**
   - Δημιουργία project στο Google Cloud Console
   - Ενεργοποίηση Maps SDK για Android
   - Δημιουργία API key
   - Προσθήκη του API key στο `local.properties`

   > **Σημείωση Ασφαλείας**: Το API key είναι ευαίσθητο στοιχείο και πρέπει να διατηρείται ιδιωτικό. Μην το μοιράζεστε ποτέ σε δημόσια repositories ή documentation.

## Δομή του Project
```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/dineout/
│   │   │       ├── data/
│   │   │       ├── ui/
│   │   │       └── utils/
│   │   └── res/
│   └── test/
└── build.gradle
```

## Δοκιμές
Έχουμε ελέγξει την εφαρμογή σε:
- Android 7.0 έως Android 14
- Διάφορες συσκευές (Samsung, Google Pixel, Xiaomi)
- Διαφορετικές συνθήκες δικτύου
- Πολλαπλές ταυτόχρονες παραγγελίες

## Συμπεράσματα
Η ανάπτυξη της εφαρμογής DineOut αποτέλεσε μια σημαντική ευκαιρία για την εφαρμογή των γνώσεων που αποκτήσαμε στο μάθημα.
Μέσω της εργασίας αυτής, καταφέραμε να συνδυάσουμε διάφορες τεχνολογίες και να δημιουργήσουμε μια πλήρη εφαρμογή Android
που ανταποκρίνεται στις σύγχρονες απαιτήσεις της αγοράς.

## Μελλοντικές Βελτιώσεις
- Προσθήκη συστήματος αξιολογήσεων
- Υλοποίηση push notifications
- Ενσωμάτωση συστήματος προσφορών
- Βελτίωση του συστήματος αναζήτησης

## Πηγές
- [Android Developer Documentation](https://developer.android.com)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Google Maps Platform](https://developers.google.com/maps)

## Άδεια Χρήσης
Αυτό το project διανέμεται υπό την άδεια MIT.

---

*© 2024 Ομάδα Ανάπτυξης DineOut - Τμήμα Πληροφορικής* 