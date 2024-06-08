package com;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;
public class ProcessThread extends Thread{
	Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
	DefaultTableModel dtm;
public ProcessThread(Socket soc,DefaultTableModel dtm){
	socket=soc;
	this.dtm=dtm;
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
		if(type.equals("generatekey")){
			String user = (String)input[1];
			String arr[] = user.split(",");
			byte key[] = GenerateKey.KeyGeneration(arr);
			Object obj[] = {key};
			out.writeObject(obj);
			out.flush();
			Object res[] = {"Secret key generated for user "+user};
			dtm.addRow(res);
		}
	}catch(Exception e){
        e.printStackTrace();
    }
}
}
