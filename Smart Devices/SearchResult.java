package com;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;
public class SearchResult extends JFrame{
	JTable tab;
	DefaultTableModel dtm;
	JScrollPane jsp;
	JPanel p1,p2;
	Font f1;
	JButton b1;
public SearchResult(){
	super("View Search Result");
	getContentPane().setLayout(new BorderLayout());

	f1 = new Font("Times New Roman",Font.BOLD,14);

	dtm = new DefaultTableModel(){
		public boolean isCellEditable(int row_no,int column_no){
			return false;
		}
	};
	tab = new JTable(dtm);
	tab.getTableHeader().setFont(new Font("Courier New",Font.BOLD,14));
	tab.setFont(new Font("Courier New",Font.BOLD,13));
	tab.setRowHeight(30);
	jsp = new JScrollPane(tab);
	dtm.addColumn("Username");
	dtm.addColumn("Filename");
	getContentPane().add(jsp,BorderLayout.CENTER);

	p2 = new JPanel();
	b1 = new JButton("Download File");
	b1.setFont(f1);
	p2.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			download();
		}
	});
	getContentPane().add(p2,BorderLayout.SOUTH);
}
public void viewData(ArrayList<String> list){
	for(int i=0;i<list.size();i++){
		String arr[] = list.get(i).split(",");
		dtm.addRow(arr);
	}
}
public void download(){
	int row = tab.getSelectedRow();
	String user = dtm.getValueAt(row,0).toString().trim();
	String file = dtm.getValueAt(row,1).toString().trim();
	try{
		Socket socket = new Socket("localhost",2222);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		Object req[] = {"download",user,file};
		out.writeObject(req);
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		Object res[] = (Object[])in.readObject();
		byte data[] = (byte[])res[0];
		String str = new String(data);
		if(!str.equals("File integrity failed")){
			FileOutputStream fout = new FileOutputStream("D:/"+file);
			fout.write(data,0,data.length);
			fout.close();
			JOptionPane.showMessageDialog(this,"File downloaded in D directory");
		}else{
			JOptionPane.showMessageDialog(this,"File verification failed");
		}

	}catch(Exception e){
		e.printStackTrace();
	}
}
}