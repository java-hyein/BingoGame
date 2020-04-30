package AwtSwing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class BingoGame {
	static JPanel panelNorth; //Top View
	static JPanel panelCenter; //Game View
	static JLabel labelMessage;
	static JButton[] buttons = new JButton[16]; //4X4 버튼 16개
	static String[] images= {
			"fruit01.png","fruit02.png","fruit03.png","fruit04.png",
			"fruit05.png","fruit06.png","fruit07.png","fruit08.png",
			"fruit01.png","fruit02.png","fruit03.png","fruit04.png",
			"fruit05.png","fruit06.png","fruit07.png","fruit08.png",	
	};
	static int openCount = 0; //열린 카드 개수 : 0,1,2
	static int buttonIndexSave1 = 0; // 첫번째 열린 카드의 인덱스 0~15
	static int buttonIndexSave2 = 0;
	static Timer timer;
	static int tryCount = 0; // 몇번 시도해서 성공했는지 알려주는 것
	static int successCount = 0; //BingoCount : 0~8
	
	
	static class MyFrame extends JFrame implements ActionListener{
		public MyFrame(String title) {
			super(title);
			this.setLayout(new BorderLayout());  //swing의 기본 프레임 BorderLayout이다.
			this.setSize(400,500);  // 상하좌우로 나누어진 layout을 말한다.
			this.setVisible(true);  //이게 있어야 실제 보이게 된다.
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 실제로 창이 닫힌다.
			
			// UI PART START
			initUI(this); // Screen UI Set.
			mixCard(); // 카드 랜덤 섞기.
			
			this.pack(); // 컨테이너의 크기를 구성 요소 들의 크기로 설정
		}
		
		static void playSound(String filename) {
			File file = new File("./wav/" + filename);
			if(file.exists()) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("File Not Found");
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(openCount == 2) { // 카드가 2개 일때 까지만 열기 (2개이상 안열림)
				return;
			}
			
			JButton btn = (JButton)e.getSource(); //눌러진 버튼객체를 알 수있음.
			int index = getButtonIndex(btn);
			//System.out.println("buttonIndex : " + index);
			btn.setIcon(changeImage(images[index]));
			
			openCount++;
			if(openCount == 1) { // 첫번째 연 카드
				buttonIndexSave1 = index;
			}else if(openCount == 2) { //두번째 연 카드
				buttonIndexSave2 = index;
				tryCount++;
				labelMessage.setText("Find Same Fruit! " + "TRY : " + tryCount);
				
				// 판정 로직 (맞는지 확인)
				boolean isBingo = checkCard(buttonIndexSave1, buttonIndexSave2);
				if(isBingo == true) {
					playSound("bingo.wav");
					openCount = 0;
					successCount++;
					if(successCount == 8) {
						labelMessage.setText("SUCCESS!!!! TRY : " + tryCount);
						playSound("clapping.wav");
					}
				}else {
					backtoQuestion();
				}
			}
		}
		public void backtoQuestion(){
			//Timer set 0.5 second
			timer = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("timer is working");
					
					playSound("fail.wav");
					openCount = 0;
					buttons[buttonIndexSave1].setIcon(changeImage("question.png"));
					buttons[buttonIndexSave2].setIcon(changeImage("question.png"));
					timer.stop();
				}
			});
			timer.start();
		}
		
		public boolean checkCard(int index1, int index2) {
			if(index1 == index2) {
				return false;
			}
			if(images[index1].equals(images[index2])) {
				return true;
			}else {
				return false;
			}
		}
		
		public int getButtonIndex(JButton btn) { // 어떤 버튼이 들어왔는지 확인
			int index = 0;
			for (int i = 0; i < buttons.length; i++) {
				if(buttons[i] == btn) { //Same Instance???
					index = i;
				}
			}
			return index;
		}
	}
	
	static void mixCard() { // 카드 섞는 메소드
		Random rand = new Random();
		for (int i = 0; i < 1000; i++) {
			int random = rand.nextInt(15) + 1; // 1~15 까지의 카드 인덱
			//섞기
			String temp = images[0];
			images[0] = images[random];
			images[random] = temp;
		}
	}
	
	static void initUI(MyFrame myFrame) {
		panelNorth = new JPanel();
		panelNorth.setPreferredSize(new Dimension(400,100));
		panelNorth.setBackground(Color.DARK_GRAY);
		labelMessage = new JLabel("Find Same Fruit! " + "Try 0"); // TRY 부분은 시도회수
		labelMessage.setPreferredSize(new Dimension(400,100));
		labelMessage.setForeground(Color.white);
		labelMessage.setFont(new Font("Monaco", Font.BOLD, 20));
		labelMessage.setHorizontalAlignment(JLabel.CENTER); //가운데정렬 
		panelNorth.add(labelMessage);
		myFrame.add("North", panelNorth);
		
		
		panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(4,4));
		panelCenter.setPreferredSize(new Dimension(400,400));
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton();
			buttons[i].setPreferredSize(new Dimension(100,100));
			buttons[i].setIcon(changeImage("question.png"));
			buttons[i].addActionListener(myFrame);
			panelCenter.add(buttons[i]);
		}
		myFrame.add("Center",panelCenter);
	}
	static ImageIcon changeImage(String filename) { // 이미지가 들어오면 사이즈 바꿔주는 메서드
		ImageIcon icon = new ImageIcon("./img/" + filename);
		Image originImage = icon.getImage();
		Image changedImage = originImage.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		ImageIcon icon_new = new ImageIcon(changedImage);
		return icon_new;
	}
	
	public static void main(String[] args) {
		new MyFrame("Bingo Game!!"); // 제목
		
	}

}
