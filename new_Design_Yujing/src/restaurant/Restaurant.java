package restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.*;

/**
 * Restaurant class bundles different systems in the restaurant. The class
 * itself doesn't do a lot.
 *
 * @author owl
 * @author TODO
 * @version 0.0
 */
public class Restaurant {

    /**
     * Restaurant menu.
     */
    public ArrayList<Category> menu;

    /**
     * Restaurant menu code to item look up talbe.
     */
    public HashMap<Integer, MenuItem> menuReference;

    /**
     * Table and reservation manager.
     */
    private TableManager tableManager;

    /**
     * Active orders.
     */
    public ArrayList<Order> activeOrders;

    /**
     * Inactive (paied) orders.
     */
    public ArrayList<Order> inactiveOrders;

    /**
     * Restaurant staff.
     */
    public HashMap<Integer, Staff> staff;

    /**
     * Restaurant historical orders, used for sales report.
     */
    private TreeSet<Order> historyOrders;

    public Restaurant() {
        menuReference = new HashMap<>();
        loadMenuReference();
        System.out.println("loadMenuReference done");
        menu = new ArrayList<>();
        loadMenu();
        System.out.println("loadMenu done");
        tableManager = new TableManager();
        staff = new HashMap<>();
        loadStaff();
        activeOrders = new ArrayList<>();
        loadOrder();
        inactiveOrders = new ArrayList<>();
        historyOrders = new TreeSet<>();

    }

    /**
     * Returns a list of all active orders.
     *
     * @return A list of all active orders.
     */
    public ArrayList<Order> getActiveOrders() {
        return activeOrders;
    }

    /**
     * Search active orders by table id.
     *
     * @param tableId Inquired table id.
     * @return Active order using the table, or {@code null} if not found.
     */
    public Order getOrderByTableId(int tableId) {
        int n = activeOrders.size();
        Order o = null;
        for (int i = 0; i < n; i++) {
            if (activeOrders.get(i).getTableId() == tableId) {
                o = activeOrders.get(i);
                break;
            }
        }
        return o;
    }

    /**
     * Generates a sales report of the given time period.
     *
     * @return A sales report of the given time period.
     */
    public SalesReport generateSalesReport(LocalDate from, LocalDate to) {
        SalesReport report = new SalesReport(from, to);
        if (historyOrders.size() == 0)
            loadHistoryOrders();
        var it = historyOrders.iterator();
        for (; it.hasNext();) {
            Order o = (Order) it.next();
            if (o.getTime().toLocalDate().isAfter(from) && o.getTime().toLocalDate().isBefore(to)) {
                report.processOrder(o);
            }
        }
        return report;
    }

