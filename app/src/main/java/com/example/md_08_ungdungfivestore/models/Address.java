package com.example.md_08_ungdungfivestore.models;

import java.io.Serializable;

/**
 * Model cho địa chỉ giao hàng
 */
public class Address implements Serializable {
    private String _id;
    private String full_name;
    private String phone_number;
    private String province;
    private String district;
    private String ward;
    private String street;
    private boolean is_default;

    public Address() {
    }

    public Address(String full_name, String phone_number, String province, String district,
            String ward, String street, boolean is_default) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.is_default = is_default;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean is_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    /**
     * Trả về địa chỉ đầy đủ dạng chuỗi
     */
    public String getFullAddress() {
        return street + ", " + ward + ", " + district + ", " + province;
    }
}
