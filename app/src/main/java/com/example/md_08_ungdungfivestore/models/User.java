package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    private String id;
    private String username;
    private String email;

    @SerializedName(value = "fullName", alternate = {"full_name"})
    private String fullName;

    // ⭐ ĐÃ SỬA: Đổi tên biến Java thành phoneNumber (tên thường dùng) ⭐
    // Ánh xạ vẫn chấp nhận cả "phone" (khi gửi) và "phone_number" (khi nhận từ Backend)
    @SerializedName(value = "phone", alternate = {"phone_number"})
    private String phoneNumber;

    // Các trường địa chỉ phẳng (flat fields)
    private String street;
    private String ward;
    private String district;
    private String province;

    private String date_of_birth;
    private int gender;
    private String avatar_url;


    // Constructor rỗng (Rất quan trọng cho Gson)
    public User() {
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public String getFullName() { return fullName; }

    // ⭐ ĐÃ SỬA: GETTER MỚI ⭐
    public String getPhoneNumber() { return phoneNumber; }

    public String getStreet() { return street; }
    public String getWard() { return ward; }
    public String getDistrict() { return district; }
    public String getProvince() { return province; }
    public String getDateOfBirth() { return date_of_birth; }
    public int getGender() { return gender; }
    public String getAvatarUrl() { return avatar_url; }


    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }

    // ⭐ ĐÃ SỬA: SETTER MỚI ⭐
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setStreet(String street) { this.street = street; }
    public void setWard(String ward) { this.ward = ward; }
    public void setDistrict(String district) { this.district = district; }
    public void setProvince(String province) { this.province = province; }
    public void setDateOfBirth(String date_of_birth) { this.date_of_birth = date_of_birth; }
    public void setGender(int gender) { this.gender = gender; }
    public void setAvatarUrl(String avatar_url) { this.avatar_url = avatar_url; }
}