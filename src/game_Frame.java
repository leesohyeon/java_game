import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;


class game_Frame extends JFrame implements KeyListener, Runnable {
	
	public static String user_id; // id
	public static int High_score = 0; // highest score
	public static boolean isStageCleared = true; // 스테이지 클리어 시 점수 계산을 확인을 위함
	public static Clip clip; // 오디오
	public static boolean KeySetting = true; // KeySetting : 기본은 방향키로 설정
	public static boolean music = true; // music : 기본은 on으로 설정
	public static boolean isItem[] = {false, false};
	public static int missile_speed; // 미사일 속도
	
	// 크기
	int f_width = 700, f_height = 800; // f_width = 프레임 가로 크기, f_height = 프레임 세로 크기
	int w_plane = 65, h_plane = 100; // w_plane = 비행기 가로 크기, h_plane = 비행기 세로 크기
	int w_missile = 4, h_missile = 33; // w_missile = 미사일 가로 크기, h_missile = 미사일 세로 크기
	int x_min = 5, y_min = 35; // x_min = 최소 x좌표, y_min = 최소 y좌표 
	int x_max = f_width - w_plane - 5, y_max = f_height - h_plane - 5; // x_max = 최대 x좌표, y_max = 최대 y좌표
	int x_user, y_user; // x_user = 유저 x좌표, y_user = 유저 y좌표
	int w_life = 38; // w_life = life 사이 값 
	int boss_size = 250;
	
	// 키보드, 마우스 관리
	boolean KeyUp = false; // 방향키 ↑, W키
	boolean KeyDown = false; // 방향키 ↓, S키
	boolean KeyLeft = false; // 방향키 ←, A키
	boolean KeyRight = false; // 방향키 →, D키
	boolean KeySpace = false; // Space키, 마우스 클릭
	boolean KeyAny = false; // 게임 오버 시 다음 화면 전환 용
	
	// 값 체크 변수
	boolean isPanel = true; // Panel이 있는 경우 오버라이딩 된 paint() 메소드 사용 x
	boolean isAlive = true; // 게임 종료 체크 변수
	boolean isEnter = true; // 스테이지 크리어 or 게임 오버 시 다음으로 진행 할 때 enter키 입력
	boolean isPause = false; // 일시정지 체크 변수
	boolean isBoss = false;
	boolean isBossCleared = false;
	
	Thread game; // 게임 실행 스레드
	Toolkit tk = Toolkit.getDefaultToolkit(); // 이미지 파일 로드
	Image buffImage; // 프레임 repaint()시 사용
	Graphics buffg; // 프레임 repaint()시 사용
	Image background_img; // 배경 이미지
	
	Image user_img; // 유저 비행기 이미지
	
	Image life_img; // 생명 이미지
	
	Image Missile_img; // 미사일 이미지 
	Image Mob_Missile_img; // 몹 미사일 이미지
	ArrayList<Missile> Missile_List = new ArrayList<>(); // 미사일 배열
	
	Image Mob_img[] = new Image[3]; // 몹 이미지
	ArrayList<Mob> Mob_List = new ArrayList<>(); // 몹 배열
	
	Image Item[] = new Image[3];
	ArrayList<Item> Item_List = new ArrayList<>();
	Image Itemed[] = new Image[2];
	
	Image boss_img;
	Image boss_missile_img;
	
	Missile ms; // 미사일 클래스 접근 키
	Mob mob; // 적 클래스 접근 키
	Item item;
	Boss boss;
	
	int by; // 게임 배경 움직이기 위한 y좌표	
	int cnt; // run() 내부의 무한 루프 카운트 -> 몹이 나타나는 시간
	int fire_cnt; // 공격 키 누르는 시간 카운트
	int mob_time; // 몹이 일정하게 나타나는 시간 
	int mob_cnt = 5; // 몹 개수
	int sleeping = 19; // 프로세스 실행 sleep 시간
	int fire_speed; // 미사일 발사 속도 제어
	int mob_speed; // 몹 속도
	int speed;
	int score; // 점수
	int user_hp; // 유저 hp
	int mob_hp; // 몹 hp
	int mob_score[] = {50, 100, 150}; // 몹 점수
	int stage_time = 1000 * 20; // 타이머 제어용 변수 -> 스테이지 : 20초
	int stage_cnt = 0; // 스테이지 카운트
	int stage_countdown = 3; // 스테이지 시작 시 카운트 다운
	int kill[] = {0, 0, 0}; // 몹 제거한 횟수
	int killed = 0; // 총 몹 제거 함수
	
