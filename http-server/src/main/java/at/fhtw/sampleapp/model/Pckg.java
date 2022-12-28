package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Pckg {
    @JsonAlias({"P_d"})
    private int package_id;
    @JsonAlias({"Name"})
    private String package_name;
    @JsonAlias({"Price"})
    private int package_price;

    public Pckg() {

    }
    public Pckg(String package_name, int package_price) {
        this.package_name = package_name;
        this.package_price = package_price;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public int getPackage_price() {
        return package_price;
    }

    public void setPackage_price(int package_price) {
        this.package_price = package_price;
    }
}
