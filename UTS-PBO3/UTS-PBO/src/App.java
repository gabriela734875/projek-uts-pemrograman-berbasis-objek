import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Scanner;
import java.util.function.Function;

public class App {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final Path DATA_DIR = tentukanFolderData();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void main(String[] args) {
        System.out.println("=== Program Pemesanan Kendaraan ===");
        try {
            // Load data dari file teks
            List<Seller> sellers = muatSeller(DATA_DIR.resolve("seller.txt"));
            List<Produk> products = muatProduk(DATA_DIR.resolve("produk.txt"));
            List<Buyer> buyers = muatBuyer(DATA_DIR.resolve("buyer.txt"));

            // Pilih sales person, buyer, dan produk
            Seller seller = pilih("Sales person", sellers, s -> s.getIdSeller() + " - " + s.getNama());
            Buyer buyer = inputBuyer(buyers);
            System.out.println("\nData sales:");
            tampilkanDataOrang(seller);
            System.out.println("\nData pemesan:");
            tampilkanDataOrang(buyer);
            Produk product = pilih("Kendaraan", products,
                    p -> p.getKodeProduk() + " - " + p.getModelTipe() + " / " + p.getWarna()
                            + " (Rp " + rupiah(p.getHarga()) + ")");

            // Input jumlah unit, metode pembayaran, diskon, dan booking fee
            int jumlah = bacaInt("Jumlah unit: ", 1, 100);
            String metode = pilihMetode();
            String leasing = metode.startsWith("Kredit") ? pilihPenyediaKredit(metode) : "-";

            // Input diskon jika ada
            boolean pakaiDiskon = bacaYaTidak("Apakah customer memiliki/mengajukan diskon? (ya/tidak): ");
            double diskonPersen = 0;
            if (pakaiDiskon) {
                diskonPersen = bacaDouble("Persentase diskon (0-100): ", 0, 100);
            }

            // Input booking fee jika ada
            long bookingFee = bacaLong("Booking fee (Rp, isi 0 jika belum dibayar): ", 0, Long.MAX_VALUE);
            String tanggal = LocalDate.now().format(DATE_FORMAT);
            String noSpk = "SPK-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + String.format("%03d", (int) (Math.random() * 1000));

            // Buat objek Penjualan dan cetak SPK
            Penjualan transaksi = new Penjualan(noSpk, tanggal, jumlah, diskonPersen,
                    leasing, metode, bookingFee, product.getHarga());
            cetakSpk(seller, buyer, product, transaksi);

            // Simpan transaksi dan data buyer jika diinginkan termasuk opsi pembatalan
            if (bacaYaTidak("Simpan transaksi ini? (ya/tidak): ")) {
                transaksi.simpanPenjualan(DATA_DIR.resolve("transaksi.txt"), buyer, product);
                simpanAtauPerbaruiBuyer(DATA_DIR.resolve("buyer.txt"), buyers, buyer);
                System.out.println("Transaksi tersimpan di " + DATA_DIR.resolve("transaksi.txt"));
                System.out.println("Data pelanggan tersimpan di " + DATA_DIR.resolve("buyer.txt"));
                if (bacaYaTidak("Batalkan transaksi yang sudah disimpan? (ya/tidak): ")) {
                    String kategoriAlasan = pilihAlasanPembatalan();
                    String alasan = kategoriAlasan + " - " + bacaTeks("Jelaskan alasan pembatalan: ");
                    double persenKembali = kategoriAlasan.equals("Pembatalan sepihak oleh customer") ? 50.0 : 100.0;
                    long pengembalian = transaksi.hitungPengembalianBookingFee(persenKembali);
                    transaksi.catatPembatalan(DATA_DIR.resolve("pembatalan.txt"), buyer, alasan, pengembalian);
                    System.out.println("Transaksi " + noSpk + " dicatat sebagai batal.");
                    System.out.println("Estimasi booking fee yang dikembalikan: Rp " + rupiah(pengembalian));
                    System.out.println("Catatan pembatalan tersimpan di " + DATA_DIR.resolve("pembatalan.txt"));
                } else {
                    System.out.println("Transaksi tetap aktif.");
                }
            } else {
                System.out.println("Transaksi tidak disimpan dan dianggap batal. Tidak ada pembayaran yang dicatat.");
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Terjadi kesalahan saat memproses transaksi: " + e.getMessage());
            System.out.println("Pastikan dijalankan dari folder proyek yang berisi folder data.");
        }
    }

