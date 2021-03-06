package WB;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.proteanit.sql.DbUtils;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.util.Timer;
import java.util.TimerTask;
import com.toedter.calendar.JDateChooser;

import PDF.PDF_SACA2;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JRadioButton;


public class Okno extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTextField textField;
	private JTextField textField_1;
	private JRadioButton spawane;
	private JRadioButton mechaniczne;
	private SimpleDateFormat formatDaty = new SimpleDateFormat("yyyy-MM-dd");
	private Calendar date = Calendar.getInstance();
	private String dzisiaj = formatDaty.format(date.getTime());
	private String DodaneDoPilnych = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Connection connection = RCPdatabaseConnection.dbConnector("tosia", "1234");
				try {
					Okno frame = new Okno(connection);
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public Okno(final Connection connection) {
		
		setBackground(Color.WHITE);
		setTitle("Pilne SACA");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 579, 709);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblMojeMenu = new JLabel("Pilne dla SACA");
		lblMojeMenu.setHorizontalAlignment(SwingConstants.CENTER);
		lblMojeMenu.setFont(new Font("Century", Font.BOLD, 24));
		
		JScrollPane scrollPane = new JScrollPane();
		
		table = new JTable();
		table.setBackground(Color.WHITE);
		table.setAutoCreateRowSorter(true);
		table.setDefaultRenderer(Object.class, new TableCellRendererColor());
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try{
					int row = table.getSelectedRow();
					String wybor = (table.getModel().getValueAt(row, 1)).toString();
					
					textField_1.setText(wybor);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			});
		
		scrollPane.setViewportView(table);
		
		JLabel lblProjekt = new JLabel("Projekt:");
		lblProjekt.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblTyp = new JLabel("Typ:");
		lblTyp.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode()==KeyEvent.VK_ENTER){
					ShowTable(connection, table);
					System.out.println("aaa");
				}
			}
		});
		textField.setToolTipText("nr seryjny maszyny");
		textField.setColumns(10);
		
		spawane = new JRadioButton("spawane");
		spawane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowTable(connection, table);
			}
		});
		spawane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_ENTER){
					ShowTable(connection, table);
				}
			}
		});
		spawane.setSelected(true);
		
		mechaniczne = new JRadioButton("mechaniczne");
		mechaniczne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowTable(connection, table);
			}
		});
		mechaniczne.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_ENTER){
					ShowTable(connection, table);
				}
			}
		});
		
		ButtonGroup grupa = new ButtonGroup();
		grupa.add(spawane);
		grupa.add(mechaniczne);
		
		JTextArea txtrDodanoDoPilnych = new JTextArea();
		txtrDodanoDoPilnych.setFont(new Font("Century", Font.PLAIN, 12));
		txtrDodanoDoPilnych.setLineWrap(true);
		txtrDodanoDoPilnych.setText("Dodano do pilnych:\r\n- "+DodaneDoPilnych);
		
		JButton btnSzukaj = new JButton("Szukaj");
		btnSzukaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShowTable(connection, table);
			}
		});
		btnSzukaj.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSzukaj.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_ENTER){
					ShowTable(connection, table);
				}
			}
		});
		
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		
		Image img1 = new ImageIcon(this.getClass().getResource("/light_mini.png")).getImage();
		Image img2 = new ImageIcon(this.getClass().getResource("/refresh_mini.png")).getImage();
		Image img3 = new ImageIcon(this.getClass().getResource("/Check-icon2.png")).getImage();
		
		JButton btnOznaczJakoPilne = new JButton("Pilne");
		btnOznaczJakoPilne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(textField_1.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Nie zaznaczono �adnej pozycji");
				}else{
					//sprawdza czy wybrano zlozenia spawane czy mechaniczne
					if(spawane.isSelected()){
						
						int row2 = table.getSelectedRow();
						String projektGlowny = (table.getModel().getValueAt(row2, 0)).toString();
						String zlozenieSpawane = (table.getModel().getValueAt(row2, 1)).toString();
						ArrayList<String> CzesciDoZlozenia = new ArrayList<String>();
						String CzesciDoZlozeniaString = CzesciDoZlozenia.toString();
						
						System.out.println(projektGlowny);
						System.out.println(zlozenieSpawane);
						
						// szuka z tabeli spawane czesci, ktore naleza do wybranego zlozenia spawanego - zapisuje je w ArrayList, a pozniej konwertuje j� do Stringa
						try {		
							String query= "SELECT kodArt FROM spawane WHERE projekt = '"+projektGlowny+"' AND ArtykulNadrzedny = '"+zlozenieSpawane+"' AND nrZamowienia<>'Na magazynie'";
							PreparedStatement pst=connection.prepareStatement(query);
							ResultSet rs=pst.executeQuery();
							while(rs.next()){
								CzesciDoZlozenia.add("'"+rs.getString("kodArt")+"'");
							}
							CzesciDoZlozeniaString = CzesciDoZlozenia.toString();
							CzesciDoZlozeniaString = CzesciDoZlozeniaString.replace('[', '(');
							CzesciDoZlozeniaString = CzesciDoZlozeniaString.replace(']', ')');
							System.out.println(CzesciDoZlozeniaString);
							
							pst.close();
							rs.close();
										
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						// w tabeli saca znajduje czesci zapisane powyzej w CzesciDoZlozeniaString, a nastepnie nadaje im status pilne
						try {
							String query="";
							if(CzesciDoZlozenia.isEmpty()){
								// musi byc wywolane jakies query, nawet jesli czesci do zlozenia sa puste
								query = "SELECT * FROM saca";
							}else{
								query= "UPDATE saca SET Wazne = 1 WHERE projekt = '"+projektGlowny+"' AND KodArtykulu IN "+CzesciDoZlozeniaString+" AND DataDodania = '"+dzisiaj+"'";
							}
							PreparedStatement pst=connection.prepareStatement(query);
							ResultSet rs=pst.executeQuery();
							pst.close();
							rs.close();
										
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//dodanie wybranej pozycji do listy Dodano do pilnych
						
						DodaneDoPilnych = DodaneDoPilnych + zlozenieSpawane + " cz�ci: " + CzesciDoZlozeniaString + ", ";
						txtrDodanoDoPilnych.setText("Dodano do pilnych:\r\n- "+DodaneDoPilnych);
						
						//zakolorowanie wybranej pozycji
						
						table.repaint();
						ShowTable(connection, table);
						
					// gdy wybrano czesci mechaniczne
					}else{
						int row3 = table.getSelectedRow();
						String projektGlowny = (table.getModel().getValueAt(row3, 0)).toString();
						String artykul = (table.getModel().getValueAt(row3, 1)).toString();
						System.out.println(projektGlowny);
						System.out.println(artykul);
						System.out.println(dzisiaj);
						
						try {		
							String query= "UPDATE saca SET Wazne = 1 WHERE projekt = '"+projektGlowny+"' AND KodArtykulu = '"+artykul+"' AND DataDodania = '"+dzisiaj+"'";
							PreparedStatement pst=connection.prepareStatement(query);
							ResultSet rs=pst.executeQuery();
							pst.close();
							rs.close();
										
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//dodanie wybranej pozycji do listy Dodano do pilnych
						
						DodaneDoPilnych = DodaneDoPilnych + artykul + ", ";
						txtrDodanoDoPilnych.setText("Dodano do pilnych:\r\n- "+DodaneDoPilnych);
						
						//zakolorowanie wybranej pozycji
						
						table.repaint();
						ShowTable(connection, table);
					
					}
				}
			}
		});
		btnOznaczJakoPilne.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnOznaczJakoPilne.setIcon(new ImageIcon(img1));
		
		
		// ODZNACZENIE POZYCJI JAKO PILNEJ
		
		JButton btnNiePilne = new JButton("Nie pilne");
		btnNiePilne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if(textField_1.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Nie zaznaczono �adnej pozycji");
				}else{
					//sprawdza czy wybrano zlozenia spawane czy mechaniczne
					if(spawane.isSelected()){
						
						int row2 = table.getSelectedRow();
						String projektGlowny = (table.getModel().getValueAt(row2, 0)).toString();
						String zlozenieSpawane = (table.getModel().getValueAt(row2, 1)).toString();
						ArrayList<String> CzesciDoZlozenia = new ArrayList<String>();
						String CzesciDoZlozeniaString = CzesciDoZlozenia.toString();
						
						System.out.println(projektGlowny);
						System.out.println(zlozenieSpawane);
						
						// szuka z tabeli spawane czesci, ktore naleza do wybranego zlozenia spawanego - zapisuje je w ArrayList, a pozniej konwertuje j� do Stringa
						try {		
							String query= "SELECT kodArt FROM spawane WHERE projekt = '"+projektGlowny+"' AND ArtykulNadrzedny = '"+zlozenieSpawane+"' AND nrZamowienia<>'Na magazynie'";
							PreparedStatement pst=connection.prepareStatement(query);
							ResultSet rs=pst.executeQuery();
							while(rs.next()){
								CzesciDoZlozenia.add("'"+rs.getString("kodArt")+"'");
							}
							CzesciDoZlozeniaString = CzesciDoZlozenia.toString();
							CzesciDoZlozeniaString = CzesciDoZlozeniaString.replace('[', '(');
							CzesciDoZlozeniaString = CzesciDoZlozeniaString.replace(']', ')');
							System.out.println(CzesciDoZlozeniaString);
							
							pst.close();
							rs.close();
										
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						// w tabeli saca znajduje czesci zapisane powyzej w CzesciDoZlozeniaString, a nastepnie nadaje im status pilne
						try {
							String query="";
							if(CzesciDoZlozenia.isEmpty()){
								// musi byc wywolane jakies query, nawet jesli czesci do zlozenia sa puste
								query = "SELECT * FROM saca";
							}else{
								query= "UPDATE saca SET Wazne = 0 WHERE projekt = '"+projektGlowny+"' AND KodArtykulu IN "+CzesciDoZlozeniaString+" AND DataDodania = '"+dzisiaj+"'";
							}
							PreparedStatement pst=connection.prepareStatement(query);
							ResultSet rs=pst.executeQuery();
							pst.close();
							rs.close();
										
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//usuniecie wybranej pozycji do listy Dodano do pilnych
						
						DodaneDoPilnych = DodaneDoPilnych.replace(zlozenieSpawane + " cz�ci: " + CzesciDoZlozeniaString + ", ", "");
						txtrDodanoDoPilnych.setText("Dodano do pilnych:\r\n- "+DodaneDoPilnych);
						
						//ODkolorowanie wybranej pozycji
						
						table.repaint();
						ShowTable(connection, table);
						
					// gdy wybrano czesci mechaniczne
					}else{
						int row3 = table.getSelectedRow();
						String projektGlowny = (table.getModel().getValueAt(row3, 0)).toString();
						String artykul = (table.getModel().getValueAt(row3, 1)).toString();
						System.out.println(projektGlowny);
						System.out.println(artykul);
						System.out.println(dzisiaj);
						
						try {		
							String query= "UPDATE saca SET Wazne = 0 WHERE projekt = '"+projektGlowny+"' AND KodArtykulu = '"+artykul+"' AND DataDodania = '"+dzisiaj+"'";
							PreparedStatement pst=connection.prepareStatement(query);
							ResultSet rs=pst.executeQuery();
							pst.close();
							rs.close();
										
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//usuniecie wybranej pozycji do listy Dodano do pilnych
						
						DodaneDoPilnych = DodaneDoPilnych.replace(artykul + ", ", "");
						txtrDodanoDoPilnych.setText("Dodano do pilnych:\r\n- "+DodaneDoPilnych);
					
						//ODkolorowanie wybranej pozycji
						
						table.repaint();
						ShowTable(connection, table);
					}
				}
			}
		});
		btnNiePilne.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNiePilne.setIcon(new ImageIcon(img2));
		
		JButton btnAktualizuj = new JButton("Aktualizuj");
		btnAktualizuj.setIcon(new ImageIcon(img3));
		btnAktualizuj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PDF_SACA2.createDoc();
			}
		});
		btnAktualizuj.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnSzukaj)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_1))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTyp, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblProjekt))
							.addGap(13)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(spawane)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(mechaniczne))
								.addComponent(textField))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOznaczJakoPilne)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNiePilne)
					.addContainerGap(117, Short.MAX_VALUE))
				.addComponent(lblMojeMenu, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(txtrDodanoDoPilnych, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(405, Short.MAX_VALUE)
					.addComponent(btnAktualizuj, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblMojeMenu, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblProjekt))
					.addPreferredGap(ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(spawane)
						.addComponent(lblTyp, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addComponent(mechaniczne))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSzukaj)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOznaczJakoPilne, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNiePilne, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 358, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtrDodanoDoPilnych, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAktualizuj, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
					.addGap(12))
		);
		contentPane.setLayout(gl_contentPane);
		
		JLabel lblNewLabel = new JLabel("");
		Image img = new ImageIcon(this.getClass().getResource("/piesa.png")).getImage();
		lblNewLabel.setBounds(450, 0, 200, 200);
		lblNewLabel.setIcon(new ImageIcon(img));
		contentPane.add(lblNewLabel);
		
		}
	
	public void ShowTable(Connection connection, JTable table)
	{
		if(textField.getText().equals("")){
			JOptionPane.showMessageDialog(null, "Nie wype�niono pola projektu");
		}else{
			if(spawane.isSelected()){
				try {		
					//LEFT JOIN saca ON spawane.KodArt=saca.KodArtykulu AND SPAWANE.Projekt = SACA.projekt 
					String query= "SELECT DISTINCT spawane.projekt, spawane.ArtykulNadrzedny AS ZlozenieSpawane, partsoverview.ItemDesc AS NazwaZlozenia, saca.Wazne AS Pilne FROM spawane " + 
							"LEFT JOIN partsoverview ON spawane.ArtykulNadrzedny = partsoverview.itemno " + 
							"LEFT JOIN saca ON spawane.KodArt=saca.KodArtykulu and spawane.Projekt = saca.projekt " + 
							"WHERE spawane.projekt LIKE '%/"+textField.getText()+"' AND spawane.nrZamowienia <> 'Na magazynie' AND DataDodania = '"+dzisiaj+"'";
					PreparedStatement pst=connection.prepareStatement(query);
					ResultSet rs=pst.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(rs));
					pst.close();
					rs.close();
								
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(mechaniczne.isSelected()){
				try {		
					String query= "SELECT projekt, KodArtykulu AS Artykul, NazwaArtykulu, Wazne AS Pilne FROM saca WHERE projekt LIKE '%/"+textField.getText()+"' AND typ='M' AND DataDodania = '"+dzisiaj+"'";
					PreparedStatement pst=connection.prepareStatement(query);
					ResultSet rs=pst.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(rs));
					pst.close();
					rs.close();
								
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public class TableCellRendererColor extends DefaultTableCellRenderer{
		
		private Component komponent;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
			
			komponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			int ileWierszy = table.getModel().getRowCount();
			int nrKolumny=1;

			
			for (int i = 0; i < ileWierszy; i++){
				
				Object Komorka = table.getModel().getValueAt(row, column);

				if (DodaneDoPilnych.contains(Komorka.toString()) && column!=3){
					komponent.setBackground(new java.awt.Color(255, 221, 153));
				}else{
					komponent.setBackground(new java.awt.Color(255,255,255));
				}
			}
			
			return komponent;
		}
		
	}
}


