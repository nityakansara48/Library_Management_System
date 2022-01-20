import java.sql.*;
import java.io.*;
import java.util.Scanner;
import java.util.Iterator;
import java.time.*;
import java.text.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

public class cse_5330_project_2 {
    
    public static void main(String[] args) {
        try {  
            Connection cn = DB_Connect();
            int i=1;      
            while(true){          
                if(i==9){
                    break;
                }else{
                    System.out.println("1. Use Database.");
                    System.out.println("2. Create Tables.");
                    System.out.println("3. Insert Records.");
                    System.out.println("4. Print Tables.");
                    System.out.println("5. Generate Weekly Report.");
                    System.out.println("6. Add new Trasaction.");
                    System.out.println("7. Renew the Membership.");
                    System.out.println("8. Execute Triggers.");
                    System.out.println("9. Exit");
                    System.out.print("Enter Your Choice: ");            
                    Scanner scan = new Scanner(System.in);
                    i = scan.nextInt();
                    System.out.println("");
                    
                    switch(i) {                    

                        case 1:
                            Use_DB(cn);
                            break;
                            
                        case 2:
                            Create_Tables(cn);
                            break;
                         
                        case 3:
                            Insert_Data(cn);
                            break;
                            
                        case 4:
                            Display_data(cn);
                            break;
                        
                        case 5:
                            Weekly_report(cn);
                            break;

                        case 6:
                            New_Transaction(cn);
                            break;
                            
                        case 7:
                            Renew_membeship(cn);
                            break;
                            
                        case 8:
                            Execute_trigger(cn);
                            break;
        
                        case 9:
                            break;

                        default:
                            System.out.println("Invalid Choice.");
                            break;
                    }
                }
            }         
        }catch(Exception e) {            
            System.out.println("Main Exception: "+e);            
        }             
    }
    
    static Connection DB_Connect() {
        try {           
            String JDBC_Driver = "com.mysql.jdbc.Driver";
            String DB_URL = "jdbc:mysql://acadmysqldb001p/";
            String Username = "nxk9794";
            String Password = "Nhk@241098";        
            Class.forName(JDBC_Driver);            
            Connection cn = DriverManager.getConnection(DB_URL,Username,Password);        
            return cn;                 
        }catch(Exception e) {
            System.out.println("DB_Connect Function Exception:"+e);
        }
        return null;        
    }
    
    public static void Use_DB(Connection cn) {
        
        try {
            Statement st = cn.createStatement();        
            String Library_DB = "USE nxk9794";        
            st.executeUpdate(Library_DB);         
            st.close();            
            System.out.println("Database nxk9794 Selected.\n");
        }catch(Exception e) {            
            System.out.println("Create_DB Function Exception: "+e);
        }
    }
    
    public static void Create_Tables(Connection cn) {
        
        try {
            Statement st = cn.createStatement();        
            st.executeQuery("USE nxk9794");              
            String Lib_member = "CREATE TABLE Lib_member (Name varchar(50), SSN varchar(10), Campus_add varchar(100), Res_add varchar(100), Phone varchar(10), Lib_card_no varchar(20), Lib_card_expire date, Is_prof varchar(1), Is_member_active varchar(1), PRIMARY KEY(SSN));";
            String Lib_staff = "CREATE TABLE Lib_staff (Staff_id varchar(10), SSN varchar(10), Type varchar(40), PRIMARY KEY(Staff_id), FOREIGN KEY f1 (SSN) REFERENCES Lib_member(SSN));";
            String Book_author = "CREATE TABLE Book_author (Author_id varchar(10), Author_name varchar(50), PRIMARY KEY (Author_id));";
            String Books = "CREATE TABLE Books (ISBN varchar(13), Title varchar(50), Author_id varchar(50), Sub_area varchar(50), Description varchar(500), Book_type Varchar(20), Is_lendable varchar(1), Language varchar(20), Binding varchar(20), Edition varchar(20), PRIMARY KEY (ISBN), FOREIGN KEY f2 (Author_id) REFERENCES Book_author(Author_id));";
            String Book_issue = "CREATE TABLE Book_issue (Issue_id varchar(10), SSN varchar(10), Staff_id varchar(10), ISBN varchar(13), Issue_date date, Due_date date, Notice_date date, Is_returned varchar(1), PRIMARY KEY (Issue_id), FOREIGN KEY f3 (SSN) REFERENCES Lib_member(SSN), FOREIGN KEY f4 (Staff_id) REFERENCES Lib_staff(Staff_id), FOREIGN KEY f5 (ISBN) REFERENCES Books(ISBN));";
            String Book_availale = "CREATE TABLE Book_available (ISBN varchar(13), Total_copies varchar(10), Available_copies varchar(10), FOREIGN KEY f6 (ISBN) REFERENCES Books(ISBN));";
            String Book_require = "CREATE TABLE Book_require (ISBN varchar(13), Total_book_required varchar(10), FOREIGN KEY f7 (ISBN) REFERENCES Books(ISBN));";
            st.executeUpdate(Lib_member);
            System.out.println("Lib_member table Created.");
            st.executeUpdate(Lib_staff);
            System.out.println("Lib_staff table Created.");
            st.executeUpdate(Book_author);
            System.out.println("Book_author table Created.");
            st.executeUpdate(Books);
            System.out.println("Books table Created.");
            st.executeUpdate(Book_issue);
            System.out.println("Book_issue table Created.");
            st.executeUpdate(Book_availale);
            System.out.println("Book_available table Created.");
            st.executeUpdate(Book_require);
            System.out.println("Book_require table Created.\n");
            st.close();
        }catch(SQLException e) {
            System.out.println("Create_Tables Function Exception: "+e);
        }
    }
    
