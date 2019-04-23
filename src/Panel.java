import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Panel extends JPanel { // Absolute
	ImageIcon backgroundImage = new ImageIcon("image\\game_background.png"); // 디폴트 값
	boolean isBackground = true;
	
	Panel() {} // 기본 생성자
	Panel(String src) {
		backgroundImage = new ImageIcon(src);
	}
	Panel(boolean isBackground) {
		this.isBackground = isBackground;
	}
	
	@Override
	public void setLayout(LayoutManager mgr) {
		super.setLayout(null);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(isBackground) {
			g.drawImage(backgroundImage.getImage(), 0, 0, 700, 800, null);
			setOpaque(false);
		}
		super.paintComponent(g);
	}
}
