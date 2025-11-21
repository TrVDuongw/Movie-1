public class Ve {
    private String tenPhim;
    private String ghe;
    private String tenKhach;
    private String email;
    public Ve(String tenPhim, String ghe, String tenKhach, String email) {
        this.tenPhim = tenPhim;
        this.ghe = ghe;
        this.tenKhach = tenKhach;
        this.email = email;
    }
    public String getTenPhim() { return tenPhim; }
    public String getGhe() { return ghe; }
    public String getTenKhach() { return tenKhach; }
    public String getEmail() { return email; }
    @Override
    public String toString() {
        return "Ve xem: "+tenPhim+"\nGhe: "+ghe+"\nKhach: "+tenKhach+
                (email!=null && !email.isEmpty() ? "\nEmail: "+email : "");
    }
}
