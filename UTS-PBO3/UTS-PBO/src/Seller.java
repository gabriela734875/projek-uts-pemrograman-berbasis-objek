public class Seller extends Person implements Cetak { // Atribut tambahan untuk Seller yang mengimplementasikan interface Cetak
    private String idSeller; // Atribut ID Seller
    private String jabatan; // Atribut jabatan

    public Seller(String nama, String alamat, String noTelpn, String email, String idSeller, String jabatan) {
        super(nama, alamat, noTelpn, email);
        this.idSeller = idSeller;
        this.jabatan = jabatan;
    }

    public void inputPenjualan() {
        System.out.println(getNama() + " sedang menginput data penjualan.");
    }

    //Override
    public void cetak() {
        System.out.println("--- Cetak Data Seller ---");
        tampilkanData();
    }

    //Abstract method dari class Person
    public void tampilkanData() {
        System.out.println("ID Seller : " + idSeller);
        System.out.println("Nama      : " + getNama());
        System.out.println("Jabatan   : " + jabatan);
        System.out.println("Alamat    : " + getAlamat());
        System.out.println("No Telp   : " + getNoTelpn());
        System.out.println("Email     : " + getEmail());
    }

    // Setters & getters atribut Seller
    public String getIdSeller() { 
        return idSeller; 
    }
    public void setIdSeller(String idSeller) { 
        this.idSeller = idSeller; 
    }

    public String getJabatan() { 
        return jabatan; 
    }
    public void setJabatan(String jabatan) { 
        this.jabatan = jabatan; 
    }
}