    // Menentukan folder data yang berisi file seller.txt, bisa di folder data langsung atau di dalam folder data
    private static Path tentukanFolderData() {
        Path folderLangsung = Path.of("data");
        if (Files.isRegularFile(folderLangsung.resolve("seller.txt"))) return folderLangsung;

        Path folderDalamProject = Path.of("UTS-PBO", "data");
        if (Files.isRegularFile(folderDalamProject.resolve("seller.txt"))) return folderDalamProject;

        return folderLangsung;
    }

    // Memuat data seller dari file teks
    private static List<Seller> muatSeller(Path file) throws IOException {
        List<Seller> hasil = new ArrayList<>();
        for (String[] row : bacaBaris(file, 6)) {
            hasil.add(new Seller(row[1], row[2], row[3], row[4], row[0], row[5]));
        }
        return hasil;
    }

    // Memuat data buyer dari file teks
    private static List<Buyer> muatBuyer(Path file) throws IOException {
        List<Buyer> hasil = new ArrayList<>();
        if (!Files.exists(file)) return hasil;
        for (String[] row : bacaBaris(file, 8, true)) {
            hasil.add(new Buyer(row[1], row[3], row[4], row[7], row[0], row[2], row[5], row[6]));
        }
        return hasil;
    }

    // Memasukkan data buyer, baik pelanggan lama maupun baru
    private static Buyer inputBuyer(List<Buyer> buyers) throws IOException {
        System.out.println("\nJenis pelanggan:");
        System.out.println("1. Pelanggan lama (cari dengan No. KTP)");
        System.out.println("2. Pelanggan baru (isi data lengkap)");
        int jenis = bacaInt("Pilih jenis pelanggan: ", 1, 2);
        if (jenis == 2) return inputBuyerBaru();

        while (true) {
            String noKTP = bacaTeks("Masukkan No. KTP pelanggan lama: ");
            Buyer buyer = cariBuyer(buyers, noKTP);
            if (buyer == null) {
                System.out.println("No. KTP belum terdaftar.");
                int tindakan = bacaInt("1. Coba cari lagi  2. Daftarkan sebagai pelanggan baru: ", 1, 2);
                if (tindakan == 1) continue;
                return inputBuyerBaru(noKTP);
            }

            System.out.println("Pelanggan lama ditemukan: " + buyer.getNama());
            System.out.println("Riwayat transaksi tersimpan: " + hitungRiwayat(buyer.getNoKTP()) + " kali.");
            lengkapiDataBuyer(buyer);
            return buyer;
        }
    }

    private static Buyer inputBuyerBaru() {
        return inputBuyerBaru(bacaTeks("No. KTP: "));
    }

    private static Buyer inputBuyerBaru(String noKTP) {
        System.out.println("\nInput data lengkap pelanggan baru:");
        String nama = bacaTeks("Nama lengkap: ");
        String pekerjaan = bacaTeks("Pekerjaan: ");
        String alamat = bacaTeks("Alamat sesuai KTP: ");
        String noTelepon = bacaTeks("No. telepon: ");
        String tanggalLahir = bacaTeks("Tanggal lahir: ");
        String alamatKantor = bacaTeks("Alamat kantor: ");
        String email = bacaTeks("Email: ");
        return new Buyer(nama, alamat, noTelepon, email, noKTP, pekerjaan, tanggalLahir, alamatKantor);
    }

