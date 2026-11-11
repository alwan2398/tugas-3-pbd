import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Menu {

    private ArrayList<MenuItem> daftarMenu;

    public Menu() {
        daftarMenu = new ArrayList<>();
    }

    public void tambahItem(MenuItem item) {
        daftarMenu.add(item);
    }

    public int getJumlahItem() {
        return daftarMenu.size();
    }

    public ArrayList<MenuItem> getDaftarMenu() {
        return daftarMenu;
    }

    public MenuItem getItem(int nomor) throws MenuTidakTersediaException {
        if (nomor < 1 || nomor > daftarMenu.size()) {
            throw new MenuTidakTersediaException("Item nomor " + nomor + " tidak ada dalam menu.");
        }
        return daftarMenu.get(nomor - 1);
    }

    public void tampilkanMenu() {
        if (daftarMenu.isEmpty()) {
            System.out.println("Menu masih kosong.");
            return;
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-4s %-25s %-15s %s%n", "No", "Nama", "Jenis", "Harga");
        System.out.println("--------------------------------------------------------------");
        for (int i = 0; i < daftarMenu.size(); i++) {
            System.out.printf("%-4d", i + 1);
            daftarMenu.get(i).tampilMenu();
        }
        System.out.println("--------------------------------------------------------------");
    }

    public void simpanKeFile(String namaFile) {
        try (BufferedWriter tulis = new BufferedWriter(new FileWriter(namaFile))) {
            for (MenuItem item : daftarMenu) {
                tulis.write(item.toFile());
                tulis.newLine();
            }
            System.out.println("Menu (" + daftarMenu.size() + " item) tersimpan di " + namaFile);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan menu: " + e.getMessage());
        }
    }

    public void muatDariFile(String namaFile) {
        daftarMenu.clear();
        int barisGagal = 0;
        try (BufferedReader baca = new BufferedReader(new FileReader(namaFile))) {
            String baris;
            while ((baris = baca.readLine()) != null) {
                if (baris.isBlank()) {
                    continue;
                }
                try {
                    String[] data = baris.split(";");
                    if (data[0].equals("MAKANAN")) {
                        daftarMenu.add(new Makanan(data[1], Double.parseDouble(data[2]), data[3]));
                    } else if (data[0].equals("MINUMAN")) {
                        daftarMenu.add(new Minuman(data[1], Double.parseDouble(data[2]), data[3]));
                    } else if (data[0].equals("DISKON")) {
                        daftarMenu.add(new Diskon(data[1], Double.parseDouble(data[2])));
                    } else {
                        barisGagal++;
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    barisGagal++;
                }
            }
            System.out.println(daftarMenu.size() + " item berhasil dimuat dari " + namaFile);
            if (barisGagal > 0) {
                System.out.println(barisGagal + " baris tidak valid dilewati.");
            }
        } catch (IOException e) {
            System.out.println("Gagal memuat menu: " + e.getMessage());
        }
    }
}
