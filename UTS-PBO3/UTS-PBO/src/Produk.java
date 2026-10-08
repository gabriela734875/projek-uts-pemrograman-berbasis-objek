public class Produk {
    private String kodeProduk; // Atribut kode produk
    private String modelTipe; // Atribut tipe produk
    private String warna; // Atribut warna
    private String noMesin; // Atribut nomor mesin produk
    private String noRangka; // atribut nomor rangka produk
    private Long harga; // Menggunakan long utk tipe data satuan rupiah

    public Produk(String kodeProduk, String modelTipe, String warna, String noMesin, String noRangka, Long harga) {
        this.kodeProduk = kodeProduk;
        this.modelTipe = modelTipe;
        this.warna = warna;
        this.noMesin = noMesin;
        this.noRangka = noRangka;
        this.harga = harga;
    }

    // Setters & getters
    public String getKodeProduk() { 
        return kodeProduk; 
    }
    public void setKodeProduk(String kodeProduk) { 
        this.kodeProduk = kodeProduk; 
    }

    public String getModelTipe() { 
        return modelTipe; 
    }
    public void setModelTipe(String modelTipe) { 
        this.modelTipe = modelTipe; 
    }

    public String getWarna() { 
        return warna; }
    public void setWarna(String warna) { 
        this.warna = warna; 
    }

    public String getNoMesin() { 
        return noMesin; 
    }
    public void setNoMesin(String noMesin) { 
        this.noMesin = noMesin; 
    }

    public String getNoRangka() { 
        return noRangka; 
    }
    public void setNoRangka(String noRangka) { 
        this.noRangka = noRangka; 
    }

    public Long getHarga() { 
        return harga; 
    }
    public void setHarga(Long harga) { 
        this.harga = harga; 
    }
}