package com.example.md_08_ungdungfivestore.utils;

/**
 * Constants cho các trạng thái đơn hàng
 */
public class OrderStatus {
    public static final String PENDING = "pending"; // Chờ xác nhận
    public static final String CONFIRMED = "confirmed"; // Đã xác nhận
    public static final String PROCESSING = "processing"; // Đang xử lý
    public static final String SHIPPING = "shipping"; // Đang giao
    public static final String DELIVERED = "delivered"; // Đã giao
    public static final String CANCELLED = "cancelled"; // Đã hủy

    /**
     * Chuyển đổi status code sang text hiển thị
     */
    public static String getStatusText(String status) {
        switch (status) {
            case PENDING:
                return "Chờ xác nhận";
            case CONFIRMED:
                return "Đã xác nhận";
            case PROCESSING:
                return "Đang xử lý";
            case SHIPPING:
                return "Đang giao hàng";
            case DELIVERED:
                return "Đã giao hàng";
            case CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    /**
     * Kiểm tra xem đơn hàng có thể hủy được không
     * Chỉ cho phép hủy khi status = pending
     */
    public static boolean canCancel(String status) {
        return PENDING.equals(status);
    }

    /**
     * Lấy màu sắc cho từng trạng thái (có thể dùng cho UI)
     */
    public static int getStatusColor(String status) {
        switch (status) {
            case PENDING:
                return 0xFFFFA726; // Orange
            case CONFIRMED:
                return 0xFF42A5F5; // Blue
            case PROCESSING:
                return 0xFF9C27B0; // Purple
            case SHIPPING:
                return 0xFF29B6F6; // Light Blue
            case DELIVERED:
                return 0xFF66BB6A; // Green
            case CANCELLED:
                return 0xFFEF5350; // Red
            default:
                return 0xFF757575; // Grey
        }
    }
}
