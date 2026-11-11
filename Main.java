import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final Scanner input = new Scanner(System.in);
    private static final Menu menu = new Menu();
    private static final String FILE_MENU = "menu.txt";

    public static void main(String[] args) {
        System.out.println("=== APLIKASI MANAJEMEN RESTORAN SEDAP RASA ===");
        muatMenuPertama();
        int pilihan;
        do {
            tampilkanMenuUtama();
            pilihan = bacaAngka("Pilihan Anda: ");
            switch (pilihan) {
                case 1:
                    tambahItemMenu();
                    break;
                case 2:
                    menu.tampilkanMenu();
                    break;
                case 3:
                    prosesPesanan();
                    break;
                case 4:
                    menu.simpanKeFile(FILE_MENU);
                    break;
                case 5:
                    menu.muatDariFile(FILE_MENU);
                    break;
                case 6:
                    System.out.println("Terima kasih. Sampai jumpa!");
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia, silakan coba lagi.");
            }
        } while (pilihan != 6);
    }

    private static void muatMenuPertama() {
        File fileMenu = new File(FILE_MENU);
        if (fileMenu.exists()) {
            menu.muatDariFile(FILE_MENU);
        } else {
            isiMenuAwal();
        }
    }

    private static void isiMenuAwal() {
        menu.tambahItem(new Makanan("Nasi Goreng Spesial", 25000, "Utama"));
        menu.tambahItem(new Makanan("Sate Ayam", 20000, "Utama"));
        menu.tambahItem(new Makanan("Puding Cokelat", 10000, "Penutup"));
        menu.tambahItem(new Minuman("Es Teh Manis", 8000, "Dingin"));
        menu.tambahItem(new Minuman("Kopi Susu", 15000, "Panas"));
        menu.tambahItem(new Diskon("Promo Member", 10));
    }

    private static void tampilkanMenuUtama() {
        System.out.println();
        System.out.println("------------- MENU UTAMA -------------");
        System.out.println("1. Tambah item menu");
        System.out.println("2. Tampilkan menu restoran");
        System.out.println("3. Buat pesanan");
        System.out.println("4. Simpan menu ke file");
        System.out.println("5. Muat menu dari file");
        System.out.println("6. Keluar");
    }

    private static void tambahItemMenu() {
        System.out.println("Jenis item baru: 1. Makanan | 2. Minuman | 3. Diskon");
        int jenis = bacaAngka("Pilih jenis: ");
        if (jenis == 1) {
            String nama = bacaTeks("Nama makanan : ");
            String jenisMakanan = bacaTeks("Jenis makanan (mis. Utama/Penutup): ");
            double harga = bacaDesimal("Harga: ");
            menu.tambahItem(new Makanan(nama, harga, jenisMakanan));
            System.out.println("Makanan baru berhasil ditambahkan.");
        } else if (jenis == 2) {
            String nama = bacaTeks("Nama minuman : ");
            String jenisMinuman = bacaTeks("Jenis minuman (mis. Panas/Dingin): ");
            double harga = bacaDesimal("Harga: ");
            menu.tambahItem(new Minuman(nama, harga, jenisMinuman));
            System.out.println("Minuman baru berhasil ditambahkan.");
        } else if (jenis == 3) {
            String nama = bacaTeks("Nama promo: ");
            double persen = bacaDesimal("Besar diskon (%): ");
            menu.tambahItem(new Diskon(nama, persen));
            System.out.println("Diskon baru berhasil ditambahkan.");
        } else {
            System.out.println("Jenis item tidak dikenali.");
        }
    }

    private static void prosesPesanan() {
        if (menu.getJumlahItem() == 0) {
            System.out.println("Menu masih kosong, tambahkan item terlebih dahulu.");
            return;
        }
        Pesanan pesanan = new Pesanan();
        System.out.println("Masukkan nomor item yang dipesan (0 untuk selesai).");
        while (true) {
            menu.tampilkanMenu();
            int nomor = bacaAngka("Nomor item (0 = selesai): ");
            if (nomor == 0) {
                break;
            }
            try {
                MenuItem item = menu.getItem(nomor);
                if (item instanceof Diskon) {
                    System.out.println("Diskon tidak dapat dipesan, pilih saat pembayaran.");
                    continue;
                }
                int jumlah = bacaAngka("Jumlah: ");
                if (jumlah < 1) {
                    System.out.println("Jumlah minimal 1, pesanan tidak dicatat.");
                    continue;
                }
                for (int i = 0; i < jumlah; i++) {
                    pesanan.tambahItem(item);
                }
                System.out.println(jumlah + " x " + item.getNama() + " masuk ke pesanan.");
            } catch (MenuTidakTersediaException e) {
                System.out.println(e.getMessage());
            }
        }
        if (pesanan.getJumlahItem() == 0) {
            System.out.println("Pesanan dibatalkan karena tidak ada item.");
            return;
        }
        terapkanDiskon(pesanan);
        pesanan.tampilkanStruk();
        String jawab = bacaTeks("Simpan struk ke file? (y/t): ");
        if (jawab.equalsIgnoreCase("y")) {
            pesanan.simpanStruk(buatNamaFileStruk());
        }
    }

    private static void terapkanDiskon(Pesanan pesanan) {
        String jawab = bacaTeks("Gunakan promo diskon? (y/t): ");
        if (!jawab.equalsIgnoreCase("y")) {
            return;
        }
        ArrayList<Diskon> daftarDiskon = new ArrayList<>();
        for (MenuItem item : menu.getDaftarMenu()) {
            if (item instanceof Diskon) {
                daftarDiskon.add((Diskon) item);
            }
        }
        if (daftarDiskon.isEmpty()) {
            System.out.println("Belum ada promo diskon pada menu.");
            return;
        }
        System.out.println("Daftar promo diskon:");
        for (int i = 0; i < daftarDiskon.size(); i++) {
            Diskon d = daftarDiskon.get(i);
            System.out.println((i + 1) + ". " + d.getNama() + " (" + d.persenDiskon() + "%)");
        }
        int pilih = bacaAngka("Pilih nomor promo: ");
        if (pilih >= 1 && pilih <= daftarDiskon.size()) {
            pesanan.setDiskon(daftarDiskon.get(pilih - 1));
            System.out.println("Promo berhasil diterapkan.");
        } else {
            System.out.println("Pilihan tidak valid, pesanan dilanjutkan tanpa diskon.");
        }
    }

    private static String buatNamaFileStruk() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "struk_" + LocalDateTime.now().format(format) + ".txt";
    }

    private static int bacaAngka(String pesan) {
        int angka;
        while (true) {
            try {
                System.out.print(pesan);
                angka = input.nextInt();
                input.nextLine();
                return angka;
            } catch (InputMismatchException e) {
                System.out.println("Input harus berupa bilangan bulat.");
                input.nextLine();
            }
        }
    }

    private static double bacaDesimal(String pesan) {
        double nilai;
        while (true) {
            try {
                System.out.print(pesan);
                nilai = input.nextDouble();
                input.nextLine();
                return nilai;
            } catch (InputMismatchException e) {
                System.out.println("Input harus berupa angka, mis. 15000 atau 15000.5.");
                input.nextLine();
            }
        }
    }

    private static String bacaTeks(String pesan) {
        System.out.print(pesan);
        return input.nextLine();
    }
}