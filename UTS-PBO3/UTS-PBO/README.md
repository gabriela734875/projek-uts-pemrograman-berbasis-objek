# Program Pemesanan Kendaraan

Program interaktif Java untuk memilih data sales dan kendaraan dari file, mengidentifikasi pelanggan lama dari No. KTP atau mendaftarkan pelanggan baru, menentukan transaksi, menghitung diskon, lalu menyimpan SPK ke `data/transaksi.txt`.

## Menjalankan

Jalankan dari folder `App` (folder yang berisi `src` dan `data`):

```text
javac -d bin src/*.java
java -cp bin App
```

Gunakan Java 11 atau lebih baru. Folder kerja saat menjalankan program harus folder `App`, karena program membaca folder `data` relatif terhadap folder tersebut.

## Data TXT

Setiap baris data dipisahkan tanda `|`. Baris kosong dan komentar berawalan `#` dilewati. Jangan gunakan tanda `|` di dalam nilai.

- `data/seller.txt`: `idSeller|nama|alamat|noTelepon|email|jabatan`
- `data/buyer.txt`: `noKTP|nama|pekerjaan|alamatKTP|noTelepon|tanggalLahir|alamatKantor|email`. Data ini menjadi daftar pelanggan yang terus diperbarui setelah transaksi disimpan.
- `data/produk.txt`: `kodeProduk|modelTipe|warna|noMesin|noRangka|harga`
- `data/transaksi.txt`: dibuat otomatis, satu transaksi per baris.
- `data/pembatalan.txt`: dibuat otomatis jika transaksi yang tersimpan kemudian dibatalkan.

Simpan teks berformat UTF-8. Tambahkan data baru dengan menambah baris mengikuti kolom tersebut.

## Konsep OOP

`Person` adalah abstract class karena `Buyer` dan `Seller` memiliki data pribadi dasar yang sama, tetapi cara menampilkan identitasnya berbeda. Method abstrak `tampilkanData()` diwujudkan oleh masing-masing subclass. `Penjualan` tetap class konkret untuk menyimpan dan menghitung transaksi.

Contoh konsep materi pada source:

- Class dan object: `Buyer`, `Seller`, `Produk`, dan `Penjualan` menjadi class; `App` membuat object untuk transaksi.
- Encapsulation: atribut pada class model dibuat `private` dan diakses melalui constructor, getter, serta setter.
- Inheritance: `Buyer` dan `Seller` menggunakan `extends Person` untuk mewarisi data nama, alamat, telepon, dan email.
- Abstract: `Person` tidak dibuat sebagai object langsung. Subclass wajib mengisi cara masing-masing menampilkan data melalui `tampilkanData()`.
- Polymorphism: `App.tampilkanDataOrang(Person orang)` menerima parent `Person`; ketika dikirim object `Seller`, method versi `Seller` berjalan, dan ketika dikirim object `Buyer`, method versi `Buyer` berjalan.
- Interface: `Buyer` dan `Seller` menggunakan `implements Cetak` dan menyediakan method `cetak()`.
- Java library, collection, dan file: `String`, `Scanner`, `LocalDate`, `ArrayList`/`List`, serta `Files`/`Path` digunakan untuk input, daftar data, dan penyimpanan.
- Exception handling: operasi file melempar `IOException` dan ditangani pada `App` dengan `try-catch`.

Pada awal program, pilih pelanggan lama atau pelanggan baru. Pelanggan lama dicari menggunakan No. KTP; data yang sudah tersimpan tidak perlu diketik ulang dan kolom yang masih kosong akan dilengkapi. Pelanggan baru mengisi semua data. Riwayat jumlah transaksi aktif ditampilkan berdasarkan No. KTP, sementara transaksi yang dibatalkan tidak dihitung sebagai pembelian. Data buyer baru atau perubahan profil disimpan saat transaksi disimpan.

Program menawarkan transfer, kartu debit/kredit, kredit melalui bank (contoh: BCA, Bank Mandiri, BRI), atau kredit melalui leasing (contoh: Adira Finance, FIFGROUP, WOM Finance). Pengguna juga dapat mengetik nama bank/leasing lain. Diskon diminta sebagai jawaban ya/tidak; bila ya, pengguna memasukkan persentasenya.

Jika pengguna menjawab tidak pada konfirmasi simpan, transaksi tidak dicatat dan tidak ada pembayaran yang diproses. Jika transaksi sudah disimpan, program bertanya apakah ingin membatalkannya dan meminta penjelasan alasannya. Sesuai ketentuan pada surat, pembatalan sepihak customer mengembalikan 50% booking fee; kredit ditolak atau unit tidak tersedia mengembalikan 100%. Catatan kategori dan penjelasan pembatalan ditambahkan ke `data/pembatalan.txt`, sedangkan catatan transaksi asli tetap ada sebagai riwayat. Perhitungan ini hanya simulasi, bukan proses transfer uang.
