# EasyStudy 🎓

**EasyStudy** is an AI-powered academic companion designed for School and College students.

---

## 📱 How to Install the App from GitHub

### Method 1: Install Directly on Your Phone (Easiest)

1. **Push or Upload to GitHub**:
   - In Google AI Studio, click the **Settings / Export** menu in the top corner and choose **Push to GitHub** (or **Download ZIP** and push to your GitHub repo).
   - Alternatively, upload the included `EasyStudy.apk` file directly to your GitHub repository under **Releases**.
2. **Download on Your Phone**:
   - On your Android phone, open Chrome or your browser.
   - Go to your GitHub repository:
     - If using **Releases**: Go to **Releases** on the right side and tap **`EasyStudy.apk`**.
     - If using **Actions**: Go to the **Actions** tab, click the latest build, and download the **`EasyStudy-APK`** artifact.
     - Or download the `EasyStudy.apk` file directly from the repo root.
3. **Install the APK**:
   - Once downloaded, tap on the notification or open your **Files / Downloads** folder.
   - Tap **EasyStudy.apk**.
   - If prompted: **"Install unknown apps"**, toggle **Allow from this source**.
   - Tap **Install** and then **Open**!

---

### Method 2: Build & Install Using Android Studio (Developers)

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/<your-username>/<your-repo-name>.git
   cd <your-repo-name>
   ```

2. **Open in Android Studio**:
   - Open Android Studio.
   - Select **File > Open...** and choose the cloned folder.
   - Wait for Gradle sync to complete.

3. **Install on Your Phone**:
   - Enable **Developer Options** and **USB Debugging** on your Android phone:
     - Go to phone **Settings > About Phone**.
     - Tap **Build Number** 7 times.
     - Go to **Developer Options > USB Debugging** and turn it **ON**.
   - Plug your phone into your computer via USB cable.
   - In Android Studio, select your connected phone from the device dropdown list at the top.
   - Click the green **Run (▶)** button (or press `Shift + F10`).
   - The app will automatically build and install on your phone!

---

### Method 3: Install via Command Line (ADB)

If you already have `adb` installed:
```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
