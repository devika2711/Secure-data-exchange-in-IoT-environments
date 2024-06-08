package com;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import org.jfree.ui.RefineryUtilities;
public class UserScreen extends JFrame{
	JButton b1,b2,b3,b4,b5;
	JPanel p2;
	CustomPanel p1;
	Font f1;
	JFileChooser chooser;
	String user;
	Login login;

	byte filedata[] = null;
	File file;
	static ArrayList<String> time = new ArrayList<String>();
public UserScreen(String usr,Login log){
	setTitle("Welcome "+usr);
	user = usr;
	login = log;
	
	p1 = new CustomPanel();
	p1.setTitle("   User Screen");
	p1.setLayout(null);
	f1 = new Font("Microsoft Sanserif",Font.BOLD,11);
	
	chooser = new JFileChooser();
	p2 = new JPanel(); 
	
	b1 = new JButton("Upload File");
	b1.setFont(f1);
	p2.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			int option = chooser.showOpenDialog(UserScreen.this);
			if(option == JFileChooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				try{
					FileInputStream fin = new FileInputStream(file);
					filedata = new byte[fin.available()];
					fin.read(filedata,0,filedata.length);
					fin.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	});
	b2 = new JButton("Share File");
	b2.setFont(f1);
	p2.add(b2);
	b2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			sharefile();
		}
	});

	b3 = new JButton("Search File");
	b3.setFont(f1);
	p2.add(b3);
	b3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			searchFile();
		}
	});

	b4 = new JButton("File Encryption Time Graph");
	b4.setFont(f1);
	p2.add(b4);
	b4.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Chart chart1 = new Chart("File Encryption Time Graph");
			chart1.pack();
			RefineryUtilities.centerFrameOnScreen(chart1);
			chart1.setVisible(true);
		}
	});

	b5 = new JButton("Logout");
	b5.setFont(f1);
	p2.add(b5);
	b5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			logout();
			setVisible(false);
			login.clear();
			login.setVisible(true);
		}
	});

	p2.setBackground(Color.white);
	p2.setBounds(0,45,600,400);
	p1.add(p2);
	getContentPane().add(p1,BorderLayout.CENTER);
}

public void logout(){
	try{
		Socket socket = new Socket("localhost",2222);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		Object req[] = {"logout",user};
		out.writeObject(req);
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		Object res[] = (Object[])in.readObject();
	}catch(Exception e){
		e.printStackTrace();
	}
}

public void sharefile(){
	try{
		Socket socket = new Socket("localhost",2222);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		Object req[] = {"upload",user,file.getName(),filedata};
		out.writeObject(req);
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		Object res[] = (Object[])in.readObject();
		String response = (String)res[0];
		String timedata = (String)res[1];
		time.add(file.getName()+","+timedata);
		JOptionPane.showMessageDialog(this,response);
	}catch(Exception e){
		e.printStackTrace();
	}
}
public void searchFile(){
	String query = JOptionPane.showInputDialog(this,"Enter search query");
	if(query != null){
		try{
			Socket socket = new Socket("localhost",2222);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			Object req[] = {"search",query};
			out.writeObject(req);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Object res[] = (Object[])in.readObject();
			ArrayList<String> response = (ArrayList<String>)res[0];
			if(response.size() > 0){
				SearchResult sr = new SearchResult();
				sr.setVisible(true);
				sr.setSize(600,400);
				sr.viewData(response);
			}else{
				JOptionPane.showMessageDialog(this,"Given query not found");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
}