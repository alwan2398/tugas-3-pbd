import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Pesanan {

    private ArrayList<MenuItem> daftarItem;
    private Diskon diskon;

    public Pesanan() {
        daftarItem = new ArrayList<>();
        diskon = null;
    }

    public void tambahItem(MenuItem item) {
        daftarItem.add(item);
    }

    public void setDiskon(Diskon diskon) {
        this.diskon = diskon;
    }

    public int getJumlahItem() {
        return daftarItem.size();
    }

    public double hitungSubtotal() {
        double total = 0;
        for (MenuItem item : daftarItem) {
            total += item.getHarga();
        }
        return total;
    }

    public double hitungPotongan() {
        if (diskon == null) {
            return 0;
        }
        return diskon.hitungPotongan(hitungSubtotal());
    }

    public double hitungTotal() {
        return hitungSubtotal() - hitungPotongan();
    }

    public String buatStruk() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        StringBuilder struk = new StringBuilder();
        struk.append("==============================================\n");
        struk.append("             RESTORAN SEDAP RASA\n");
        struk.append("==============================================\n");
        struk.append("Tanggal : ").append(LocalDateTime.now().format(format)).append("\n");
        struk.append("----------------------------------------------\n");
        int nomor = 1;
        for (int i = 0; i < daftarItem.size(); i++) {
            MenuItem item = daftarItem.get(i);
            boolean sudahTercetak = false;
            for (int j = 0; j < i; j++) {
                if (daftarItem.get(j).getNama().equalsIgnoreCase(item.getNama())) {
                    sudahTercetak = true;
                    break;
                }
            }
            if (sudahTercetak) {
                continue;
            }
            int jumlah = 0;
            for (MenuItem lain : daftarItem) {
                if (lain.getNama().equalsIgnoreCase(item.getNama())) {
                    jumlah++;
                }
            }
            struk.append(String.format("%2d. %-22s x%-3d %12s%n", nomor, item.getNama(), jumlah,
                    MenuItem.formatRupiah(item.getHarga() * jumlah)));
            nomor++;
        }
        struk.append("----------------------------------------------\n");
        struk.append(String.format("%-31s %14s%n", "Subtotal", MenuItem.formatRupiah(hitungSubtotal())));
        if (diskon != null) {
            struk.append(String.format("%-31s %14s%n",
                    "Diskon " + diskon.getNama() + " (" + diskon.persenDiskon() + "%)",
                    "-" + MenuItem.formatRupiah(hitungPotongan())));
        }
        struk.append(String.format("%-31s %14s%n", "TOTAL", MenuItem.formatRupiah(hitungTotal())));
        struk.append("==============================================\n");
        struk.append("        Terima kasih atas kunjungan Anda\n");
        return struk.toString();
    }

    public void tampilkanStruk() {
        System.out.println(buatStruk());
    }

    public void simpanStruk(String namaFile) {
        try (BufferedWriter tulis = new BufferedWriter(new FileWriter(namaFile))) {
            tulis.write(buatStruk());
            System.out.println("Struk tersimpan di " + namaFile);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan struk: " + e.getMessage());
        }
    }
}