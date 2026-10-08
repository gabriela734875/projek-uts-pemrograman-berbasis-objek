public abstract class Person {
    private String nama; // Atribut nama
    private String alamat; // Atribut alamat
    private String noTelpn; // Atribut nomor telepon
    private String email; // Atribut email

    // Constructor
    public Person(String nama, String alamat, String noTelpn, String email) { // Constructor dengan parameter nama, alamat, noTelpn, dan email
        this.nama = nama;
        this.alamat = alamat;
        this.noTelpn = noTelpn;
        this.email = email;
    }

    // Setters & getters
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) { 
        this.nama = nama; 
    }

    public String getAlamat() { 
        return alamat; 
    }
    public void setAlamat(String alamat) { 
        this.alamat = alamat; 
    }

    public String getNoTelpn() { 
        return noTelpn; 
    }
    public void setNoTelpn(String noTelpn) { 
        this.noTelpn = noTelpn; 
    }

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    // Abstract method untuk menampilkan data, harus diimplementasikan oleh inheritance class
    public abstract void tampilkanData();
}
