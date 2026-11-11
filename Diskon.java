public class Diskon extends MenuItem {

    private double diskon;

    public Diskon(String nama, double diskon) {
        super(nama, 0, "Diskon");
        this.diskon = diskon;
    }

    public double getDiskon() {
        return diskon;
    }

    public void setDiskon(double diskon) {
        this.diskon = diskon;
    }

    public String persenDiskon() {
        if (diskon == Math.floor(diskon)) {
            return String.format("%.0f", diskon);
        }
        return String.valueOf(diskon);
    }

    public double hitungPotongan(double subtotal) {
        return subtotal * diskon / 100;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("%-25s %-15s %s%n", getNama(), "-", "Diskon " + persenDiskon() + "%");
    }

    @Override
    public String toFile() {
        return "DISKON;" + getNama() + ";" + diskon;
    }
}