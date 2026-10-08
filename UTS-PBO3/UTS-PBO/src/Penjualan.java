import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

public class Penjualan {
    private String noSPK; // Atribut nomor surat pemesanan kendaraan
    private String tanggal; // Atribut tanggal produk telah dijual
    private int jumlahUnit; // Atribut jumlah unit kendaraan yang dijual
    private double discount; // Atribut diskon dalam persen
    private String leasing; // Atribut nama leasing yang digunakan untuk pembiayaan kendaraan
    private String metodePembayaran; // Atribut metode pembayaran produk
    private long bookingFee; // 
    private long hargaTotal; // Kedua bookingFee & hargaTotal menggunakan long utk tipe data satuan rupiah

    // Constructor dengan parameter untuk semua atribut
    public Penjualan(String noSPK, String tanggal, int jumlahUnit, double discount, 
                     String leasing, String metodePembayaran, long bookingFee, long hargaTotal) {
        this.noSPK = noSPK;
        this.tanggal = tanggal;
        this.jumlahUnit = jumlahUnit;
        this.discount = discount;
        this.leasing = leasing;
        this.metodePembayaran = metodePembayaran;
        this.bookingFee = bookingFee;
        this.hargaTotal = hargaTotal;
    }

    public long hitungTotal() {
        long subtotal = Math.multiplyExact(this.hargaTotal, this.jumlahUnit);
        return Math.round(subtotal * (1.0 - this.discount / 100.0));
    }

    public void simpanPenjualan() {
        System.out.println("Data penjualan dengan No SPK: " + noSPK + " berhasil disimpan.");
    }

    public void simpanPenjualan(Path file, Buyer buyer, Produk produk) throws IOException {
        String baris = String.join("|", noSPK, tanggal, buyer.getNoKTP(), buyer.getNama(),
                produk.getKodeProduk(), Integer.toString(jumlahUnit), metodePembayaran, leasing,
                Double.toString(discount), Long.toString(bookingFee), Long.toString(hitungTotal()))
                + System.lineSeparator();
        Files.write(file, baris.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public long hitungPengembalianBookingFee(double persentase) {
        return Math.round(this.bookingFee * persentase / 100.0);
    }

    public void catatPembatalan(Path file, Buyer buyer, String alasan, long jumlahPengembalian)
            throws IOException {
        String baris = String.join("|", noSPK, LocalDate.now().toString(), buyer.getNoKTP(),
                buyer.getNama(), alasan, Long.toString(bookingFee), Long.toString(jumlahPengembalian))
                + System.lineSeparator();
        Files.write(file, baris.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    // Setters & getters
    public String getNoSPK() { 
        return noSPK; 
    }
    public void setNoSPK(String noSPK) { 
        this.noSPK = noSPK; 
    }

    public String getTanggal() { 
        return tanggal; 
    }
    public void setTanggal(String tanggal) { 
        this.tanggal = tanggal; 
    }

    public int getJumlahUnit() { 
        return jumlahUnit; 
    }
    public void setJumlahUnit(int jumlahUnit) { 
        this.jumlahUnit = jumlahUnit; 
    }

    public double getDiscount() { 
        return discount; 
    }
    public void setDiscount(double discount) { 
        this.discount = discount; 
    }

    public String getLeasing() { 
        return leasing; 
    }
    public void setLeasing(String leasing) { 
        this.leasing = leasing; 
    }

    public String getMetodePembayaran() { 
        return metodePembayaran; 
    }
    public void setMetodePembayaran(String metodePembayaran) { 
        this.metodePembayaran = metodePembayaran; 
    }

    public long getBookingFee() { 
        return bookingFee; 
    }
    public void setBookingFee(long bookingFee) { 
        this.bookingFee = bookingFee; 
    }

    public long getHargaTotal() { 
        return hargaTotal; 
    }
    public void setHargaTotal(long hargaTotal) { 
        this.hargaTotal = hargaTotal; 
    }
}