    private static void lengkapiDataBuyer(Buyer buyer) {
        if (buyer.getPekerjaan().isEmpty()) buyer.setPekerjaan(bacaTeks("Pekerjaan: "));
        if (buyer.getAlamat().isEmpty()) buyer.setAlamat(bacaTeks("Alamat sesuai KTP: "));
        if (buyer.getNoTelpn().isEmpty()) buyer.setNoTelpn(bacaTeks("No. telepon: "));
        if (buyer.getTanggalLahir().isEmpty()) buyer.setTanggalLahir(bacaTeks("Tanggal lahir: "));
        if (buyer.getAlamatKantor().isEmpty()) buyer.setAlamatKantor(bacaTeks("Alamat kantor: "));
        if (buyer.getEmail().isEmpty()) buyer.setEmail(bacaTeks("Email: "));
    }

    private static Buyer cariBuyer(List<Buyer> buyers, String noKTP) {
        for (Buyer buyer : buyers) {
            if (buyer.getNoKTP().equalsIgnoreCase(noKTP)) return buyer;
        }
        return null;
    }

    private static long hitungRiwayat(String noKTP) throws IOException {
        Path transaksiFile = DATA_DIR.resolve("transaksi.txt");
        if (!Files.exists(transaksiFile)) return 0;
        Set<String> spkBatal = new HashSet<>();
        Path pembatalanFile = DATA_DIR.resolve("pembatalan.txt");
        if (Files.exists(pembatalanFile)) {
            for (String[] row : bacaBaris(pembatalanFile, 7, true)) spkBatal.add(row[0]);
        }
        long jumlah = 0;
        for (String[] row : bacaBaris(transaksiFile, 11, true)) {
            if (row[2].equalsIgnoreCase(noKTP) && !spkBatal.contains(row[0])) jumlah++;
        }
        return jumlah;
    }

    private static void simpanAtauPerbaruiBuyer(Path file, List<Buyer> buyers, Buyer buyer) throws IOException {
        Buyer terdaftar = cariBuyer(buyers, buyer.getNoKTP());
        String[] row = {buyer.getNoKTP(), buyer.getNama(), buyer.getPekerjaan(), buyer.getAlamat(),
                buyer.getNoTelpn(), buyer.getTanggalLahir(), buyer.getAlamatKantor(), buyer.getEmail()};
        for (int i = 0; i < row.length; i++) row[i] = row[i].replace('|', ' ');
        String barisBaru = String.join("|", row);
        if (terdaftar == null) {
            Files.write(file, (barisBaru + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            buyers.add(buyer);
            return;
        }

        // Perbarui data buyer yang sudah terdaftar
        List<String> lines = Files.exists(file) ? Files.readAllLines(file, StandardCharsets.UTF_8) : new ArrayList<>();
        boolean ditemukan = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] kolom = lines.get(i).split("\\|", -1);
            if (kolom.length == 8 && kolom[0].trim().equalsIgnoreCase(buyer.getNoKTP())) {
                lines.set(i, barisBaru);
                ditemukan = true;
                break;
            }
        }
        if (!ditemukan) lines.add(barisBaru);
        Files.write(file, lines, StandardCharsets.UTF_8);
        if (terdaftar != buyer) {
            terdaftar.setNama(buyer.getNama());
            terdaftar.setPekerjaan(buyer.getPekerjaan());
            terdaftar.setAlamat(buyer.getAlamat());
            terdaftar.setNoTelpn(buyer.getNoTelpn());
            terdaftar.setTanggalLahir(buyer.getTanggalLahir());
            terdaftar.setAlamatKantor(buyer.getAlamatKantor());
            terdaftar.setEmail(buyer.getEmail());
        }
    }

    // Parameter bertipe parent Person; implementasi yang dipanggil mengikuti object sebenarnya.
    private static void tampilkanDataOrang(Person orang) {
        orang.tampilkanData();
    }