    /**
     * Load menu items code information from file.
     */
    public void loadMenuReference() {
        try {
            String fileName = "restaurant\\MenuReference.txt";
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String l;
            ArrayList<String[]> bundleLists = new ArrayList<>();
            while ((l = br.readLine()) != null) {
                String[] temp = l.split(":");
                int itemCode = Integer.parseInt(temp[0]);
                int flag = Integer.parseInt(temp[1]);
                if (flag == 0) {
                    // alacarte item
                    String itemName = temp[2];
                    Double price = Double.parseDouble(temp[3]);
                    String itemDescription = temp[4];
                    MenuLeaf ml = new MenuLeaf(itemCode, itemName, itemDescription);
                    ml.setPrice(price);
                    menuReference.put(itemCode, ml);
                } else {
                    bundleLists.add(temp);
                }
            }

            for (String[] temp : bundleLists) {
                int itemCode = Integer.parseInt(temp[0]);
                // bundle item
                /* assumed always the component of the bundle item has alreay been parsed */
                String itemName = temp[2];
                double price = Double.parseDouble(temp[3]);
                MenuBundle mb = new MenuBundle(itemCode, itemName, "");
                mb.setPrice(price);
                String[] bundleItems = temp[4].split(",");
                int numOfItems = bundleItems.length;
                for (int i = 0; i < numOfItems; i++) {
                    int childCode = Integer.parseInt(bundleItems[i]);
                    mb.addChild(menuReference.get(childCode));
                }
                menuReference.put(itemCode, mb);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save menu items code information to file.
     */
    public void saveMenuReference() {
        try {
            String fileName = "restaurant\\MenuReference.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for (MenuItem mc : menuReference.values()) {
                if (mc instanceof MenuBundle mb) {
                    String t = mc.getCode() + ":1:" + mc.getName() + ":" + mc.getPrice() + ":";
                    for (int i = 0; i < mb.getChildrenCount(); i++) {
                        t = t + mb.getChild(i).getCode() + ",";
                    }
                    bw.write(t.substring(0, t.length() - 1) + "\n");

                } else {
                    bw.write(mc.getCode() + ":0:" + mc.getName() + ":" + mc.getPrice() + ":" + mc.getDescription() + "\n");
                }

            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load menu information from file.
     */
    private void loadMenu() {
        try {
            String fileName = "restaurant\\Menu.txt";

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int numOfCategory = Integer.parseInt(br.readLine());
            for (int i = 0; i < numOfCategory; i++) {
                String categoryName = br.readLine();
                menu.add(new Category(categoryName));
                int numOfItems = Integer.parseInt(br.readLine());
                for (int j = 0; j < numOfItems; j++) {
                    int itemCode = Integer.parseInt(br.readLine());
                    menu.get(i).addChild(menuReference.get(itemCode));
                }
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save menu information to file.
     */
    private void saveMenu() {
        try {
            String fileName = "restaurant\\Menu.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(menu.size() + "\n");

            for (int i = 0; i < menu.size(); i++) {
                Category category = menu.get(i);
                bw.write(category.getName() + "\n");
                bw.write(category.getChildrenCount() + "\n");
                for (int j = 0; j < category.getChildrenCount(); j++) {
                    MenuItem item = category.getChild(j);
                    bw.write(item.getCode() + "\n");
                }
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load order information from file.
     */
    private void loadOrder() {
        try {
            String fileName = "restaurant\\activeOrders.txt";

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int numOfOrders = Integer.parseInt(br.readLine());
            for (int i = 0; i < numOfOrders; i++) {
                String l = br.readLine();
                String[] list = l.split(",");
                LocalDateTime time = LocalDateTime.parse(list[0]);
                int tableId = Integer.parseInt(list[1]);
                int pax = Integer.parseInt(list[2]);
                long contact = Long.parseLong(list[3]);
                Reservation r = new Reservation(time, tableId, pax, contact);
                tableManager.addReservation(r);

                int staffId = Integer.parseInt(br.readLine());
                Staff s = staff.get(staffId);
                Order curOrder = new Order(r, s);
                int n = Integer.parseInt(br.readLine());
                for (int j = 0; j < n; j++) {
                    l = br.readLine();
                    String[] temp = l.split(",");
                    int quantity = Integer.parseInt(temp[0]);
                    int itemCode = Integer.parseInt(temp[1]);
                    curOrder.addItem(menuReference.get(itemCode), quantity);
                }
                activeOrders.add(curOrder);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save active orders & inactive orders to file.
     */
    private void saveOrder() {
        // save active orders
        try {
            String filename = "restaurant\\activeOrders.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(activeOrders.size() + "\n");
            for (Order curOrder : activeOrders) {
                Reservation r = curOrder.getReservation();
                bw.write(r.getTime().toString() + "," + r.getTableId() + "," + r.getPax() + "," + r.getContact() + "\n");
                bw.write(curOrder.getStaffId() + "\n");
                bw.write(curOrder.getChildrenCount() + "\n");
                for (int i = 0; i < curOrder.getChildrenCount(); i++) {
                    MenuItem mc = curOrder.getChild(i);
                    int quantity = mc.getQuantity();
                    bw.write(quantity + "," + mc.getCode() + "\n");
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // save history orders
        try {

            for (Order curOrder : inactiveOrders) {
                String filename = "restaurant\\historyOrders\\"
                        + curOrder.getReservation().getTime().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "")
                        + ".txt";
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                Reservation r = curOrder.getReservation();
                bw.write(r.getTime().toString() + "," + r.getTableId() + "," + r.getPax() + "," + r.getContact() + "\n");
                bw.write(curOrder.getStaffId() + "\n");
                bw.write(curOrder.getTotalPrice() + "\n");
                bw.write(curOrder.getChildrenCount() + "\n");
                for (int i = 0; i < curOrder.getChildrenCount(); i++) {
                    MenuItem mc = curOrder.getChild(i);
                    bw.write(mc.getQuantity() + "," + mc.getCode() + "\n");
                }
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Load staff information from file.
     */
    private void loadStaff() {
        try {
            String fileName = "restaurant\\staff.txt";
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int numOfStaff = Integer.parseInt(br.readLine());
            for (int i = 0; i < numOfStaff; i++) {
                String name = br.readLine();
                String[] list = br.readLine().split(",");
                int id = Integer.parseInt(list[0]);
                String title = list[1];
                String gender = list[2];
                Staff curStaff = new Staff(name, id, title, gender);
                staff.put(id, curStaff);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save staff information to file.
     */
    private void saveStaff() {
        try {
            String filename = "restaurant\\staff.txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(staff.size() + "\n");
            for (Staff curStaff : staff.values()) {
                bw.write(curStaff.getName() + "\n");
                bw.write(curStaff.getId() + "," + curStaff.getTitle() + "," + curStaff.getGender() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save all relevant states to file.
     */
    public void saveAll() {
        saveMenuReference();
        saveMenu();
        saveOrder();
        saveStaff();
    }

    /**
     * Load historical orders form file, for sales report.
     */
    public void loadHistoryOrders() {
        String pathName = "restaurant\\historyOrders/";

        File file = new File(pathName);
        File[] fileList = file.listFiles();

        for (int i = 0; i < fileList.length; i++) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileList[i]));
                String l = br.readLine();
                String[] temp = l.split(",");
                LocalDateTime time = LocalDateTime.parse(temp[0]);
                int tableId = Integer.parseInt(temp[1]);
                int pax = Integer.parseInt(temp[2]);
                long contact = Long.parseLong(temp[3]);
                int staffId = Integer.parseInt(br.readLine());
                double totalPrice = Double.parseDouble(br.readLine());
                int numOfItems = Integer.parseInt(br.readLine());
                Reservation r = new Reservation(time, tableId, pax, contact);
                Order o = new Order(r, totalPrice);
                for (int j = 0; j < numOfItems; j++) {
                    String[] t = br.readLine().split(",");
                    int quantity = Integer.parseInt(t[0]);
                    int itemCode = Integer.parseInt(t[1]);
                    o.addItem(menuReference.get(itemCode), quantity);
                }
                historyOrders.add(o);
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update time of all orders.
     */
    public void updateTime() {
        for (Order o : activeOrders) {
            o.updateTime();
        }
    }


    public void printAllTables(){
        tableManager.printAllTables();
    }

    public void printAllReservations(){
        tableManager.printAllReservations();
    }

    public void printAllTables(LocalDateTime time){
        tableManager.printAllTables(time);
    }

    public ArrayList<Reservation> getReservation(long c){
        return tableManager.getReservation(c);
    }

    public void removeReservation(Reservation r){
        tableManager.removeReservation(r);
    }

    /**
     * Helper function for adding a new order.
     */
    public void makeOrder(int staffId, int tableId, int pax) {
        Staff s = staff.get(staffId);
        ArrayList<Reservation> reservationList = tableManager.getReservation(tableId);
        Reservation r;
        if (reservationList.size() == 0) {
            r = new Reservation(LocalDateTime.now(), tableId, pax, 0);
        } else
            r = reservationList.get(0);

        Order newOrder = new Order(r, s);
        activeOrders.add(newOrder);
    }

    /**
     * @param time The customer(s) requested time
     */
    // staff will check all the available table before makeReservation() is called
    public void makeReservation(LocalDateTime time, int tableId, int pax, long contact) {
        Reservation r = new Reservation(time, tableId, pax, contact);
        tableManager.addReservation(r);
    }

}
