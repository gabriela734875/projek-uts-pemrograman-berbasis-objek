public class Buyer extends Person implements Cetak {
    private String noKTP; // Atribut nomor KTP
    private String pekerjaan; // Atribut pekerjaan
    private String tanggalLahir; // Disesuaikan menjadi String sesuai diagram
    private String alamatKantor; // Atribut alamat kantor

    // Constructor dengan parameter String untuk tanggalLahir
    public Buyer(String nama, String alamat, String noTelpn, String email, 
                 String noKTP, String pekerjaan, String tanggalLahir, String alamatKantor) { // Constructor dengan parameter nama, alamat, noTelpn, email, noKTP, pekerjaan, tanggalLahir, dan alamatKantor
        super(nama, alamat, noTelpn, email); // Memanggil constructor dari kelas induk Person
        this.noKTP = noKTP;
        this.pekerjaan = pekerjaan;
        this.tanggalLahir = tanggalLahir;
        this.alamatKantor = alamatKantor;
    }

    public void pesanKendaraan() {
        System.out.println(getNama() + " sedang memesan kendaraan.");
    }

    //Override
    public void cetak() {
        System.out.println("--- Cetak Data Buyer ---");
        tampilkanData();
    }

    //Abstract method dari class Person
    public void tampilkanData() {
        System.out.println("Nama Buyer    : " + getNama());
        System.out.println("No KTP        : " + noKTP);
        System.out.println("Alamat        : " + getAlamat());
        System.out.println("No Telp       : " + getNoTelpn());
        System.out.println("Email         : " + getEmail());
        System.out.println("Pekerjaan     : " + pekerjaan);
        System.out.println("Tanggal Lahir : " + tanggalLahir);
        System.out.println("Alamat Kantor : " + alamatKantor);
    }

    // Setters & getters
    public String getNoKTP() { 
        return noKTP; 
    }
    public void setNoKTP(String noKTP) { 
        this.noKTP = noKTP; 
    }

    public String getPekerjaan() { 
        return pekerjaan; 
    }
    public void setPekerjaan(String pekerjaan) { 
        this.pekerjaan = pekerjaan; 
    }

    public String getTanggalLahir() { 
        return tanggalLahir; 
    }
    public void setTanggalLahir(String tanggalLahir) { 
        this.tanggalLahir = tanggalLahir; 
    }

    public String getAlamatKantor() { 
        return alamatKantor; 
    }
    public void setAlamatKantor(String alamatKantor) { 
        this.alamatKantor = alamatKantor; 
    }
}