    // Memuat data produk dari file teks
    private static List<Produk> muatProduk(Path file) throws IOException {
        List<Produk> hasil = new ArrayList<>();
        for (String[] row : bacaBaris(file, 6)) {
            hasil.add(new Produk(row[0], row[1], row[2], row[3], row[4], Long.parseLong(row[5])));
        }
        return hasil;
    }

    // Membaca baris dari file teks, mengabaikan baris kosong dan komentar, serta memvalidasi jumlah kolom
    private static List<String[]> bacaBaris(Path file, int jumlahKolom) throws IOException {
        return bacaBaris(file, jumlahKolom, false);
    }

    private static List<String[]> bacaBaris(Path file, int jumlahKolom, boolean bolehKosong) throws IOException {
        List<String[]> hasil = new ArrayList<>();
        int nomorBaris = 0;
        for (String baris : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            nomorBaris++;
            if (baris.trim().isEmpty() || baris.trim().startsWith("#")) continue;
            String[] kolom = baris.split("\\|", -1);
            if (kolom.length != jumlahKolom) {
                throw new IOException(file + ": format salah pada baris " + nomorBaris
                        + " (harus ada " + jumlahKolom + " kolom dipisahkan tanda |).");
            }
            for (int i = 0; i < kolom.length; i++) kolom[i] = kolom[i].trim();
            hasil.add(kolom);
        }
        if (hasil.isEmpty() && !bolehKosong) throw new IOException("Tidak ada data di " + file);
        return hasil;
    }

    // Fungsi generik untuk memilih item dari daftar pilihan dengan label yang ditentukan
    private static <T> T pilih(String judul, List<T> pilihan, Function<T, String> label) {
        System.out.println("\n" + judul + ":");
        for (int i = 0; i < pilihan.size(); i++) {
            System.out.println((i + 1) + ". " + label.apply(pilihan.get(i)));
        }
        int nomor = bacaInt("Pilih nomor: ", 1, pilihan.size());
        return pilihan.get(nomor - 1);
    }

    private static String pilihMetode() {
        System.out.println("\nMetode pembayaran (sesuai surat, non tunai):");
        System.out.println("1. Transfer");
        System.out.println("2. Kartu debit/kredit");
        System.out.println("3. Kredit melalui bank (contoh: BCA, Bank Mandiri, BRI)");
        System.out.println("4. Kredit melalui leasing (contoh: Adira Finance, FIFGROUP, WOM Finance)");
        switch (bacaInt("Pilih metode: ", 1, 4)) {
            case 1: return "Transfer";
            case 2: return "Kartu debit/kredit";
            case 3: return "Kredit Bank";
            default: return "Kredit Leasing";
        }
    }

    private static String pilihPenyediaKredit(String metode) {
        if (metode.equals("Kredit Bank")) {
            System.out.println("\nContoh bank: 1. BCA  2. Bank Mandiri  3. BRI  4. Lainnya");
            return "Bank: " + bacaPilihanPenyedia(4, "Nama bank lainnya: ",
                    new String[] {"BCA", "Bank Mandiri", "BRI"});
        }
        System.out.println("\nContoh leasing: 1. Adira Finance  2. FIFGROUP  3. WOM Finance  4. Lainnya");
        return "Leasing: " + bacaPilihanPenyedia(4, "Nama leasing lainnya: ",
                new String[] {"Adira Finance", "FIFGROUP", "WOM Finance"});
    }

    private static String bacaPilihanPenyedia(int pilihanLainnya, String promptLainnya, String[] contoh) {
        int nomor = bacaInt("Pilih penyedia: ", 1, pilihanLainnya);
        return nomor == pilihanLainnya ? bacaTeks(promptLainnya) : contoh[nomor - 1];
    }

    private static String pilihAlasanPembatalan() {
        System.out.println("\nAlasan pembatalan:");
        System.out.println("1. Customer membatalkan sepihak (booking fee kembali 50%)");
        System.out.println("2. Kredit ditolak leasing atau unit tidak tersedia (booking fee kembali 100%)");
        return bacaInt("Pilih alasan: ", 1, 2) == 1
                ? "Pembatalan sepihak oleh customer"
                : "Kredit ditolak atau unit tidak tersedia";
    }

