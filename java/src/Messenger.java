/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;
   public static String curr_user=null;
public static String curr_phone=null;
public static String curr_pwd=null;
public static String curr_status=null;
public static String curr_block=null;
public static String curr_contact=null;
public static String curr_chat="-1";
public static String chat_sender=null;


   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */



   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public ResultSet getResultSet (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      //stmt.close();
      return rs;
   }
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            curr_user = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: curr_user = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (curr_user != null) {
              boolean usermenu = true;
	     
              while(usermenu) {
                System.out.println("USER MENU");
                System.out.println("---------");
                System.out.println("1. Add to contact list");
		System.out.println("2. Add to block list");
		System.out.println("3. Delete from contact list");
		System.out.println("4. Delete from block list");
                System.out.println("5. Browse contact list");
		System.out.println("6. Browse block list");
		System.out.println("7. View chats/Start new chat");
                //System.out.println("7. Write a new message");
                System.out.println("8. Read notification list");
		System.out.println("9. Delete account");
                System.out.println(".........................");
                System.out.println("10. Log out");
                switch (readChoice()){
		   case 10: usermenu = false; break;
                   case 1: AddToContact(esql); break;
   		   case 2: AddToBlock(esql); break;
                   case 3: DeleteContact(esql); break;
                   case 4: DeleteBlock(esql); break;
		   case 5: ListContacts(esql); break;
		   case 6: ListBlocks(esql); break;
                   case 7: 
			boolean chatmenu = true;
	     
              	while(chatmenu) {
                	System.out.println("CHAT MENU");
                	System.out.println("---------");
                	System.out.println("1. Start new chat");
			System.out.println("2. List chats");
			System.out.println("3. Go back");

        		switch (readChoice()){
                   	case 1: StartChat(esql); break;
   		   	case 2: curr_chat = ListChats(esql); break;
                   	case 3: chatmenu=false; curr_chat="-1"; break;
         
				}
			if(!curr_chat.equals("-1"))
			{
			  	boolean list_chat_menu=true;
			 	while(list_chat_menu)
				{
String query = String.format("SELECT msg_id, sender_login, msg_timestamp, msg_text  FROM MESSAGE WHERE chat_id=%s ORDER BY msg_timestamp DESC" ,curr_chat);

ResultSet rs = esql.getResultSet(query);
/*ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

	    for(int i = 1; i <= numCol; ++i)
		System.out.print(rsmd.getColumnName(i) + "\t");
	    System.out.println();
	  
	for (int i=0; rs.next() && i<10; ++i){
		for (int j=1; i<=numCol; ++j)
            		System.out.print (rs.getString (j) + "\t");
         	System.out.println ();
}*/

ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next() && rowCount<10){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
	 if(rowCount==10)break;
      }//end while
	
        



     
					//esql.executeQueryAndPrintResult(query);
					System.out.println("LIST CHAT MENU");
               				System.out.println("---------");
                			System.out.println("1. Create new message");
					System.out.println("2. Edit message");
					System.out.println("3. Delete message");
					System.out.println("4. Load earlier messages");
					System.out.println("5. Add new member");
					System.out.println("6. Delete member");
					System.out.println("7. Delete chat");
					System.out.println("8. Go back");

				 	switch (readChoice()){
                   			case 8: list_chat_menu=false; break;
   		   			case 1: NewMessage(esql); break;
                   			case 2: EditMessage(esql); break;
					case 3: DeleteMessage(esql); break;
					case 4: 

	rowCount = 0;

      // iterates through the result set and output them to standard out.
      outputHeader = true;
      while (rs.next() && rowCount<10){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
	if(rowCount==10)break;
      }//end while 
		break;
					case 5: AddMember(esql); break;
   		   			case 6: DeleteMember(esql); break;
                   			case 7: DeleteChat(esql); break;
					}
         
				}
			}
					
		}break;
		   case 8: ReadNotifications(esql); break;
		   case 9: DeleteAccount(esql); usermenu=false;break;
                   
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }//end if
         }//end while keepon
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

	 //Creating empty contact\block lists for a user
	 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
	 int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
	 int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
	 String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);

	 if (userNum > 0){
ResultSet rs = esql.getResultSet(query);
rs.next();
curr_phone=rs.getString(2);
curr_pwd=rs.getString(3);
curr_status=rs.getString(4);
curr_block=rs.getString(5);
curr_contact=rs.getString(6);
		return login;
}
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static void AddToContact(Messenger esql){
try{
System.out.print("\tEnter contact id: ");
String id = in.readLine();
String query = String.format("SELECT * FROM Usr WHERE login = '%s'", id);
int success=esql.executeQuery(query);
String query2 = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'",curr_contact, id);
int success2=esql.executeQuery(query2);
if(success==0)
System.out.println("\tInvalid id!");
else if(success2!=0)
System.out.println("\tThis user is already on your contact list!");
else{
query = String.format("INSERT INTO USER_LIST_CONTAINS VALUES(%s,'%s')", curr_contact, id);
esql.executeUpdate(query);
System.out.println("\tUser added!");
}

}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static void AddToBlock(Messenger esql){
try{
System.out.print("\tEnter block id: ");
String id = in.readLine();
String query = String.format("SELECT * FROM Usr WHERE login = '%s'", id);
int success=esql.executeQuery(query);
String query2 = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'",curr_block, id);
int success2=esql.executeQuery(query2);
if(success==0)
System.out.println("\tInvalid id!");
else if(success2!=0)
System.out.println("\tThis user is already on your block list!");
else{
query = String.format("INSERT INTO USER_LIST_CONTAINS VALUES(%s,'%s')", curr_block, id);
esql.executeUpdate(query);
System.out.println("\tUser added!");
}

}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static void DeleteContact(Messenger esql){
try{
System.out.print("\tEnter contact id: ");
String id = in.readLine();
String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = %s AND list_member='%s'", curr_contact, id);
int success=esql.executeQuery(query);
if(success==0)
System.out.println("\tThis ID does not exist in your contact list!");
else{
query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'",curr_contact, id);
esql.executeUpdate(query);
System.out.println("\tUser deleted!");
}
}catch(Exception e){
         System.err.println (e.getMessage ());
      }}//end

   public static void DeleteBlock(Messenger esql){
try{
System.out.print("\tEnter contact id: ");
String id = in.readLine();
String query = String.format("SELECT * FROM USER_LIST_CONTAINS WHERE list_id = %s AND list_member='%s'", curr_block, id);
int success=esql.executeQuery(query);
if(success==0)
System.out.println("\tThis ID does not exist in your contact list!");
else{
query = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id=%s AND list_member='%s'",curr_block, id);
esql.executeUpdate(query);
System.out.println("\tUser deleted!");
}
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
}//end

   public static void ListContacts(Messenger esql){
try{
String query = String.format("SELECT list_member, status FROM USER_LIST_CONTAINS, USR WHERE list_id=%s AND list_member=login", curr_contact);
esql.executeQueryAndPrintResult(query);
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static void ListBlocks(Messenger esql){
try{
String query = String.format("SELECT list_member FROM USER_LIST_CONTAINS WHERE list_id=%s", curr_block);
esql.executeQueryAndPrintResult(query);
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   public static void NewMessage(Messenger esql){
      try{
         System.out.print("\tEnter your message:");
         String message = in.readLine();
         SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         java.util.Date now = new java.util.Date();
         String curr_timestamp = sdfNow.format( now );
         String query = String.format("INSERT INTO MESSAGE (msg_text, msg_timestamp, sender_login, chat_id) VALUES ('%s','%s','%s','%s')", message, curr_timestamp, curr_user, curr_chat);
	 
         esql.executeUpdate(query);
	String msg_id=Integer.toString(esql.getCurrSeqVal("message_msg_id_seq"));
	query=String.format("SELECT member FROM CHAT_LIST WHERE chat_id=%s", curr_chat);
	ResultSet rs = esql.getResultSet(query);
	while(rs.next())
	{
		String member=rs.getString(1);
		query=String.format("INSERT INTO NOTIFICATION VALUES('%s', %s)", member, 			msg_id);
		esql.executeUpdate(query);

	}
	query=String.format("DELETE FROM NOTIFICATION WHERE usr_login='%s' AND msg_id=%s",curr_user, msg_id);
esql.executeUpdate(query);
         System.out.println ("Message successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 

   public static void LoadMessage(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end 

   public static void EditMessage(Messenger esql){
try{
System.out.print("\tEnter message id: ");
String id = in.readLine();
String query = String.format("SELECT * FROM MESSAGE WHERE msg_id=%s", id);
int success=esql.executeQuery(query);
String query2 = String.format("SELECT * FROM MESSAGE WHERE msg_id=%s AND sender_login='%s'",id,curr_user);
int success2=esql.executeQuery(query2);
if(success==0)
System.out.println("\tInvalid id!");
else if(success2==0)
System.out.println("\tYou are not the creator of this message!");
else{
System.out.print("\tEnter modified message: ");
String newmsg = in.readLine();
query = String.format("UPDATE MESSAGE SET msg_text='%s' WHERE msg_id=%s",newmsg,id);
esql.executeUpdate(query);
System.out.println("\tMessage changed!");
}

}catch(Exception e){
         System.err.println (e.getMessage ());
      }

 
   }//end 

   public static void DeleteMessage(Messenger esql){
try{
System.out.print("\tEnter message id: ");
String id = in.readLine();
String query = String.format("SELECT * FROM MESSAGE WHERE msg_id=%s", id);
int success=esql.executeQuery(query);
String query2 = String.format("SELECT * FROM MESSAGE WHERE msg_id=%s AND sender_login='%s'",id,curr_user);
int success2=esql.executeQuery(query2);
if(success==0)
System.out.println("\tInvalid id!");
else if(success2==0)
System.out.println("\tYou are not the creator of this message!");
else{
query = String.format("DELETE FROM MESSAGE WHERE msg_id=%s",id);
esql.executeUpdate(query);
System.out.println("\tMessage deleted!");
}

}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 

   public static void ReadNotifications(Messenger esql){
   try{
   String query=String.format("SELECT msg_id FROM NOTIFICATION WHERE usr_login = '%s'", curr_user);
esql.executeQueryAndPrintResult(query);
String msg_id="1";
while(!msg_id.equals("-1"))
{
System.out.print("\tEnter message id to read (-1 to end): ");
msg_id = in.readLine();
if(msg_id.equals("-1"))return;
query=String.format("SELECT * FROM NOTIFICATION WHERE usr_login='%s' AND msg_id = %s", curr_user, msg_id);
int success=esql.executeQuery(query);
if(success==0)
System.out.println("\tInvalid id!");
else{
query = String.format("SELECT sender_login, msg_timestamp, msg_text  FROM MESSAGE WHERE msg_id=%s" ,msg_id);
esql.executeQueryAndPrintResult(query);
query = String.format("DELETE FROM NOTIFICATION WHERE usr_login='%s' AND msg_id=%s",curr_user,msg_id);
esql.executeUpdate(query);
}
}
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
  //end Query6
   }//end

   public static void StartChat(Messenger esql){
try{
String id;
String query;
String query2;
int success;
int success2;
boolean added=false;
while(!added)
{
System.out.print("\tEnter member id (-1 to end): ");
id = in.readLine();
if(id.equals("-1"))return;

query=String.format("SELECT * FROM Usr WHERE login = '%s'", id);
success=esql.executeQuery(query);
if(success==0)
System.out.println("\tInvalid id!");
else{
query=String.format("INSERT INTO CHAT(chat_type,init_sender) VALUES ('private','%s')",curr_user);
esql.executeUpdate(query);
curr_chat = Integer.toString(esql.getCurrSeqVal("chat_chat_id_seq"));
query=String.format("INSERT INTO CHAT_LIST VALUES (%s,'%s')",curr_chat, curr_user);
esql.executeUpdate(query);
query=String.format("INSERT INTO CHAT_LIST VALUES (%s,'%s')",curr_chat, id);
esql.executeUpdate(query);
added=true;
System.out.println("\tMember added!");

}
}

while(true)
{
System.out.print("\tEnter member id (-1 to end): ");
id = in.readLine();
if(id.equals("-1"))return;
query=String.format("SELECT * FROM Usr WHERE login = '%s'", id);
success=esql.executeQuery(query);
query2=String.format("SELECT * FROM CHAT_LIST WHERE chat_id=%s AND member='%s'", curr_chat, id);
success2=esql.executeQuery(query2);
if(success==0)
System.out.println("\tInvalid id!");
else if(success2!=0)
System.out.println("\tThis user is already in this chat!");
else{
query=String.format("INSERT INTO CHAT_LIST VALUES (%s,'%s')",curr_chat, id);
esql.executeUpdate(query);
query=String.format("UPDATE CHAT SET chat_type='group' WHERE chat_id='%s'",curr_chat);
esql.executeUpdate(query);
System.out.println("\tMember added!");
}
}

}

catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end Query6


    public static String ListChats(Messenger esql){
try{
   String query=String.format("SELECT chat_id FROM chat_list WHERE member = '%s'", curr_user);
esql.executeQueryAndPrintResult(query);
System.out.print("\tEnter chat id: ");
String chat_id = in.readLine();
query=String.format("SELECT * FROM chat_list WHERE chat_id=%s AND member = '%s'", chat_id, curr_user);
int success=esql.executeQuery(query);
if(success==0)
{
System.out.println("\tInvalid id!");
return ("-1");
}
else{
query=String.format("SELECT * FROM chat WHERE chat_id=%s", chat_id);
ResultSet rs = esql.getResultSet(query);
rs.next();
chat_sender=rs.getString(3);
chat_sender=chat_sender.replaceAll("\\s","");
return chat_id; 
}
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
return("-1");
   }//end Query6



   public static void AddMember(Messenger esql){
   try{
if(!curr_user.equals(chat_sender)){
System.out.println("You are not the initial sender of the chat!");
return;
}

System.out.print("\tEnter member id: ");
String id = in.readLine();
String query=String.format("SELECT * FROM Usr WHERE login = '%s'", id);
int success=esql.executeQuery(query);
String query2=String.format("SELECT * FROM CHAT_LIST WHERE chat_id=%s AND member='%s'", curr_chat, id);
int success2=esql.executeQuery(query2);
if(success==0)
System.out.println("\tInvalid id!");
else if(success2!=0)
System.out.println("\tThis user is already in this chat!");
else{
query=String.format("INSERT INTO CHAT_LIST VALUES (%s,'%s')",curr_chat, id);
esql.executeUpdate(query);
query=String.format("UPDATE CHAT SET chat_type='group' WHERE chat_id='%s'",curr_chat);
esql.executeUpdate(query);
System.out.println("\tMember added!");
}

}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 

   public static void DeleteMember(Messenger esql){
 try{
if(!curr_user.equals(chat_sender)){
System.out.println("You are not the initial sender of the chat!");
return;
}
System.out.print("\tEnter member id: ");
String id = in.readLine();
String query=String.format("SELECT * FROM CHAT_LIST WHERE chat_id=%s AND member='%s'", curr_chat, id);
int success=esql.executeQuery(query);
if(success==0)
System.out.println("\tThis user is not in this chat!");
else{
query=String.format("DELETE FROM CHAT_LIST WHERE chat_id=%s AND member='%s'",curr_chat, id);
esql.executeUpdate(query);
System.out.println("\tMember deleted!");
}
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 

   public static void DeleteChat(Messenger esql){
try{
if(!curr_user.equals(chat_sender)){
System.out.println("You are not the initial sender of the chat!");
return;
}
String query=String.format("DELETE FROM CHAT WHERE chat_id=%s" ,curr_chat);
esql.executeUpdate(query);
System.out.println("\tChat deleted!");
}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 

   public static void DeleteAccount(Messenger esql){
try{
String query=String.format("SELECT * FROM CHAT WHERE init_sender='%s'",curr_user);
int success=esql.executeQuery(query);
if(success!=0)
System.out.println("There are linked information associated with this account, delete cannot be processed.");
else
{
query=String.format("DELETE FROM USR WHERE login='%s'",curr_user);
esql.executeUpdate(query);
System.out.println("Account deleted!");
curr_user=null;
}

}catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 


}//end Messenger
