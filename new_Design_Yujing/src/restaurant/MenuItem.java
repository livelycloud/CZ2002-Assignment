package restaurant;

public interface MenuItem {
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    double getPrice();
    void setPrice(double price);
    int getQuantity();
    void setQuantity(int quantity);
    double getTotalPrice();
    int getCode();
    void setCode(int code);
    void print();
}