    private static void cetakSpk(Seller seller, Buyer buyer, Produk produk, Penjualan penjualan) {
        System.out.println("\n================ SURAT PEMESANAN KENDARAAN ================");
        System.out.println("No. SPK          : " + penjualan.getNoSPK());
        System.out.println("Tanggal          : " + penjualan.getTanggal());
        System.out.println("Sales person     : " + seller.getNama() + " (" + seller.getIdSeller() + ")");
        System.out.println("Nama pemesan     : " + buyer.getNama());
        System.out.println("Pekerjaan        : " + buyer.getPekerjaan());
        System.out.println("No. KTP          : " + buyer.getNoKTP());
        System.out.println("Alamat           : " + buyer.getAlamat());
        System.out.println("Telepon / email  : " + buyer.getNoTelpn() + " / " + buyer.getEmail());
        System.out.println("Jumlah unit      : " + penjualan.getJumlahUnit());
        System.out.println("Model / warna    : " + produk.getModelTipe() + " / " + produk.getWarna());
        System.out.println("No. mesin/rangka : " + produk.getNoMesin() + " / " + produk.getNoRangka());
        System.out.println("Harga per unit   : Rp " + rupiah(produk.getHarga()));
        System.out.println("Diskon           : " + penjualan.getDiscount() + "%");
        System.out.println("Total setelah diskon: Rp " + rupiah(penjualan.hitungTotal()));
        System.out.println("Metode           : " + penjualan.getMetodePembayaran());
        if (penjualan.getMetodePembayaran().startsWith("Kredit")) {
            System.out.println("Penyedia kredit  : " + penjualan.getLeasing());
        }
        System.out.println("Booking fee      : Rp " + rupiah(penjualan.getBookingFee()));
        System.out.println("Sisa pembayaran : Rp " + rupiah(Math.max(0, penjualan.hitungTotal() - penjualan.getBookingFee())));
        System.out.println("============================================================");
    }

    private static String bacaTeks(String prompt) {
        while (true) {
            System.out.print(prompt);
            String nilai = INPUT.nextLine().trim();
            if (!nilai.isEmpty()) return nilai;
            System.out.println("Isian tidak boleh kosong.");
        }
    }

    private static int bacaInt(String prompt, int min, int max) {
        while (true) {
            try {
                int nilai = Integer.parseInt(bacaTeks(prompt));
                if (nilai >= min && nilai <= max) return nilai;
            } catch (NumberFormatException ignored) { }
            System.out.println("Masukkan angka " + min + " sampai " + max + ".");
        }
    }

    private static long bacaLong(String prompt, long min, long max) {
        while (true) {
            try {
                long nilai = Long.parseLong(bacaTeks(prompt));
                if (nilai >= min && nilai <= max) return nilai;
            } catch (NumberFormatException ignored) { }
            System.out.println("Masukkan nilai rupiah yang valid (minimal " + min + ").");
        }
    }

    private static double bacaDouble(String prompt, double min, double max) {
        while (true) {
            try {
                double nilai = Double.parseDouble(bacaTeks(prompt).replace(',', '.'));
                if (Double.isFinite(nilai) && nilai >= min && nilai <= max) return nilai;
            } catch (NumberFormatException ignored) { }
            System.out.println("Masukkan persentase " + min + " sampai " + max + ".");
        }
    }

    private static boolean bacaYaTidak(String prompt) {
        while (true) {
            String nilai = bacaTeks(prompt).toLowerCase(Locale.ROOT);
            if (nilai.equals("ya") || nilai.equals("y")) return true;
            if (nilai.equals("tidak") || nilai.equals("t") || nilai.equals("n")) return false;
            System.out.println("Jawab ya atau tidak.");
        }
    }

    private static String rupiah(long nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "%,d", nilai).replace(',', '.');
    }
}