	MainPanel panel_main = new MainPanel(Main.connection);
	Panel panel_how = new Panel("image\\how_to_play.png");
	
	Item_timer item_timer = new Item_timer();
	boolean item_end = true;
	long item_start = 0;
	long item_last = 0;
	int item_time = 1000 * 5;
	
	Stage_Timer stage_timer = new Stage_Timer(); // 새 스테이지로 넘어가기 위한 타이머
	long start = 0; // 타이머 시작 시간
	long last = 0; // 타이머 중지 시 남은 시간
	boolean pausing = false; // 다시 불러왔을 때 한 번만 타이머를 작동시키기 위한 제어 변수
	
	PreparedStatement ps; // SQL 작성
	Properties p; // ini 파일(설정 파일) read, write
	
	game_Frame() {
		setting();
		if(music)
			Sound("sound\\music.wav", true);
		else
			Sound("sound\\music.wav", false);
		start_window();
		  
		setTitle("Shooting Game");
		setSize(f_width, f_height);
		
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		setVisible(true);
	}
	
	// 설정 값 불러오기
	public void setting() {
		p = new Properties();
		try {
			p.load(new FileInputStream("setting.ini"));
			if(p.getProperty("music").equals("on"))
				music = true;
			else 
				music = false;
			if(p.getProperty("key").equals("true"))
				KeySetting = true;
			else
				KeySetting = false;
		} catch (FileNotFoundException e) {
			// 기본 값 설정
			p.setProperty("music", "on");
			p.setProperty("key", "true");
			try {
				p.store(new FileOutputStream("setting.ini"), "done");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 게임 전 패널 창
	public void start_window() {
		// 시작 창
		Panel panel_start = new Panel("image\\start_background.png");
		panel_start.setPreferredSize(new Dimension(700, 800));
		
		// 로그인 창
		LoginPanel panel_login = new LoginPanel(this, panel_main, Main.connection);
		// 회원가입 창
		JoinPanel panel_join = new JoinPanel(this, panel_login, Main.connection);
		
		JButton btn_login = new JButton();
		btn_login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				getContentPane().add(panel_login);
				revalidate();
				repaint();
			}
		});
		btn_login.setOpaque(false);
		btn_login.setBorderPainted(false);
		btn_login.setBackground(Color.WHITE);
		btn_login.setForeground(Color.WHITE);
		btn_login.setBounds(20, 500, 660, 60);
		
		JButton btn_join = new JButton("JOIN");
		btn_join.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				getContentPane().add(panel_join);
				revalidate();
				repaint();
			}
		});
		btn_join.setOpaque(false);
		btn_join.setBackground(Color.WHITE);
		btn_join.setForeground(Color.WHITE);
		btn_join.setBounds(20, 710, 660, 40);
		panel_login.add(btn_join);
		
		panel_start.add(btn_login);
				
		JButton btn_backStart_login = new JButton("BACK");
		btn_backStart_login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getContentPane().removeAll();
				getContentPane().add(panel_start);
				revalidate();
				repaint();
			}
		});
		btn_backStart_login.setOpaque(false);
		btn_backStart_login.setBackground(Color.WHITE);
		btn_backStart_login.setForeground(Color.WHITE);
		btn_backStart_login.setBounds(10, 10, 80, 50);
		panel_login.add(btn_backStart_login);
		
		panel_login.id.setFocusable(true);
		panel_login.id.requestFocus();
		
		JButton btn_backStart_join = new JButton("BACK");
		btn_backStart_join.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getContentPane().removeAll();
				getContentPane().add(panel_start);
				revalidate();
				repaint();
			}
		});
		btn_backStart_join.setOpaque(false);
		btn_backStart_join.setBackground(Color.WHITE);
		btn_backStart_join.setForeground(Color.WHITE);
		btn_backStart_join.setBounds(10, 10, 80, 50);
		panel_join.add(btn_backStart_join);
		
		panel_join.id.setFocusable(true);
		panel_join.id.requestFocusInWindow();
		
		panel_main.setPreferredSize(new Dimension(700, 800));
		
		JButton btn_backMain = new JButton("BACK");
		btn_backMain.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getContentPane().removeAll();
				getContentPane().add(panel_main);
				revalidate();
				repaint();
			}
		});
		btn_backMain.setOpaque(false);
		btn_backMain.setBackground(Color.WHITE);
		btn_backMain.setForeground(Color.WHITE);
		btn_backMain.setBounds(600, 700, 80, 50);
		panel_how.add(btn_backMain);
		
		JButton btn_how = new JButton("HOW TO PLAY");
		btn_how.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				getContentPane().add(panel_how);
				revalidate();
				repaint();
			}
		});
		btn_how.setOpaque(false);
		btn_how.setBackground(Color.WHITE);
		btn_how.setForeground(Color.WHITE);
		btn_how.setBounds(20, 660, 660, 40);
		
		JButton btn_start = new JButton("START");
		btn_start.addActionListener(new ActionListener() {
			@Override
			
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				isPanel = false;
				init();
				start();
			}
		});
		btn_start.setOpaque(false);
		btn_start.setBackground(Color.WHITE);
		btn_start.setForeground(Color.WHITE);
		btn_start.setBounds(20, 710, 660, 40);
		
		panel_main.add(btn_how);
		panel_main.add(btn_start);
		
		add(panel_start);
	}
	
	// 기본 세팅
	public void init(){ 
		user_img = tk.getImage("image\\user.png");
		user_img = user_img.getScaledInstance(w_plane, h_plane, java.awt.Image.SCALE_SMOOTH);
		
		life_img = tk.getImage("image\\life.png");
		
		Missile_img = tk.getImage("image\\user_attack.png"); //미사일 이미지를 불러온다.
		Missile_img = Missile_img.getScaledInstance(w_missile, h_missile, java.awt.Image.SCALE_SMOOTH);
		Mob_Missile_img = tk.getImage("image\\mob_attack.png");
		Mob_Missile_img = Mob_Missile_img.getScaledInstance(w_missile, h_missile, java.awt.Image.SCALE_SMOOTH);
		
		for(int i = 0; i < Mob_img.length; i++) {
			Mob_img[i] = tk.getImage("image\\mob"+(i+1)+".png");
			Mob_img[i] = Mob_img[i].getScaledInstance(w_plane, h_plane, java.awt.Image.SCALE_SMOOTH);
		}
		
		Item[0] = tk.getImage("image\\power.png");
		Item[1] = tk.getImage("image\\speed.png");
		Item[2] = tk.getImage("image\\life.png");
		
		Itemed[0] = tk.getImage("image\\power_user.png");
		Itemed[1] = tk.getImage("image\\speed_user.png");
		Itemed[0] = Itemed[0].getScaledInstance(w_plane, h_plane, java.awt.Image.SCALE_SMOOTH);
		Itemed[1] = Itemed[1].getScaledInstance(w_plane, h_plane, java.awt.Image.SCALE_SMOOTH);
		
		boss_img = tk.getImage("image\\boss.png");
		boss_img = boss_img.getScaledInstance(boss_size, boss_size, java.awt.Image.SCALE_SMOOTH);
		boss_missile_img = tk.getImage("image\\boss_attack.png");
		
		background_img = tk.getImage("image\\game_background.png");
		by = -800;
		
		cnt = 0;
		score = 0;
		user_hp = 3;
		mob_hp = 1;
		missile_speed = 9; // 11
		fire_speed = 9; // 7
		mob_speed = 7;
		speed = 5;
		stage_cnt = 0;
		mob_time = 200;
		fire_cnt = 0;
		killed = 0;
		
		for(int i = 0; i < kill.length; i++) {
			kill[i] = 0;
		}
		
		isAlive = true;
		isStageCleared = true;
		isEnter = true;
		
		Missile_List.clear();
		Mob_List.clear();
		x_user = x_max / 2;
		y_user = y_max;
		
		boss = new Boss((f_width - boss_size) / 2, 10);
	}
	
	public void start(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setFocusable(true);
		requestFocusInWindow();
		
		addKeyListener(this);
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				KeySpace = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				KeySpace = true;
				fire_cnt++;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				isPause = true;
				stage_timer.cancel();
				last = start - System.currentTimeMillis();
				pausing = true;
				item_timer.cancel();
				item_last = item_start - System.currentTimeMillis();
			}
			
			@Override
			public void windowClosing(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {
				isPause = false;
				if(item_last > 0) {
					item_timer.schedule(item_last);
					item_start = System.currentTimeMillis() + item_last;
				}
			}
		});
		
		game = new Thread(this); 
		game.start(); 
	}
	
	public void stage_init() { // 스테이지 변수 초기화
		if(stage_cnt % 3 == 1 && stage_cnt != 1) {
			mob_speed += 2;
			mob_time = mob_time / 5 * 4;
		}
		if(user_hp < 4 && stage_cnt != 1) user_hp++;
		
		cnt = 0;
		isAlive = true;
		isStageCleared = true;
		Missile_List.clear();
		Mob_List.clear();
		x_user = x_max / 2;
		y_user = y_max;
		for(int i = 0; i < isItem.length; i++) {
			isItem[i] = false;
		}
		item_timer.cancel();
	}
	
	public void run(){ 
		try{ 
			while(isAlive){
				Thread.sleep(1);
				if(!isPause) {
					if(pausing && last > 0) {
						stage_timer.schedule(last);
						start = System.currentTimeMillis() + last;
						pausing = false;
					}
					if(isStageCleared) {
						if(stage_cnt % 3 == 0) {
							isBoss = true;
							boss.x = (f_width - boss_size) / 2;
							boss.y = 10;
							boss.speed += 2;
							boss.hp += 10;
						} else if(isBossCleared) {
							stage_cnt--;
						}
						
						stage_cnt++;
						stage_init();
						
						if(stage_cnt > 1) {
							repaint();
							do {
								Thread.sleep(20);
							}while(!(KeyAny));
							isEnter = true;
							if(isBossCleared) {
								isBoss = false;
								isBossCleared = false;
							}
						} 
						else if(stage_cnt == 1) 
							isBoss = false;
						
						do {
							repaint();
							Thread.sleep(1000);
						} while(stage_countdown-- > 1);
						
						isEnter = false;
						isStageCleared = false;
						stage_countdown = 3;
						
						if(!isBoss) {
							stage_timer.schedule(stage_time);
							start = System.currentTimeMillis() + stage_time;
						}
					}
					
					KeyProcess(); 
					
					MissileProcess(); // 미사일 처리 메소드 실행
					if(isBoss) Boss();
					else MobProcess(); // 적 처리 메소드 실행
					ItemProcess();
					
					repaint(); 
					Thread.sleep(sleeping);
					cnt++;
				} // if(!isPause)
			} // while(isAlive)
			repaint();
			while(!(KeyAny)) {
				Thread.sleep(20);
			}
		}
		catch (Exception e){
			e.getStackTrace();
		}
		if(score > High_score) {
			try {
				if(High_score == 0) {
					ps = Main.connection.prepareStatement("insert into ranking values(?,?)");
					ps.setString(1, user_id);
					ps.setInt(2, score);
				} else {
					ps = Main.connection.prepareStatement("update ranking set high_score = ? where id = ?");
					ps.setInt(1, score);
					ps.setString(2, user_id);
				}
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "CONGRATULATIONS! NEW HIGH SCORE!", "NEW HIGH SCORE", JOptionPane.INFORMATION_MESSAGE);
				High_score = score;
				System.out.println(High_score);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			panel_main.high_score.setText("HIGHEST : " + String.format("%09d", game_Frame.High_score));
			panel_main.ranking();
			panel_main.TableSet();
			panel_main.repaint();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		isPanel = true;
		add(panel_main);
		repaint();
	}
		
	public void MissileProcess(){ // 미사일 처리 메소드
		if(!(KeySpace) && fire_cnt > 1) { // 단일 발사
			ms = new Missile(x_user + (w_plane / 2) - 3, y_user - 70, true); 	// 이미지 크기를 감안한 미사일 발사 좌표는 수정됨.
			Missile_List.add(ms);   // 해당 미사일 추가
			fire_cnt = 0;
		}
		else if (KeySpace && fire_cnt % fire_speed == 0){ // 스페이스바 키 상태가 true -> 연속 발사
			ms = new Missile(x_user + (w_plane / 2) - 3, y_user - 70, true); 	// 이미지 크기를 감안한 미사일 발사 좌표는 수정됨.
			Missile_List.add(ms); // 해당 미사일 추가
			fire_cnt = 0;
		}
		
		if(isBoss) {
			BossMissileCrash();
		}
		else {
			MissileCrash();
		}
	}
	
	public void BossMissileCrash() {
		for(int i = 0; i < Missile_List.size(); i++) {
			ms = (Missile) Missile_List.get(i);
			ms.move();
			if (ms.y < 0 || ms.y > y_max) Missile_List.remove(i);
			
			if(ms.isBoss) {
				if(!(isItem[0]) && Crash(ms.x, ms.y, x_user, y_user, w_missile, h_missile, w_plane, h_plane)) {
					Missile_List.remove(i);
					if(--user_hp <= 0) isAlive = false;
				}
			}
			else {
				if (Crash(ms.x, ms.y, boss.x, boss.y, w_missile, h_missile, boss_size, boss_size)){
					Missile_List.remove(i);
					if(--boss.hp <= 0) {
						isStageCleared = true;
						isBossCleared = true;
					}
				}
			}
		} // for i
	}
	
	public void MissileCrash() {
		boolean isRemove = false; // 중복 제거 방지용 체크 변수
		for(int i = 0; i < Missile_List.size(); i++) {
			isRemove = false;
			ms = (Missile) Missile_List.get(i);
			ms.move();
			if ( ms.y < 0 || ms.y > y_max ){ 
				Missile_List.remove(i); 
			}
			
			if(ms.isUser) {
				for (int j = 0; j < Mob_List.size(); j++){
					mob = (Mob) Mob_List.get(j);
					if (Crash(ms.x, ms.y, mob.x, mob.y, w_missile, h_missile, w_plane, h_plane)){
						if(!isRemove) {
							Missile_List.remove(i);
							isRemove = true;
						}
						if(--mob.hp <= 0) {
							Mob_List.remove(j);
							score += mob_score[mob.diff];
							kill[mob.diff]++;
							killed++;
						}
					}
				} // for j
			} //if
			else
				if(!(isItem[0]) && Crash(ms.x, ms.y, x_user, y_user, w_missile, h_missile, w_plane, h_plane)) {
					Missile_List.remove(i);
					if(--user_hp <= 0) isAlive = false;
				}
		} // for i
	
	}
	
	public void MobProcess(){//적 행동 처리 메소드
		for (int i = 0 ; i < Mob_List.size(); i++){ 
			mob = (Mob)(Mob_List.get(i)); 
			mob.move(); 
			if(mob.y > y_max){ 
				Mob_List.remove(i); 
			}
			
			if(!(isItem[0]) && Crash(x_user, y_user, mob.x, mob.y, w_plane, h_plane, w_plane, h_plane)) {
				if(--user_hp <= 0) isAlive = false;
				Mob_List.remove(i);
			}
		}

		if (cnt % mob_time == 0){ // mob_time이 될 때마다 새 적 생성
			int x_mob; // x좌표
			Random random = new Random();
			x_mob = random.nextInt(x_max);
			
			// 몹 랜덤값으로 위치 생성
			for(int i = 1; i <= mob_cnt; i++) {
				x_mob = random.nextInt(x_max);
				
				int diff = stage_cnt % 3 == 0 ? 3 : stage_cnt % 3;
				switch(i % diff) {
					case 0:
						mob = new Mob(x_mob, 0, mob_hp, 0);
						break;
					case 1:
						mob = new Mob(x_mob, 0, mob_hp + 1, 1);
						break;
					case 2:
						mob = new Mob(x_mob, 0, mob_hp + 1, 2);
						ms = new Missile(x_mob + (w_plane / 2) - 3, h_plane + 5, false);
						Missile_List.add(ms);
						break;
				}
				Mob_List.add(mob);
			}
		} 
	}
	
	public boolean Crash(int x1, int y1, int x2, int y2, int w1, int h1, int w2, int h2){
		//﻿충돌 판정을 위한 새로운 Crash 메소드를 만들었습니다.
		//판정을 위해 충돌할 두 사각 이미지의 좌표 및 
		//넓이와 높이값을 받아들입니다.
		//여기서 이미지의 넓이, 높이값을 계산하기 위해 밑에 보면
		//이미지 크기 계산용 메소드를 또 추가했습니다.
		boolean check = false;

		if ( Math.abs( ( x1 + w1 / 2 )  - ( x2 + w2 / 2 ))  <  ( w2 / 2 + w1 / 2 )  
				&& Math.abs( ( y1 + h1 / 2 )  - ( y2 + h2 / 2 ))  <  ( h2 / 2 + h1/ 2 ) ){
			check = true;
		}
		else{ 
			check = false;
		}

		return check;
	}
	
	public void ItemProcess() {
		if(cnt % 250 == 0) {
			int x_item, diff;
			Random random = new Random();
			x_item = random.nextInt(x_max);
			diff = random.nextInt(Item.length);
			
			item = new Item(x_item, diff);
			Item_List.add(item);
		}
		
		for(int i = 0; i < Item_List.size(); i++) {
			item = Item_List.get(i);
			item.move();
			if(item.y > y_max) {
				Item_List.remove(i);
			}
			if(Crash(item.x, item.y, x_user, y_user, 32, 32, w_plane, h_plane)) {
				Item_List.remove(i);
				
				if(item.diff < 2) {
					if(!(isItem[1]) && item.diff == 1) {
						speed +=2;
						missile_speed += 4;
						fire_speed -= 2;
						item_end = false;
					}
					isItem[item.diff] = true;
					item_timer.schedule(item_time, item.diff);
					start = System.currentTimeMillis() + item_time;
				}
				else {
					if(user_hp < 4)
						user_hp++;
				}
			} // if
		} // for i
		
		if(!(item_end) && !isItem[1]) {
			speed -=2;
			missile_speed -= 4;
			fire_speed += 2;
			item_end = true;
		}
	}
	
	public void Boss() {
		boss.move();
		if(cnt % 25 == 0) {
			int x_boss;
			Random random = new Random();
			x_boss = random.nextInt(boss_size);
				
			ms = new Boss_Missile(boss.x + x_boss + 130, boss.y + boss_size + 20);
			Missile_List.add(ms);
			
			ms = new Boss_Missile(boss.x + x_boss, boss.y + boss_size + 20);
			Missile_List.add(ms);
			
			ms = new Boss_Missile(boss.x + x_boss - 130, boss.y + boss_size + 20);
			Missile_List.add(ms);
			
		}
	}
	
	// 그리기
	public void paint(Graphics g){
		if(isPanel) {
			super.paint(g);
		}
		else {
			buffImage = createImage(f_width, f_height); 
			buffg = buffImage.getGraphics();
			
			update(g);
		} 
	}
	
	public void update(Graphics g){
		Draw_Background();
		Draw_Life();
		Draw_StatusText();
		
		if(isStageCleared) {
			Draw_User();
			if(isEnter)
				Draw_Stage();
			else
				Draw_Result();
		}
		else if(isAlive) {
			Draw_User();
			Draw_Mob(); // 그려진 몹 가져와 실행
			Draw_Missile(); // 그려진 미사일 가져와 실행
			Draw_Item();
		}
		else Draw_Result();
		
		g.drawImage(buffImage, 0, 0, this); 
	}
	
	public void Draw_Background(){
		buffg.clearRect(0, 0, f_width, f_height);

		if (by < 0){
			buffg.drawImage(background_img, 0, by, this);
			by += 1;
		}
		else { 
			by = -800; 
		}
	}
	
	public void Draw_User(){ 
		buffg.drawImage(user_img, x_user, y_user, this);
		if(isItem[0]) 
			buffg.drawImage(Itemed[0], x_user, y_user, this);
		else if(isItem[1])
			buffg.drawImage(Itemed[1], x_user, y_user, this);
	}
	
	public void Draw_Missile(){
		for (int i = 0 ; i < Missile_List.size(); i++){
			ms = (Missile) (Missile_List.get(i));
			if(ms.isBoss)
				buffg.drawImage(boss_missile_img, ms.x, ms.y, this);
			else if(ms.isUser) 
				buffg.drawImage(Missile_img, ms.x, ms.y, this);
			else 
				buffg.drawImage(Mob_Missile_img, ms.x, ms.y, this);
		}
	}
	
	public void Draw_Mob(){
		if(isBoss)
			buffg.drawImage(boss_img, boss.x, boss.y, this);
		else {
	 		for (int i = 0; i < Mob_List.size(); i++){
				mob = (Mob)(Mob_List.get(i));
				buffg.drawImage(Mob_img[mob.diff], mob.x, mob.y, this);
			}
		}
	}
	
	public void Draw_Item() {
		for(int i = 0; i < Item_List.size(); i++) {
			item = Item_List.get(i);
			buffg.drawImage(Item[item.diff], item.x, item.y, this);
		}
	}
	
	public void Draw_Life() {
		int i = -1;
		while(++i < user_hp) 
			buffg.drawImage(life_img, 15 + i*w_life, 40, this);
	}
	
	
	public void Draw_StatusText(){ //상태 체크용  텍스트를 그립니다.
		buffg.setFont(new Font("Default", Font.BOLD, 20));
		//폰트 설정을 합니다.  기본폰트, 굵게, 사이즈  20
		buffg.setColor(Color.WHITE);
		// 글자 색 설정
		buffg.drawString("SCORE " + String.format("%09d", score), x_max - 130, 60);
	}
	
	// 새 스테이지 시작 시 스테이지 그림
	public void Draw_Stage() {
		buffg.setFont(new Font("Default", Font.BOLD, 80));
		if(isBoss) {
			buffg.setColor(Color.RED);
			buffg.drawString("BOSS", f_width / 2 - 120, f_height / 2 - 50);
		}
		else {
			buffg.setColor(Color.WHITE);
			buffg.drawString("STAGE", f_width / 2 - 220 , f_height / 2 - 50);
			buffg.setColor(Color.CYAN);
			buffg.drawString(String.format("%2d", stage_cnt), f_width / 2 + 140, f_height / 2 - 50); // 스테이지 숫자
		}
		buffg.setColor(Color.ORANGE);
		buffg.drawString(stage_countdown + "", f_width / 2 - 20, f_height / 2 + 50); // 카운트 다운
	}
	
	// 스테이지 클리어 시 or 게임 오버 시 결과 창
	public void Draw_Result() {
		if(isAlive) {
			if(isBoss && isBossCleared) score += (stage_cnt - 1) / 3 * 10000;
			else score += (stage_cnt - 1) * 1000;
			buffg.setFont(new Font("Default", Font.BOLD, 80));
			buffg.setColor(Color.WHITE);
			buffg.drawString("STAGE", 60, 250);
			
			buffg.setColor(Color.CYAN);
			buffg.drawString("CLEAR", f_width / 2 + 10, 250);
		}
		else {
			buffg.setFont(new Font("Default", Font.BOLD, 80));
			buffg.setColor(new Color(240, 0, 0));
			buffg.drawString("GAME OVER", 110, 250);				
		}

		buffg.setColor(new Color(255, 255, 255, 150));
		buffg.fillRect(50, 270, f_width - 100, f_height - 500);
		
		buffg.setFont(new Font("Default", Font.BOLD, 50));
		buffg.setColor(new Color(66, 33, 0));
		buffg.drawString("Result", f_width / 2 - 75, 330);
		 
		buffg.setFont(new Font("Default", Font.BOLD, 40));
		buffg.drawString("MOB X ", 75, 400);
		
		buffg.drawString("STAGE CLEAR", 75, 475);
		
		buffg.drawString("RESULT", 75, 550);
		
		if(isBoss && isBossCleared) {
			buffg.drawString("= " + String.format("%09d", (stage_cnt - 1) / 3 * 10000), f_width / 2 + 50, 475);
			
			buffg.setColor(Color.RED);
			buffg.drawString(String.format("%3d", 1), 250, 400); // 몹 처리한 숫자
		}
		else {
			buffg.drawString("= " + String.format("%09d", kill[0] * mob_score[0] + kill[1] * mob_score[1] + kill[2] * mob_score[2]), f_width / 2 + 50, 400);
			
			buffg.drawString("= " + String.format("%09d", (stage_cnt - 1) * 1000), f_width / 2 + 50, 475);
			
			buffg.setColor(Color.RED);
			buffg.drawString(String.format("%3d", killed), 250, 400); // 몹 처리한 숫자
			
			buffg.setColor(Color.CYAN);
			buffg.drawString(String.format("%2d", stage_cnt - 1), 370, 475); // 클리어한 스테이지 숫자
		}
		
		buffg.setColor(Color.WHITE);
		buffg.drawString("= " + String.format("%09d", score), f_width / 2 + 50, 550);
		
		buffg.drawString("PRESS ENTER TO CONTINUE", 70, 630);
	}
	
	// 키 컨트롤
	public void keyPressed(KeyEvent e){
		if(KeySetting) {
			switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
					KeyUp = true;
					break;
				case KeyEvent.VK_DOWN:
					KeyDown = true;
					break;
				case KeyEvent.VK_LEFT:
					KeyLeft = true;
					break;
				case KeyEvent.VK_RIGHT:
					KeyRight = true;
					break;
				
				case KeyEvent.VK_SPACE: // 스페이스키 입력 처리 추가
					KeySpace = true;
					break;
					
				case KeyEvent.VK_ENTER:
					KeyAny = true;
			}
		}
		else {
			switch(e.getKeyCode()){
				case KeyEvent.VK_W:
					KeyUp = true;
					break;
				case KeyEvent.VK_S:
					KeyDown = true;
					break;
				case KeyEvent.VK_A:
					KeyLeft = true;
					break;
				case KeyEvent.VK_D:
					KeyRight = true;
					break;
				
				case KeyEvent.VK_SPACE: 
					KeySpace = true;
					break;
					
				case KeyEvent.VK_ENTER:
					KeyAny = true;
			}
		}
	}
	public void keyReleased(KeyEvent e){
		if(KeySetting) {
			switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
					KeyUp = false;
					break;
				case KeyEvent.VK_DOWN:
					KeyDown = false;
					break;
				case KeyEvent.VK_LEFT:
					KeyLeft = false;
					break;
				case KeyEvent.VK_RIGHT:
					KeyRight = false;
					break;
				
				case KeyEvent.VK_SPACE: 
					KeySpace = false;
					break;
				
				case KeyEvent.VK_ENTER:
					KeyAny = false;
			}
		}
		else {
			switch(e.getKeyCode()){
				case KeyEvent.VK_W:
					KeyUp = false;
					break;
				case KeyEvent.VK_S:
					KeyDown = false;
					break;
				case KeyEvent.VK_A:
					KeyLeft = false;
					break;
				case KeyEvent.VK_D:
					KeyRight = false;
					break;
				
				case KeyEvent.VK_SPACE: // 스페이스키 입력 처리 추가
					KeySpace = false;
					break;
				
				case KeyEvent.VK_ENTER:
					KeyAny = false;
			}
		}
	}
	public void keyTyped(KeyEvent e){}
	
	public void KeyProcess(){
		if(KeyUp == true && y_user > y_min) y_user -= speed;
		if(KeyDown == true && y_user < y_max) y_user += speed;
		if(KeyLeft == true && x_user > x_min) x_user -= speed;
		if(KeyRight == true && x_user < x_max) x_user += speed;
		if(KeySpace) fire_cnt++;
	}
	
	//사운드 재생용 메소드
	public void Sound(String file, boolean start){
		//사운드 파일을 받아 해당사운드를 재생시킴
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			clip = AudioSystem.getClip();
			clip.open(ais);
			
			if(start) // true일 경우에만 재생
				clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}