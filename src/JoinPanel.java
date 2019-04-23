import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class JoinPanel extends Panel {
	
	Connection connection;
	PreparedStatement ps;
	boolean isDoubleChecked = false;
	
	JTextField id = new JTextField(16);
	JPasswordField password = new JPasswordField(16);
	JButton doublecheck = new JButton("CHECK");
	
	public JoinPanel(JFrame frame, Panel panel, Connection connection) {
		this.connection = connection;
		
		JLabel title = new JLabel("JOIN");
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Default", Font.BOLD, 50));
		title.setHorizontalAlignment(0);
		title.setBounds(0, 50, 700, 50);
		add(title);
		
		id.setHorizontalAlignment(0);
		id.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(id.getText().trim().length() >= 16) e.consume();
				if(e.getKeyCode() == KeyEvent.VK_ENTER) { 
					check();
				}
				isDoubleChecked = false;
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		id.setBounds(100, 270,400,50);
		JLabel label_id = new JLabel("ID");
		label_id.setBounds(10, 270, 100, 50);
		label_id.setForeground(Color.WHITE);
		label_id.setHorizontalAlignment(0);
		label_id.setFont(new Font("Default", Font.PLAIN, 20));
		add(label_id);
		
		password.setHorizontalAlignment(0);
		password.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(password.getText().length() >= 16) e.consume();
				// System.out.println(password.getText());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		password.setBounds(100,410,400,50);
		add(password);
		JLabel label_pw = new JLabel("PW");
		label_pw.setBounds(10, 410, 100, 50);
		label_pw.setForeground(Color.WHITE);
		label_pw.setHorizontalAlignment(0);
		label_pw.setFont(new Font("Default", Font.PLAIN, 20));
		add(label_pw);
		
		doublecheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println(id.getText());
				check();
			}
		});
		doublecheck.setOpaque(false);
		doublecheck.setBackground(Color.WHITE);
		doublecheck.setForeground(Color.WHITE);
		doublecheck.setFont(new Font("Default", Font.PLAIN, 20));
		doublecheck.setBounds(520, 270, 130, 50);
		add(doublecheck);
		
		JButton submit = new JButton("JOIN");
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println(password.getText().trim());
				join(frame, panel);
			}
		});
		submit.setBounds(20, 650, 660, 100);
		submit.setOpaque(false);
		submit.setBackground(Color.WHITE);
		submit.setForeground(Color.WHITE);
		
		add(id);
		add(submit);
		
		id.setFocusable(true);
		id.requestFocusInWindow();
	}
	
	public void check() {
		if(id.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.", "ID 중복확인", JOptionPane.WARNING_MESSAGE);
			id.setFocusable(true);
			id.requestFocusInWindow();
		}
		else {
			try {  
				ps = connection.prepareStatement("select id from user where id = ?");
				ps.setString(1, id.getText());
				ResultSet res = ps.executeQuery();
				
				if(res.isBeforeFirst()) {
					// System.out.println("다른 아이디 사용");
					JOptionPane.showMessageDialog(this, "다른 아이디를 사용하세요", "ID 중복확인", JOptionPane.WARNING_MESSAGE);
					id.setText("");
					id.setFocusable(true);
					id.requestFocusInWindow();
				}
				else {
					// System.out.println(id.getText() + " 사용가능");
					String answer[] = {"YES", "NO"};
					
					int ans = JOptionPane.showOptionDialog(this, id.getText() + "은(는) 사용할 수 있는 아이디 입니다.\n사용하시겠습니까?", "ID 중복확인", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, answer, answer[0]);
					if(ans == 0) {
						isDoubleChecked = true;
						password.setFocusable(true);
						password.requestFocusInWindow();
					}
					else {
						id.setText("");
						id.setFocusable(true);
						id.requestFocusInWindow();
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void join(JFrame frame, Panel panel) {
		if(isDoubleChecked && !(password.getText().trim().equals(""))) {
			try {
				ps = connection.prepareStatement("insert into user values(?,password(?))");
				ps.setString(1, id.getText().trim());
				ps.setString(2, password.getText().trim());
				int result = ps.executeUpdate();
				if(result == 1) { 
					JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "회원가입", JOptionPane.INFORMATION_MESSAGE);
					frame.getContentPane().removeAll();
					frame.add(panel);
					frame.revalidate();
					frame.repaint();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "회원가입에 실패하였습니다. 다시 시도해주세요.", "회원가입", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "아이디나  비밀번호를 먼저 입력해주세요.", "회원가입", JOptionPane.WARNING_MESSAGE);
			id.setText("");
			password.setText("");
			repaint();
		}
	}
}