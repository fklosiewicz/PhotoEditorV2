package com.example.photoeditorv2;

public class EulerityImage {

    String address;
    String created;
    String updated;

    public EulerityImage(String address, String created, String updated) {
        this.address = address;
        this.created = created;
        this.updated = updated;
    }

    public String toString() {
        return address;
    }
}