    public static void Insert_Data(Connection cn) {
        
        try {
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");
            String sql = "INSERT INTO Lib_member values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            FileInputStream fis = new FileInputStream("datafiles\\lib_member.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    }                  
                }
                ps.execute();
            }
            System.out.println("Inserted records to Lib_member.");

            sql = "INSERT INTO Lib_staff values(?,?,?);";
            ps = cn.prepareStatement(sql);            
            fis =  new FileInputStream("datafiles\\lib_staff.xlsx");            
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);            
            itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    }   
                }
                ps.execute();
            }
            System.out.println("Inserted records to Lib_staff.");
            
            sql = "INSERT INTO Book_author values(?,?);";
            ps = cn.prepareStatement(sql);            
            fis =  new FileInputStream("datafiles\\book_author.xlsx");            
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);            
            itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    } 
                }
                ps.execute();
            }
            System.out.println("Inserted records to Book_author.");
            
            sql = "INSERT INTO Books values(?,?,?,?,?,?,?,?,?,?);";
            ps = cn.prepareStatement(sql);            
            fis =  new FileInputStream("datafiles\\books.xlsx");            
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);            
            itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    ps.setString(colIndex+1,cell.getStringCellValue());
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    } 
                }
                ps.execute();
            }
            System.out.println("Inserted records to Books.");
            
            sql = "INSERT INTO Book_available values(?,?,?);";
            ps = cn.prepareStatement(sql);            
            fis =  new FileInputStream("datafiles\\book_available.xlsx");            
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);            
            itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    } 
                }
                ps.execute();
            }
            System.out.println("Inserted records to Book_available.");
            
            sql = "INSERT INTO Book_issue values(?,?,?,?,?,?,?,?);";
            ps = cn.prepareStatement(sql);            
            fis =  new FileInputStream("datafiles\\book_issue.xlsx");            
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);            
            itr = sheet.iterator();
            itr.next();
            
            while(itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    } 
                }
                ps.execute();
            }
            System.out.println("Inserted records to Book_issue.");
            
            sql = "INSERT INTO Book_require values(?,?);";
            ps = cn.prepareStatement(sql);            
            fis =  new FileInputStream("datafiles\\book_require.xlsx");            
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheetAt(0);            
            itr = sheet.iterator();
            itr.next();
            while(itr.hasNext()){
                Row row = itr.next();
                Iterator<Cell> itc = row.cellIterator();
                while(itc.hasNext()){
                    Cell cell = itc.next();
                    int colIndex = cell.getColumnIndex();
                    if("STRING".equals(cell.getCellTypeEnum().toString())){
                        ps.setString(colIndex+1,cell.getStringCellValue());
                    }else{
                        ps.setString(colIndex+1,formatter.format(cell.getDateCellValue()));
                    } 
                }
                ps.execute();
            }
            st.close();
            System.out.println("Inserted records to Book_require.\n");
        }catch(Exception e){
            System.out.println("Insert_Data Function Exception: "+e);
        }
    }
    
    public static void gap(int a,int b) {
        
        for (int i=b;i<=a;i++){
            System.out.print(" ");
        }
    }
    
    public static void Display_data(Connection cn) {
        
        try {
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");
            
            System.out.println("Which table do you want to print?");
            System.out.println("1. Lib_member");
            System.out.println("2. Lib_staff");
            System.out.println("3. Book_author");
            System.out.println("4. Books");
            System.out.println("5. Book_available");
            System.out.println("6. Book_issue");
            System.out.println("7. Book_require");
            System.out.print("Enter Your Choice: ");
            Scanner sc = new Scanner(System.in);
            int i = sc.nextInt();
            ResultSet rs = st.executeQuery("SELECT * from Lib_member");
            
            switch(i) {
            
                case 1:
                    rs = st.executeQuery("SELECT * from Lib_member");
                    System.out.print("\nTable: Lib_member\n\n");  
                    System.out.print("Name ");   gap(20,4);
                    System.out.print("SSN ");   gap(15,3);
                    System.out.print("Campus_add ");   gap(50,10);
                    System.out.print("Res_add ");   gap(50,7);
                    System.out.print("Phone ");   gap(15,5);
                    System.out.print("Lib_card_no ");   gap(15,11);
                    System.out.print("Lib_card_expire ");   gap(15,15);
                    System.out.print("Is_prof ");   gap(15,7);
                    System.out.print("Is_member_active \n");   
                    while(rs.next()) {

                        System.out.print(rs.getString(1)+" ");  gap(20, rs.getString(1).length());
                        System.out.print(rs.getString(2)+" ");  gap(15, rs.getString(2).length());
                        System.out.print(rs.getString(3)+" ");  gap(50, rs.getString(3).length());
                        System.out.print(rs.getString(4)+" ");  gap(50, rs.getString(4).length());
                        System.out.print(rs.getString(5)+" ");  gap(15, rs.getString(5).length());
                        System.out.print(rs.getString(6)+" ");  gap(15, rs.getString(6).length());
                        System.out.print(rs.getString(7)+" ");  gap(15, rs.getString(7).length());
                        System.out.print(rs.getString(8)+" ");  gap(15, rs.getString(8).length());
                        System.out.print(rs.getString(9)+"\n"); 
                    }
                break;
                
                case 2:
                    rs = st.executeQuery("SELECT * from Lib_staff");
                    System.out.print("\n\nTable: Lib_staff\n\n");
                    System.out.print("Staff_id "); gap(12,8);
                    System.out.print("SSN "); gap(12,3);
                    System.out.print("Type \n"); 
                    while(rs.next()){
                        System.out.print(rs.getString(1)+" ");  gap(12, rs.getString(1).length());
                        System.out.print(rs.getString(2)+" ");  gap(12, rs.getString(2).length());
                        System.out.print(rs.getString(3)+" \n");  
                    }
                break;

                case 3:
                    rs = st.executeQuery("SELECT * from Book_author");
                    System.out.print("\n\nTable: Book_author\n\n");
                    System.out.print("Author_id "); gap(10,9);
                    System.out.print("Author_name \n"); 
                    while(rs.next()){
                        System.out.print(rs.getString(1)+" ");  gap(10, rs.getString(1).length());
                        System.out.print(rs.getString(2)+" \n");  
                    }
                break;
                
                case 4:
                    rs = st.executeQuery("SELECT * from Books");
                    System.out.print("\n\nTable: Books\n\n");
                    System.out.print("ISBN "); gap(15,4);
                    System.out.print("Book Title "); gap(40,7);
                    System.out.print("Author_id "); gap(15,9);
                    System.out.print("Subject Area "); gap(50,12);
                    System.out.print("Description "); gap(250,11);
                    System.out.print("Book_type "); gap(12,9);
                    System.out.print("Is_lendable "); gap(12,11);
                    System.out.print("Language "); gap(12,8);
                    System.out.print("Binding "); gap(12,7);
                    System.out.print("Edition \n"); 
                    while(rs.next()){
                        System.out.print(rs.getString(1)+" ");  gap(15, rs.getString(1).length());
                        System.out.print(rs.getString(2)+" ");  gap(40, rs.getString(2).length());
                        System.out.print(rs.getString(3)+" ");  gap(15, rs.getString(3).length());
                        System.out.print(rs.getString(4)+" ");  gap(50, rs.getString(4).length());
                        System.out.print(rs.getString(5)+" ");  gap(250, rs.getString(5).length());
                        System.out.print(rs.getString(6)+" ");  gap(12, rs.getString(6).length());
                        System.out.print(rs.getString(7)+" ");  gap(12, rs.getString(7).length());
                        System.out.print(rs.getString(8)+" ");  gap(12, rs.getString(8).length());
                        System.out.print(rs.getString(9)+" ");  gap(12, rs.getString(9).length());
                        System.out.print(rs.getString(10)+" \n");  
                    }
                break;

                case 5:
                    rs = st.executeQuery("SELECT * from Book_available");
                    System.out.print("\n\nTable: Book_available\n\n");
                    System.out.print("ISBN "); gap(15,4);
                    System.out.print("Total_copies "); gap(15,12);
                    System.out.print("Available_copies \n"); 
                    while(rs.next()){
                        System.out.print(rs.getString(1)+" ");  gap(15, rs.getString(1).length());
                        System.out.print(rs.getString(2)+" ");  gap(15, rs.getString(2).length());
                        System.out.print(rs.getString(3)+" \n");  
                    }
                break;

                case 6:
                    rs = st.executeQuery("SELECT * from Book_issue");
                    System.out.print("\n\nTable: Book_issue\n\n");
                    System.out.print("Issue_id "); gap(10,8);
                    System.out.print("SSN ");   gap(15,3);
                    System.out.print("Staff_id "); gap(10,8);
                    System.out.print("ISBN "); gap(15,4);
                    System.out.print("Issue_date "); gap(12,10);
                    System.out.print("Due_date "); gap(12,8);
                    System.out.print("Notice_date "); gap(12,11);
                    System.out.print("Is_returned \n"); 
                    while(rs.next()){
                        System.out.print(rs.getString(1)+" ");  gap(10, rs.getString(1).length());
                        System.out.print(rs.getString(2)+" ");  gap(15, rs.getString(2).length());
                        System.out.print(rs.getString(3)+" ");  gap(10, rs.getString(3).length());
                        System.out.print(rs.getString(4)+" ");  gap(15, rs.getString(4).length());
                        System.out.print(rs.getString(5)+" ");  gap(12, rs.getString(5).length());
                        System.out.print(rs.getString(6)+" ");  gap(12, rs.getString(6).length());
                        System.out.print(rs.getString(7)+" ");  gap(12, rs.getString(7).length());
                        System.out.print(rs.getString(8)+" \n");  
                    }
                break;
                
                
                case 7:
                rs = st.executeQuery("SELECT * from Book_require");
                System.out.print("\n\nTable: Book_require\n\n");
                System.out.print("ISBN "); gap(15,4);
                System.out.print("Total_book_require \n"); 
                while(rs.next()){
                    System.out.print(rs.getString(1)+" ");  gap(15, rs.getString(1).length());
                    System.out.print(rs.getString(2)+" \n");  
                }
                break;
                
                default:
                    System.out.println("Invalid Choice!");
                break;
            }
            st.close();
            System.out.println("\n");
        }catch(SQLException e) {
            System.out.println("Display_data function Exception: "+e);
        }
    }
    
    public static void Weekly_report(Connection cn) {
        
        try {
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");
            System.out.print("\nWeekly Report of Library: \n\n");
            ResultSet rs = st.executeQuery("SELECT A.ISBN,A.Title,count(B.ISBN) AS No_of_copies,week(B.Issue_date) AS Week_No,SUM(datediff(B.Notice_date,B.Issue_date)) AS No_of_Days_Loaned_Out,A.Sub_area,C.Author_name from Books as A INNER JOIN Book_issue as B ON A.ISBN=B.ISBN INNER JOIN Book_author as C ON A.Author_id=C.Author_id GROUP BY A.Sub_area,A.Author_id,B.ISBN,week(B.Issue_date)");
            System.out.print("ISBN "); gap(15,4);
            System.out.print("Book Title "); gap(40,10);
            System.out.print("No._of_copies "); gap(14,13);
            System.out.print("Week_No. "); gap(9,8);
            System.out.print("No_of_Days_Loaned_Out "); gap(22,21);
            System.out.print("Subject Area "); gap(40,12);
            System.out.println("Author Name ");
            while(rs.next()){
                System.out.print(rs.getString(1)+" ");  gap(15, rs.getString(1).length());
                System.out.print(rs.getString(2)+" ");  gap(40, rs.getString(2).length());
                System.out.print(rs.getString(3)+" ");  gap(14, rs.getString(3).length());
                System.out.print(rs.getString(4)+" ");  gap(9, rs.getString(4).length());
                System.out.print(rs.getString(5)+" ");  gap(22, rs.getString(5).length());
                System.out.print(rs.getString(6)+" ");  gap(40, rs.getString(6).length());
                System.out.print(rs.getString(7)+" \n");  
            }
            st.close();
            System.out.println("\n");
        }catch(SQLException e) {
            System.out.println("Weekly_report function Exception: "+e);
        }
    }
        
    public static void New_Transaction(Connection cn) {
         
        try {
            System.out.println("Which transaction do you want to perform?");
            System.out.println("1. Add New Member.");
            System.out.println("2. Add New Book.");
            System.out.println("3. To Borrow a Book.");
            System.out.println("4. To Return a Book.");
            System.out.print("Enter your choice: ");
            Scanner sc = new Scanner(System.in);
            int i = sc.nextInt();

            switch(i){

                case 1:
                    New_Member(cn);
                    break;

               case 2:
                   New_book(cn);
                   break;

               case 3:
                   New_borrow(cn);
                   break;

               case 4:
                   Book_return(cn);
                   break;

               default:
                   System.out.println("Invalid Choice.");
                   break;
            }
        }catch(Exception e) {
            System.out.println("New_Transaction function Exception:" +e);
        }         
    }
    
    public static void New_Member(Connection cn){
        
        try {
            System.out.println("\nEnter Details of New Member.");
            Scanner sc = new Scanner(System.in);
            System.out.print("\nEnter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter SSN: ");
            String ssn = sc.nextLine();
            System.out.print("Enter Campus Address: ");
            String camp_add = sc.nextLine();
            System.out.print("Enter Resident Address: ");
            String res_add = sc.nextLine();
            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();
            System.out.print("Enter Library Card No.: ");
            String lib_card_no = sc.nextLine();
            System.out.print("Enter Library Card Expire Date(yyyy-mm-dd): ");
            String lib_card_expire = sc.nextLine();
            System.out.print("Is Professor(0/1)? ");
            String is_prof = sc.nextLine();
            System.out.print("Is Membe Active(0/1)? ");
            String is_mem_active = sc.nextLine();
            
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");
            String sql = "INSERT INTO Lib_member values (?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1,name);
            ps.setString(2,ssn);
            ps.setString(3,camp_add);
            ps.setString(4,res_add);
            ps.setString(5,phone);
            ps.setString(6,lib_card_no);
            ps.setString(7,lib_card_expire);
            ps.setString(8,is_prof);
            ps.setString(9,is_mem_active);            
            ps.execute();
            st.close();
            System.out.println("\nNew Member Added Successfully.\n");
        }catch (SQLException e) {
            System.out.println("New_Member function Exception: "+e);
        }
    }
    
    public static void New_book(Connection cn) {
        
        try {
            System.out.println("\nEnter Details of New Book.");
            Scanner sc = new Scanner(System.in);
            System.out.print("\nEnter ISBN: ");
            String isbn = sc.nextLine();
            System.out.print("Enter Tile: ");
            String tile = sc.nextLine();
            System.out.print("Enter Author ID: ");
            String author_id = sc.nextLine();
            System.out.print("Enter Subject Area: ");
            String sub_area = sc.nextLine();
            System.out.print("Enter Description: ");
            String desc = sc.nextLine();
            System.out.print("Enter Book Type: ");
            String book_type = sc.nextLine();
            System.out.print("Is Lendable(0/1)?: ");
            String len = sc.nextLine();
            System.out.print("Enter Language: ");
            String lan = sc.nextLine();
            System.out.print("Enter Binding: ");
            String bind = sc.nextLine();
            System.out.print("Enter Edition: ");
            String edition = sc.nextLine();
            
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");
            String sql = "INSERT INTO Books values (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1,isbn);
            ps.setString(2,tile);
            ps.setString(3,author_id);
            ps.setString(4,sub_area);
            ps.setString(5,desc);
            ps.setString(6,book_type);
            ps.setString(7,len);
            ps.setString(8,lan);
            ps.setString(9,bind);
            ps.setString(10,edition);            
            ps.execute();
            st.close();
            System.out.println("\nNew Book Added Successfully.\n");
        }catch(SQLException e) {
            System.out.println("New_book function Exception: "+e);
        }
    }
    
    public static void New_borrow(Connection cn){
        
        try {
            System.out.println("\nEnter Details to Borrow a Book.");
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");
            System.out.print("\nEnter Your SSN: ");
            Scanner sc = new Scanner(System.in);
            String ssn = sc.nextLine();            
            ResultSet rs = st.executeQuery("SELECT * FROM Lib_member WHERE SSN="+ssn);
            if(rs.next()==false){
                System.out.println("Sorry! You are not a member of Library or Invalid SSN.\n");
            }else { 
                System.out.print("Enter Book ISBN: ");
                String s = sc.nextLine();
                rs = st.executeQuery("select * from Book_available where ISBN="+s+" AND Available_copies<>0");
                if(rs.next()==false) {
                    System.out.println("Sorry! Book is not Available or Invalid ISBN.\n");
                }else {
                    String t,u;
                    int t1=0; int u1=0;
                    rs = st.executeQuery("SELECT Available_copies from Book_available where ISBN="+s);
                    while(rs.next()) {
                        t = rs.getString(1);
                        t1 = Integer.parseInt(t);
                        t1=t1-1;
                    }            
                    LocalDate today = LocalDate.now();
                    LocalDate today_23 = today.plusDays(23);
                    LocalDate today_23_7 = today_23.plusDays(7);
                    LocalDate today_3_month = today.plusMonths(3);
                    LocalDate today_3_month_7 = today_3_month.plusDays(7);
                    rs = st.executeQuery("SELECT Is_prof from Lib_member where SSN="+ssn+" AND Is_member_active=1");
                    if(rs.next()==false){
                        System.out.println("Sorry! You are not active member of library.\n");
                    }else{ 
                        rs = st.executeQuery("SELECT Is_prof from Lib_member where SSN="+ssn+" AND Is_member_active=1");
                        while(rs.next()){
                            u = rs.getString(1);
                            u1 = Integer.parseInt(u);
                        }                    
                        rs = st.executeQuery("select * from Book_issue");
                        int cnt = 0;
                        while(rs.next()) {
                            cnt++;
                        }cnt++;
                        if(u1==1){
                            st.executeUpdate("INSERT INTO Book_issue values ("+String.valueOf(cnt)+","+ssn+",5,"+s+",'"+String.valueOf(today)+"','"+String.valueOf(today_3_month)+"','"+String.valueOf(today_3_month_7)+"',0)");
                            st.executeUpdate("UPDATE Book_available SET Available_copies="+String.valueOf(t1)+" WHERE ISBN="+s);
                            System.out.println("Book Borrowed Successfully.\n");
                        }else{
                            st.executeUpdate("INSERT INTO Book_issue values ("+String.valueOf(cnt)+","+ssn+",5,"+s+",'"+String.valueOf(today)+"','"+String.valueOf(today_23)+"','"+String.valueOf(today_23_7)+"',0)");
                            st.executeUpdate("UPDATE Book_available SET Available_copies="+String.valueOf(t1)+" WHERE ISBN="+s);
                            System.out.println("Book Borrowed Successfully.\n");
                        }
                    }
                }               
            } 
            st.close();
        }catch(Exception e){
            System.out.println("New_borrow function Exception: "+e);
        }
    }
    
    public static void Book_return(Connection cn) {
        
        try {
            System.out.println("\nEnter Details to Return a Book.");
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");            
            Scanner sc = new Scanner(System.in);
            System.out.print("\nEnter Your SSN: ");
            String ssn = sc.nextLine();
            System.out.print("Enter Book ISBN: ");
            String s = sc.nextLine();
            
            ResultSet rs = st.executeQuery("SELECT * from Book_issue where SSN="+ssn+" AND ISBN="+s+" AND Is_returned=0 LIMIT 1");
            if(rs.next()==false){
                System.out.println("You did not borrow any book.\n");
            }else { 
                st.executeUpdate("UPDATE Book_issue SET Is_returned=1 WHERE SSN="+ssn+" AND ISBN="+s);                
                rs = st.executeQuery("SELECT Available_copies from Book_available where ISBN="+s);
                String t;
                int t1=0;
                while(rs.next()) {
                    t = rs.getString(1);
                    t1 = Integer.parseInt(t);
                    t1=t1+1;
                }
                st.executeUpdate("UPDATE Book_issue SET Is_returned=1 WHERE SSN="+ssn+" AND ISBN="+s);
                st.executeUpdate("UPDATE Book_available SET Available_copies="+String.valueOf(t1)+" WHERE ISBN="+s);
                System.out.println("Book Returned Successfully.");
                rs = st.executeQuery("SELECT B.Title,DATEDIFF(CURDATE(),A.Issue_date) AS Total_Borrow_Days,A.Issue_Date,CURDATE() AS Return_Date FROM Book_issue A INNER JOIN Books B ON A.ISBN=B.ISBN WHERE A.SSN="+ssn+" AND A.ISBN="+s+" LIMIT 1");
                System.out.print("\nBook Return Receipt:\n");
                System.out.print("Title "); gap(40,5);
                System.out.print("Total_Borrow_Days "); gap(22,20);
                System.out.print("Issue_Date "); gap(12,10);
                System.out.print("Return_Date \n"); 
                while(rs.next()){
                    System.out.print(rs.getString(1)+" ");  gap(40, rs.getString(1).length());
                    System.out.print(rs.getString(2)+" ");  gap(22, rs.getString(2).length());
                    System.out.print(rs.getString(3)+" ");  gap(12, rs.getString(3).length());
                    System.out.print(rs.getString(4)+" \n");  
                }
                System.out.println("\n");
            }
            st.close();
        }catch(Exception e) {
            System.out.println("Book_return function Exception: "+e);
        }
    }
    
    public static void Renew_membeship(Connection cn) {
        
        try {
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");            
            Scanner sc = new Scanner(System.in);
            System.out.print("\nEnter Your SSN: ");
            String ssn = sc.nextLine();
            
            ResultSet rs = st.executeQuery("SELECT * from Lib_member where SSN="+ssn);
            if(rs.next()==false){
                System.out.println("Sorry! You are not a member of Library or Invalid SSN.\n");
            }else {
                LocalDate today = LocalDate.now();
                LocalDate today__6_months = today.plusMonths(6);    
                st.executeUpdate("UPDATE Lib_member SET Lib_card_expire='"+today__6_months+"', Is_member_active=1 WHERE SSN="+ssn);
                System.out.println("Your membership is renewed and extended by 6 months.\n");
            }
            st.close();
        }catch(SQLException e) {
            System.out.println("Renew_membership function Exception: "+e);
        }
    }    
    
    public static void Execute_trigger(Connection cn) {
        
       try {
            Statement st = cn.createStatement();
            st.executeQuery("USE nxk9794");           
                        
            ResultSet rs = st.executeQuery("SELECT * FROM Book_overdue");
            System.out.println("Trigger-1: Book Overdue Details:");
            System.out.print("No. ");   gap(5,3);
            System.out.print("SSN ");   gap(13,3);
            System.out.print("ISBN "); gap(15,4);
            System.out.print("Notice Date \n");
            while(rs.next()){
                System.out.print(rs.getString(1)+" ");  gap(5, rs.getString(1).length());
                System.out.print(rs.getString(2)+" ");  gap(13, rs.getString(2).length());
                System.out.print(rs.getString(3)+" ");  gap(15, rs.getString(3).length());
                System.out.print(rs.getString(4)+" \n");  
            }
            
            rs = st.executeQuery("SELECT * FROM Membership_renew");
            System.out.println("\nTrigger-2: Membership Renewal Details:");
            System.out.print("No. ");   gap(5,3);
            System.out.print("SSN ");   gap(13,3);
            System.out.print("Name \n");
            while(rs.next()){
                System.out.print(rs.getString(1)+" ");  gap(5, rs.getString(1).length());
                System.out.print(rs.getString(2)+" ");  gap(13, rs.getString(2).length());
                System.out.print(rs.getString(3)+" \n");
            }
            st.close();
            System.out.println("\n");
        }catch(SQLException e) {
            System.out.println("Renew_membership function Exception: "+e);
        }
    }
}