package com;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
public class ProcessThread extends Thread{
	Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
	DefaultTableModel dtm;
	HashMap<String,ArrayList<String>> cache;
	static double normal_time,memory_time;
public ProcessThread(Socket soc,DefaultTableModel dtm,HashMap<String,ArrayList<String>> cache){
	socket = soc;
	this.dtm = dtm;
	this.cache = cache;
	try{
		out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }catch(Exception e){
        e.printStackTrace();
    }
}

@Override
public void run(){
	try{
		Object input[]=(Object[])in.readObject();
        String type=(String)input[0];
		if(type.equals("download")){
			String user = (String)input[1];
			String filename = (String)input[2];
			FileInputStream fin = new FileInputStream("CloudUser/"+user+"/"+filename);
			byte b[] = new byte[fin.available()];
			fin.read(b,0,b.length);
			fin.close();
			Object res[] = {b};
			out.writeObject(res);
			Object res1[] = {filename+" sent to edge server"};
			dtm.addRow(res1);
		}
		if(type.equals("upload")){
			String user = (String)input[1];
			String filename = (String)input[2];
			byte enc[] = (byte[])input[3];
			String keywords = (String)input[4];
			File file = new File("CloudUser/"+user);
			if(!file.exists())
				file.mkdir();
			byte kword[] = keywords.getBytes();
			FileOutputStream fout = new FileOutputStream("trapdoor.txt",true);
			fout.write(kword,0,kword.length);
			fout.close();
			fout = new FileOutputStream(file.getPath()+"/"+filename);
			fout.write(enc,0,enc.length);
			fout.close();
			Object res[] = {filename+" File saved at cloud server"};
			out.writeObject(res);
			dtm.addRow(res);
		}
if(type.equals("search")){
    String query = (String)input[1];
    
    double encryptionStart = System.nanoTime();
    
   
    String encryptedQuery = new StringBuilder(query).reverse().toString();
    
    double encryptionEnd = System.nanoTime();
    double encryptionTime = (encryptionEnd - encryptionStart) / 1_000_000_000.0;
    memory_time = encryptionTime;
	Object res1[] = {"Encryption done at memory module: " + encryptionTime + " seconds"};
    dtm.addRow(res1); // Assuming you want to add this row here

    if(cache.containsKey(query)){ 
        double start = System.nanoTime();
        ArrayList<String> list = cache.get(query);
        Object res[] = {list};
        out.writeObject(res);
        Object res2[] = {"Search result sent to user for query "+query+" from memory module"}; 
        dtm.addRow(res2);
        double end = System.nanoTime();
        CloudServer.cache_time = (end - start)/1_000_000_000.0; 
		normal_time = CloudServer.cache_time;
    }else{
        double start = System.nanoTime();
        String qry[] = query.split(","); 
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> dup = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("trapdoor.txt"));
        String line; // Correctly declare the variable before the loop
        System.out.println(query);
        while((line = br.readLine()) != null){ // Now 'line' is recognized within this block
            String data[] = line.split("#");
            for(int j = 0; j < qry.length; j++){
                if(data[2].indexOf(qry[j]) != -1){
                    if(!dup.contains(data[1])){
                        dup.add(data[1]);
                        list.add(data[0] + "," + data[1]);
                    }
                }
            }
        }
        br.close();
        cache.put(query, list); // Adjust as per actual logic
        Object res[] = {list};
        out.writeObject(res);
        Object res2[] = {"Search result sent to user for query "+query+" Search result not found in memory module"};
        dtm.addRow(res2);
        double end = System.nanoTime();
        CloudServer.normal_time = (end - start)/1_000_000_000.0;
    }
}

	}catch(Exception e){
        e.printStackTrace();
    }
}
}
