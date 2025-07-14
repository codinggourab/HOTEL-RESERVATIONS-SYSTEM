import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem {

    public static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    public static final String username = "root";
    public static final String password = "gourab";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a Room:");
                System.out.println("2. Get Room Number:");
                System.out.println("3. View Reservation Details:");
                System.out.println("4. Update Reservation Details:");
                System.out.println("5. Delete Reservation Details:");
                System.out.println("6. Exit:");
                System.out.print("Choose an option:");
                int choice = sc.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        getRoomNumber(con, sc);
                        break;
                    case 3:
                        viewReservations(con);
                        break;
                    case 4:
                        updateReservation(con, sc);
                        break;
                    case 5:
                        deleteReservation(con, sc);
                        break;
                    case 6:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again");

                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection con, Scanner scanner){
        try{
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try(Statement stmt = con.createStatement()){
                int affectedRows = stmt.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation Successful");
                }else{
                    System.out.println("Reservation failed");
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection con) throws SQLException{
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try(Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            System.out.println("Current Reservaations: ");
            System.out.println("+----------------+----------------+----------------+----------------+-------------------------+");
            System.out.println("| Reservation ID |   Guest Name   |   Room Number  | Contact Number |   Reservation Date");
            System.out.println("+----------------+----------------+----------------+----------------+-------------------------+");

            while(rs.next()){
                int reservationID = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getString("reservation_date").toString();

                // Format and dispaly the reservation date in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s |\n",
                        reservationID, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+----------------+----------------+----------------+----------------+");
        }
    }

    private static void getRoomNumber(Connection con, Scanner scanner){
        try{
            System.out.println("Enter Reservation ID: ");
            int reservationID = scanner.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();
            String sql = "SELECT room_number FROM reservations WHERE reservation_id = " + reservationID + "AND guest_name = '" + guestName + "'";
            try(Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){

                if(rs.next()){
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room Number for Reservation ID: " + reservationID + " and guest " + guestName + " is " + roomNumber);
                }else {
                    System.out.println("Reservtion Not found for the given reservation ID nd guest name");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection con, Scanner scanner){
        try{
            System.out.println("Enter reservation ID to update: ");
            int reservationID = scanner.nextInt();
            scanner.nextLine();

            if(!reservationExists(con, reservationID)){
                System.out.println("Reservation does not exist for the given ID");
                return;
            }
            System.out.println("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.nextLine();
            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationID;

            try(Statement stmt = con.createStatement()){
                int affectedRows = stmt.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation updated Successfully");
                }else{
                    System.out.println("Reservation Update failed");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection con, Scanner scanner){
        try{
            System.out.println("Enter reservation ID to delete: ");
            int reservationID = scanner.nextInt();

            if(!reservationExists(con, reservationID)){
                System.out.println("Reservation does not exist for the given ID");
                return;
            }

            String sql = "DELETE FROM RESERVATIONS WHERE reservation_id = " + reservationID;
            try(Statement stmt = con.createStatement()){
                int affectedRows = stmt.executeUpdate(sql);
                if(affectedRows > 0){
                    System.out.println("Reservation updated Successfully");
                }else{
                    System.out.println("Reservation Update failed");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationID){
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationID;

            try(Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){
                return rs.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;   // handel databse error
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting system");
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank Yoy for using Hotel Reservation System");
    }
}

