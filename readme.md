# Panduan Instalasi dan Penggunaan Proyek Perhitungan Penjualan

Dokumen ini berisi panduan teknis untuk melakukan instalasi lingkungan pengembangan dan cara menjalankan aplikasi Perhitungan Penjualan menggunakan Visual Studio Code (VS Code).

## 1. Instalasi Java Development Kit (JDK)

Aplikasi ini dikembangkan menggunakan Java. Diperlukan Java Development Kit (JDK) versi 23 untuk melakukan kompilasi dan menjalankan program.

1. Unduh installer JDK 23 untuk sistem operasi Windows melalui tautan resmi Oracle berikut:
   [Windows Oracle JDK 23 Download](https://download.oracle.com/java/23/archive/jdk-23.0.2_windows-x64_bin.msi)
2. Jalankan file installer (`.exe`) yang telah diunduh dan ikuti instruksi instalasi standar hingga proses selesai. Direktori instalasi bawaan umumnya berada di `C:\Program Files\Java\jdk-23`.

## 2. Konfigurasi _Environment Variables_

Sistem operasi perlu dikonfigurasi agar dapat mengenali direktori instalasi Java.

1. Buka menu pencarian Windows, ketik `Environment Variables`, lalu pilih opsi **Edit the system environment variables**.
2. Pada jendela _System Properties_ yang terbuka, klik tombol **Environment Variables...** di bagian kanan bawah.
3. Pada bagian **System variables**, klik tombol **New...** untuk menambahkan variabel baru:
   - **Variable name:** `JAVA_HOME`
   - **Variable value:** Masukkan jalur direktori instalasi JDK (contoh: `C:\Program Files\Java\jdk-23`).
   - Klik **OK**.
4. Cari dan pilih variabel bernama `Path` di dalam daftar **System variables**, lalu klik **Edit...**.
5. Klik tombol **New** dan tambahkan baris berikut: `%JAVA_HOME%\bin`
6. Klik **OK** pada seluruh jendela untuk menyimpan pengaturan.

_(Untuk memverifikasi konfigurasi, buka Command Prompt atau PowerShell dan jalankan perintah `java --version`. Konfigurasi berhasil jika sistem menampilkan informasi Java versi 23)._

## 3. Persiapan Visual Studio Code

Visual Studio Code memerlukan ekstensi khusus untuk mendukung pengembangan aplikasi Java.

1. Unduh dan instal [Visual Studio Code](https://code.visualstudio.com/) jika aplikasi belum terpasang pada perangkat Anda.
2. Buka Visual Studio Code.
3. Buka menu _Extensions_ yang terletak di bilah sisi sebelah kiri (atau gunakan pintasan keyboard `Ctrl + Shift + X`).
4. Lakukan pencarian dengan kata kunci **Extension Pack for Java** (pastikan ekstensi tersebut dipublikasikan oleh Microsoft).
5. Klik **Install** pada ekstensi tersebut.

<!-- ## 4. Mengkloning Repositori (Git Clone)

Langkah ini digunakan untuk mengunduh kode sumber proyek langsung dari repositori GitHub menggunakan fitur internal VS Code.

1. Pada Visual Studio Code, buka _Command Palette_ dengan menekan `Ctrl + Shift + P`.
2. Ketik `Git: Clone` dan pilih opsi tersebut.
3. Salin dan tempel URL repositori proyek ini ke dalam kolom yang disediakan (URL: `https://github.com/ifanhaqq/PerhitunganPenjualan`), lalu tekan **Enter**.
4. Jendela _File Explorer_ akan terbuka. Pilih direktori lokal di komputer Anda sebagai tempat penyimpanan folder proyek, lalu klik **Select Repository Location**.
5. Setelah proses pengunduhan selesai, akan muncul notifikasi di sudut kanan bawah layar. Klik **Open** untuk memuat folder proyek.
   _(Jika muncul kotak dialog keamanan terkait kepercayaan terhadap pembuat berkas, pilih **Yes, I trust the authors**)._ -->

## Langkah 4: Membuka dan Menjalankan Aplikasi

Semuanya sudah siap! Mari kita jalankan aplikasinya.

1. Di dalam VS Code, klik menu **File** di sudut kiri atas, lalu pilih **Open Folder...**
2. Cari dan pilih folder proyek ini (folder yang berisi kode Anda), lalu klik **Select Folder**.
   _(Jika muncul peringatan "Do you trust the authors...", klik **Yes, I trust the authors**)._
3. **Tunggu sejenak:** Perhatikan pojok kanan bawah layar VS Code. VS Code sedang membaca proyek Anda dan mengunduh kebutuhan file secara otomatis (biasanya ada ikon _loading_ atau tulisan _Syncing project_). Tunggu sampai muncul tanda jempol (👍) atau tulisan _Ready_.
4. **Buka file utama:**
   - Di panel sebelah kiri, buka folder `src` ➔ `main` ➔ `java` ➔ `com` ➔ `penjualan`.
   - Klik file bernama **`App.java`**.
5. **Jalankan Aplikasi:**
   - Di dalam kode file `App.java`, cari baris kode yang bertuliskan `public static void main(String[] args)`.
   - Tepat di atas baris tersebut, akan muncul tulisan kecil yang bisa diklik berbunyi **Run | Debug**.
   - Klik **Run**.

## 5. Menjalankan Aplikasi

Setelah folder berhasil, aplikasi dapat langsung dijalankan.

1. Perhatikan pop up status di sudut kanan bawah antarmuka VS Code. Tunggu proses sinkronisasi proyek dan pengunduhan dependensi Maven selesai secara otomatis (ditandai dengan indikator sinkronisasi yang berubah menjadi ikon siap/selesai).
2. Buka panel _Explorer_ di sidebar sisi kiri, kemudian navigasikan ke struktur folder berikut:
   `src` ➔ `main` ➔ `java` ➔ `com` ➔ `penjualan`
3. Pilih dan buka berkas **`App.java`**.
4. Temukan deklarasi metode utama di dalam kode tersebut, yaitu:
   `public static void main(String[] args)`
5. Tepat di atas deklarasi metode tersebut, akan muncul perintah teks kecil yang dapat diklik. Klik opsi **Run**.
6. Terminal bawaan akan terbuka untuk memproses kompilasi kode, dan antarmuka grafis (GUI) aplikasi Perhitungan Penjualan akan segera tampil di layar.
