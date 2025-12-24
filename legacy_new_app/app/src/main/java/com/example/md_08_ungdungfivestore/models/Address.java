package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

public class Address {

    // ✅ SỬA ĐỂ CHẤP NHẬN CẢ INPUT (fullName) và OUTPUT (full_name)
    @SerializedName(value = "fullName", alternate = {"full_name"})
    private String fullName;

    // ✅ SỬA ĐỂ CHẤP NHẬN CẢ INPUT (phone) và OUTPUT (phone_number/phone)
    // Giả định Backend trả về 'phone_number' khi xem chi tiết
    @SerializedName(value = "phone", alternate = {"phone_number"})
    private String phone;

    // Các trường địa chỉ khác (giữ nguyên, giả định tên khớp)
    private String province;
    private String district;
    private String ward;
    private String street;

    // Constructor rỗng (Cần thiết cho Retrofit/Gson)
    public Address() {
    }

    // Constructor đầy đủ (Giữ nguyên)
    public Address(String fullName, String phone, String province, String district, String ward, String street) {
        this.fullName = fullName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
    }

    // --- GETTERS (Giữ nguyên, sẽ tự động lấy giá trị đã được ánh xạ) ---
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
    public String getStreet() { return street; }

    // --- SETTERS ---
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProvince(String province) { this.province = province; }
    public void setDistrict(String district) { this.district = district; }
    public void setWard(String ward) { this.ward = ward; }
    public void setStreet(String street) { this.street = street; }
}