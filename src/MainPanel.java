import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class MainPanel extends Panel {
	
	Connection connection;
	PreparedStatement ps;
	ResultSet result;
	
	DefaultTableModel model;
	String header[] = {"RANK", "ID", "SCORE"};
	
	JLabel user;
	JLabel high_score;
	JTable ranking;
	
	int row_h = 45;
	
	public MainPanel(Connection connection) {
		super("image\\main_background.png");
		this.connection = connection;
		
		try {
			ImageIcon setting_icon = new ImageIcon("image\\setting.png");
			JButton setting = new JButton(setting_icon);
			setting.setBounds(10, 10, 32, 32);
			setting.setOpaque(false);
			setting.setBackground(Color.WHITE);
			setting.setBorderPainted(false);
			setting.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new SettingDialog();
				}
			});
			
			// System.out.println(game_Frame.user_id);
			user = new JLabel(game_Frame.user_id);
			user.setForeground(Color.CYAN);
			user.setFont(new Font("Default", Font.BOLD, 25));
			user.setBounds(60, 10, 300, 32);
			
			high_score = new JLabel();
			high_score.setForeground(Color.WHITE);
			high_score.setFont(new Font("Default", Font.BOLD, 25));
			high_score.setBounds(410, 10, 300, 32);
			
			JButton btn_ranking = new JButton("일반 모드");
			btn_ranking.setBounds(50, 70, 300, 60);
			btn_ranking.setOpaque(false);
			btn_ranking.setForeground(Color.WHITE);
			btn_ranking.setBackground(Color.WHITE);
			
			JButton btn_fight = new JButton("대전 모드");
			btn_fight.setBounds(350, 70, 300, 60);
			btn_fight.setOpaque(false);
			btn_fight.setForeground(Color.WHITE);
			btn_fight.setBackground(Color.WHITE);
			
			model = new DefaultTableModel(header, 0);
			ranking();
			
			ranking = new JTable(model);
			ranking.setBounds(50, 130, 600, row_h * 11);
			TableSet();
			
			add(setting);
			add(user);
			add(high_score);
			add(btn_ranking);
			add(btn_fight);
			add(ranking);
		}
		catch(Exception e) {
			
		}
	}
	
	public void ranking() throws SQLException {
		model.setRowCount(0);
		model.setColumnIdentifiers(header);
		model.addColumn(header);
		
		ps = connection.prepareStatement("select * from ranking order by high_score desc limit 10");
		result = ps.executeQuery();
		
		model.addRow(header);
		int i = 0;
		while(result.next()) {
			String row[] = new String[3];
			row[0] = String.valueOf(++i);
			row[1] = result.getString(1);
			row[2] = result.getString(2);
			model.addRow(row);
		}
	}
	
	public void TableSet() {
		ranking.getColumn("RANK").setPreferredWidth(80);
		ranking.getColumn("ID").setPreferredWidth(260);
		ranking.getColumn("SCORE").setPreferredWidth(260);
		ranking.setRowHeight(row_h);
		//System.out.println(ranking.getRowCount());
		ranking.setBackground(new Color(255,255,255,200));
		ranking.setGridColor(new Color(0,0,0,0));
		ranking.disable();
		
		ranking.setFont(new Font("Default", Font.PLAIN, 20));
		
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		
		TableColumnModel column = ranking.getColumnModel();
		column.getColumn(0).setCellRenderer(center);
	}
}
