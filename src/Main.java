import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Library{
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static int no_of_books;
    static int no_of_issued_books;
    Library(){
        no_of_books = 0;
        no_of_issued_books = 0;
    }
    void AddBook(Connection con,String Book_name,String Author_name){
        try{
            String q = "insert into library(bookName,authorName) values(?,?)";
            PreparedStatement pstmt = con.prepareStatement(q);
            pstmt.setString(1,Book_name);
            pstmt.setString(2,Author_name);
            pstmt.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(Book_name+" added in your library and author name is "+ Author_name);
        no_of_books++;
    }
    void ShowAvailableBooks(Statement stmt){
        try {
            String q  = "select * from library";
            ResultSet rs = stmt.executeQuery(q);
            System.out.println("Available books in your Library : ");
            while (rs.next()) {
                if (rs.getString(4) == null) {
                    no_of_books++;
                    int id = rs.getInt(1);
                    String book_Name = rs.getString(2);
                    String author_Name = rs.getString(3);
                    System.out.println(id + " | " + book_Name + " | " + author_Name);
                }
            }
            System.out.println("Total number of Books available in Library : "+no_of_books);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    void IssueBook(Connection con, Statement stmt, int id){
        try{
            String q2 = "select * from library";
            ResultSet rs = stmt.executeQuery(q2);
            while (rs.next()){
                if (rs.getString(4) == null && rs.getInt(1)==id) {
                    System.out.println("Enter your name :- ");
                    String name = br.readLine();
                    String q1 = "update library set personName = ? where bookId = ? ";
                    PreparedStatement pstmt = con.prepareStatement(q1);
                    pstmt.setString(1,name);
                    pstmt.setInt(2,id);
                    pstmt.executeUpdate();
                    LocalDateTime dt = LocalDateTime.now();
                    DateTimeFormatter dtb = DateTimeFormatter.ofPattern("dd-MM-yyyy E H:m:s a");
                    String myTime= dt.format(dtb);
                    System.out.println(" Book has been issued to "+name);
                    System.out.println("Book issued on : "+myTime);
                    no_of_books--;
                }
                if (rs.getString(4) != null && rs.getInt(1)==id) {
                    System.out.println("Book not available!!!");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    void showIssuedBooks(Statement stmt){
        try {
            String q  = "select * from library";
            ResultSet rs = stmt.executeQuery(q);
            System.out.println("Total number of issued books from Library : ");
            while (rs.next()) {
                if (rs.getString(4) != null) {
                    no_of_issued_books++;
                    int id = rs.getInt(1);
                    String book_Name = rs.getString(2);
                    String person_Name = rs.getString(4);
                    System.out.println(id + " | " + book_Name + " | " + person_Name);
                }
            }
            System.out.println("Total number of Books issued from Library : "+no_of_issued_books);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    void BookReturn(Connection con,Statement stmt, int id){
        try {
            String q = "select * from library";
            ResultSet rs = stmt.executeQuery(q);
            while (rs.next()){
                if (rs.getInt(1) == id && rs.getString(4) != null) {
                    String q1 = "update library set personName = ? where bookId = ? ";
                    String name = rs.getString(4);
                    PreparedStatement pstmt = con.prepareStatement(q1);
                    pstmt.setString(1,null);
                    pstmt.setInt(2,id);
                    pstmt.executeUpdate();
                    System.out.println("Book returned to the library and Book was issued to "+name);
                    no_of_books++;
                }
                if (rs.getInt(1) == id && rs.getString(4) == null) {
                    System.out.println("Book is not issued !!!");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    boolean isMore_Operation() throws IOException {
        System.out.println("Want more operation? Enter Y for Yes or N for No");
        char c = (char) br.read();
        br.readLine();
        return c == 'Y' || c == 'y';
    }
}

public class Main {
    public static void main(String[] args)  {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Library l = new Library();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarymanagement", "root", "Meinsane@123");
            Statement stmt = con.createStatement();
            boolean flag = true;
            do{
                System.out.println("Enter 1 for Add book\nEnter 2 for Book Issue\nEnter 3 for Show available books\nEnter 4 for Show issued books\nEnter 5 for book return");
                int input = Integer.parseInt(br.readLine());
                switch (input) {
                    case 1 -> {
                        System.out.println("Enter the book name :- ");
                        String book_name = br.readLine();
                        System.out.println("Enter the author name :- ");
                        String author_name = br.readLine();
                        l.AddBook(con, book_name, author_name);
                        flag = l.isMore_Operation();
                    }
                    case 2 -> {
                        System.out.println("Enter book id :- ");
                        int id = Integer.parseInt(br.readLine());
                        l.IssueBook(con, stmt, id);
                        flag = l.isMore_Operation();
                    }
                    case 3 -> {
                        l.ShowAvailableBooks(stmt);
                        flag = l.isMore_Operation();
                    }
                    case 4 -> {
                        l.showIssuedBooks(stmt);
                        flag = l.isMore_Operation();
                    }
                    case 5 -> {
                        System.out.println("Enter book id :- ");
                        int id = Integer.parseInt(br.readLine());
                        l.BookReturn(con, stmt, id);
                        flag = l.isMore_Operation();
                    }
                    default ->
                        System.out.println("Wrong input !!!! ");
                }
            }while (flag);
            System.out.println("Thank you !!!! ");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}