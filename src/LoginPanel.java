import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends Panel {
	
	Connection connection;
	PreparedStatement ps;
	
	JTextField id = new JTextField(16);
	JPasswordField password = new JPasswordField(16);
	
	public LoginPanel(JFrame frame, MainPanel panel, Connection connection) {
		this.connection = connection;
		
		JLabel title = new JLabel("LOGIN");
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
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		id.setBounds(100,200,500,50);
		JLabel label_id = new JLabel("ID");
		label_id.setBounds(10, 200, 100, 50);
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
		
		password.setBounds(100, 350, 500, 50);
		JLabel label_pw = new JLabel("PW");
		label_pw.setBounds(10, 350, 100, 50);
		label_pw.setForeground(Color.WHITE);
		label_pw.setHorizontalAlignment(0);
		label_pw.setFont(new Font("Default", Font.PLAIN, 20));
		add(label_pw);
		
		JButton submit = new JButton("LOGIN");
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println(password.getText().trim());
				login(frame, panel);
			}
		});
		submit.setBounds(20, 510, 660, 40);
		submit.setOpaque(false);
		submit.setBackground(Color.WHITE);
		submit.setForeground(Color.WHITE);
		add(id);
		add(password);
		add(submit);
		
		JLabel label_join = new JLabel("아직 회원가입을 안하셨나요?");
		label_join.setForeground(Color.WHITE);
		label_join.setFont(new Font("돋움", Font.PLAIN, 30));
		label_join.setHorizontalAlignment(0);
		label_join.setBounds(0, 620, 700, 50);
		add(label_join);
		
		id.setFocusable(true);
		id.requestFocusInWindow();
	}
	
	public void login(JFrame frame, MainPanel panel) {
		if(!(id.getText().trim().equals("")) && !(password.getText().trim().equals(""))) {
			try {
				ps = connection.prepareStatement("select * from user where id = ? and password = password(?)");
				ps.setString(1, id.getText().trim());
				ps.setString(2, password.getText().trim());
				ResultSet result = ps.executeQuery();

				if(result.isBeforeFirst()) { 
					// System.out.println("SUCCESS");
					game_Frame.user_id = id.getText().trim();
					panel.user.setText(game_Frame.user_id);
					ps = connection.prepareStatement("select high_score from ranking where id = ?");
					ps.setString(1, game_Frame.user_id);
					
					result = ps.executeQuery();
					
					if(result.next()) game_Frame.High_score = result.getInt(1);
					else game_Frame.High_score = 0;
					
					panel.high_score.setText("HIGHEST : " + String.format("%09d", game_Frame.High_score));
					
					frame.getContentPane().removeAll();
					frame.add(panel);
					frame.revalidate();
					frame.repaint();
				}
				else {
					JOptionPane.showMessageDialog(frame, "아이디나  비밀번호가 틀렸습니다. 다시 시도해주세요.", "로그인", JOptionPane.WARNING_MESSAGE);
					id.setText("");
					password.setText("");
					frame.repaint();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				// System.out.println("ERROR");
			}
		}
		else {
			JOptionPane.showMessageDialog(frame, "아이디나  비밀번호를 먼저 입력해주세요.", "로그인", JOptionPane.WARNING_MESSAGE);
			id.setText("");
			password.setText("");
			frame.repaint();
		}
		
		id.setFocusable(true);
		id.requestFocusInWindow();

	}
}
