package com;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
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
import com.jd.swing.util.Theme;
import com.jd.swing.util.PanelType;
import com.jd.swing.custom.component.panel.StandardPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import org.jfree.ui.RefineryUtilities;
import java.util.ArrayList;
public class CloudServer extends JFrame{
	StandardPanel p1;
	JPanel p2,p3,p4;
	JLabel title,l1;
	Font f1;
	JTable table;
	JScrollPane jsp;
	DefaultTableModel dtm;
	ServerSocket server;
	ProcessThread thread;
	HashMap<String,ArrayList<String>> cache = new HashMap<String,ArrayList<String>>();
	static double normal_time,cache_time;
	JButton b1, b2;
public void start(){
	try{
		server = new ServerSocket(1111);
		Object res[] = {"Cloud Storage Services Started"};
		dtm.addRow(res);
		while(true){
			Socket socket = server.accept();
			socket.setKeepAlive(true);
			thread=new ProcessThread(socket,dtm,cache);
			thread.start();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
public CloudServer(){
	super("Cloud Storage Services Started");
	
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	p1 = new StandardPanel(Theme.STANDARD_GREEN_THEME,PanelType.PANEL_ROUNDED);
	p1.setPreferredSize(new Dimension(200,50));
	f1 = new Font("Courier New",Font.BOLD,14);
	p2 = new TitlePanel(600,60);
	p2.setBackground(new Color(204, 110, 155));
	title = new JLabel("<HTML><BODY><CENTER>Secure Data Sharing and Searching at the Edge of Cloud-Assisted<br/>Internet of Things</BODY></HTML>".toUpperCase());
	title.setFont(new Font("Courier New",Font.BOLD,16));
	p2.add(title);
	panel.add(p1,BorderLayout.CENTER);
	panel.add(p2,BorderLayout.NORTH);

	l1 = new JLabel("Cloud Storage Screen");
	l1.setFont(f1);
	p1.add(l1);
	
	p3 = new JPanel();
	p3.setLayout(new BorderLayout());
	dtm = new DefaultTableModel(){
		public boolean isCellEditable(int r,int c){
			return false;
		}
	};
	table = new JTable(dtm);
	table.setFont(f1);
	table.getTableHeader().setFont(new Font("Courier New",Font.BOLD,15));
	table.setRowHeight(30);
	jsp = new JScrollPane(table);
	jsp.getViewport().setBackground(Color.white);
	dtm.addColumn("Client Request Processing Status");
	
	p3.add(jsp,BorderLayout.CENTER);

	p4 = new JPanel();
	b1 = new JButton("<html><body>Extension Comparison Graph<br/>Normal Search Time Vs Memory Module Search Time<body></html>");
	b1.setFont(f1);
	p4.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Chart chart1 = new Chart("Normal Search Time Vs Memory module Search Time");
			chart1.pack();
			RefineryUtilities.centerFrameOnScreen(chart1);
			chart1.setVisible(true);
		}
	});

    b2 = new JButton("<html><body>Encryption Comparison Graph<br/>Encryption Time Comparison</body></html>");
    b2.setFont(f1);
    p4.add(b2); // Don't forget to add b2 to the panel
    b2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            // Assuming Chart2 is correctly implemented to display the desired chart
            Chart2 chart2 = new Chart2("Encryption Time Comparison");
            chart2.pack();
            RefineryUtilities.centerFrameOnScreen(chart2);
            chart2.setVisible(true);
        }
    });

	getContentPane().add(panel,BorderLayout.NORTH);
	getContentPane().add(p3,BorderLayout.CENTER);
	getContentPane().add(p4,BorderLayout.SOUTH);
}
public static void main(String a[])throws Exception{
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	CloudServer cs = new CloudServer();
	cs.setVisible(true);
	cs.setExtendedState(JFrame.MAXIMIZED_BOTH);
	new ServerThread(cs);
}
}