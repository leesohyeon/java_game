import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class SettingDialog extends JDialog {
	
	String src[] = {"image\\key.png", "image\\key_wasd.png"};
	JLabel key;
	Properties p = new Properties();
	
	public SettingDialog() {
		setTitle("Setting");
		setSize(600,400);
		set();
		addWindowListener(window);
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
	}
	
	public void set() {
		Panel panel = new Panel(false);
		Panel panel_sound = new Panel(false);
		Panel panel_key = new Panel(false);
		
		JButton btn_sound = new JButton("sound");
		btn_sound.setOpaque(false);
		btn_sound.setBackground(Color.BLACK);
		btn_sound.setForeground(Color.BLACK);
		btn_sound.setBounds(10,10,280,50);
		btn_sound.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(panel_key);
				panel.add(panel_sound);
				revalidate();
				repaint();
			}
		});
		
		JButton btn_key = new JButton("key");
		btn_key.setOpaque(false);
		btn_key.setBackground(Color.BLACK);
		btn_key.setForeground(Color.BLACK);
		btn_key.setBounds(290,10,280,50);
		btn_key.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(panel_sound);
				panel.add(panel_key);
				revalidate();
				repaint();
			}
		});
		
		JRadioButton on = new JRadioButton("ON");
		JRadioButton off = new JRadioButton("OFF");
		
		if(game_Frame.music) 
			on.setSelected(true);
		else
			off.setSelected(true);
		
		on.setBounds(50, 50, 50, 50);
		off.setBounds(350, 50, 50, 50);
		
		on.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					game_Frame.clip.start();
					game_Frame.music = true;
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					game_Frame.clip.stop();
					game_Frame.music = false;
				}
			}
		});
		
		ButtonGroup group = new ButtonGroup();
		group.add(on);
		group.add(off);
		
		panel_sound.add(on);
		panel_sound.add(off);
		panel_sound.setBounds(0, 100, 600, 400);
		
		if(game_Frame.KeySetting) 
			key = new JLabel(new ImageIcon(src[0]));
		else 
			key = new JLabel(new ImageIcon(src[1]));
		key.setBounds(230, 50, 120, 100);
		
		JLabel left = new JLabel(new ImageIcon("image\\left.png"));
		left.setBounds(150, 70, 64, 64);
		left.addMouseListener(mouse);
		
		JLabel right = new JLabel(new ImageIcon("image\\right.png"));
		right.setBounds(360, 70, 64, 64);
		right.addMouseListener(mouse);		
		
		panel_key.add(key);
		panel_key.add(left);
		panel_key.add(right);
		panel_key.setBounds(0, 100, 600, 400);
		
		panel.add(btn_sound);
		panel.add(btn_key);
		panel.add(panel_sound);
		add(panel);
		
		
	}
	
	MouseListener mouse = new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {}
		
		@Override
		public void mousePressed(MouseEvent e) {}
		
		@Override
		public void mouseExited(MouseEvent e) {}
		
		@Override
		public void mouseEntered(MouseEvent e) {}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(game_Frame.KeySetting) {
				key.setIcon(new ImageIcon(src[1]));
				game_Frame.KeySetting = false;
			} else {
				key.setIcon(new ImageIcon(src[0]));
				game_Frame.KeySetting = true;
			}
		}
	};
	
	WindowListener window = new WindowListener() {
		
		@Override
		public void windowOpened(WindowEvent e) {}
		
		@Override
		public void windowIconified(WindowEvent e) {}
		
		@Override
		public void windowDeiconified(WindowEvent e) {}
		
		@Override
		public void windowDeactivated(WindowEvent e) {}
		
		@Override
		public void windowClosing(WindowEvent e) {
			if(game_Frame.music) 
				p.setProperty("music", "on");
			else
				p.setProperty("music", "off");
			
			if(game_Frame.KeySetting) 
				p.setProperty("key", "true");
			else 
				p.setProperty("key", "false");
			
			try {
				p.store(new FileOutputStream("setting.ini"), "done");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		@Override
		public void windowClosed(WindowEvent e) {}
		
		@Override
		public void windowActivated(WindowEvent e) {}
	};
	
}
