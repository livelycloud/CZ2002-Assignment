package restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InputHandler {

    /**
     * Date formatter used across all subroutines.
     */
    DateTimeFormatter dt_formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    Scanner sc = new Scanner(System.in);

    public int getInt(){
        String s = sc.nextLine();
        try {
            return Integer.parseInt(s);
        } catch (Exception e){
            return getIntAgain();
        }
    }

    private int getIntAgain(){
        System.out.println("Please enter a valid integer: ");
        String s = sc.nextLine();
        try {
            return Integer.parseInt(s);
        } catch (Exception e){
            return getIntAgain();
        }
    }
    public double getDouble(){
        String s = sc.nextLine();
        try {
            return Double.parseDouble(s);
        } catch (Exception e){
            return getDoubleAgain();
        }
    }

    private double getDoubleAgain(){
        System.out.println("Please enter a valid double: ");
        String s = sc.nextLine();
        try {
            return Double.parseDouble(s);
        } catch (Exception e){
            return getDoubleAgain();
        }
    }

    public String nextLine(){
        return sc.nextLine();
    }

    public LocalDate getDate(){
        String s = sc.nextLine();
        try {
            return LocalDate.parse(s, dt_formatter);
        } catch (Exception e){
            return getDateAgain();
        }
    }

    private LocalDate getDateAgain(){
        System.out.println("Please enter a valid date (format:dd/mm/yyyy): ");
        String s = sc.nextLine();
        try {
            return LocalDate.parse(s, dt_formatter);
        } catch (Exception e){
            return getDateAgain();
        }
    }

    public LocalTime getTime(){
        String s = sc.nextLine();
        try {
            return LocalTime.parse(s);
        } catch (Exception e){
            return getTimeAgain();
        }
    }

    private LocalTime getTimeAgain(){
        System.out.println("Please enter a valid time (format: 13:00): ");
        String s = sc.nextLine();
        try {
            return LocalTime.parse(s);
        } catch (Exception e){
            return getTimeAgain();
        }
    }

    public long getLong(){
        String s = sc.nextLine();
        try {
            return Long.parseLong(s);
        } catch (Exception e){
            return getLongAgain();
        }
    }

    private long getLongAgain(){
        System.out.println("Please enter a valid long: ");
        String s = sc.nextLine();
        try {
            return Long.parseLong(s);
        } catch (Exception e){
            return getLongAgain();
        }
    }

}
