package AwtSwing;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;

import javax.swing.*;
import javax.sound.sampled.*;

public class BingoGame {
	static JPanel panelNorth;   // 위
	static JPanel panelCenter;  // 게임창
	static JLabel LabelMessage;
	static JButton[] buttons = new JButton[16];    // 4*4 16개를 의미 버튼
	static String[] images = {
		"fruit01.png", "fruit02.png", "fruit03.png", "fruit04.png",	
		"fruit05.png", "fruit06.png", "fruit07.png", "fruit08.png",
		"fruit01.png", "fruit02.png", "fruit03.png", "fruit04.png",	
		"fruit05.png", "fruit06.png", "fruit07.png", "fruit08.png",
	};
	static int openCount = 0;    // 눌렀을때 열린카드 카운트 : 0 , 1, 2
	static int buttonIndexSave1 = 0;	// 첫번째로 열린 카드 0~15 값을 가짐
	static int buttonIndexSave2 = 0;	// 두번째로 열린 카드 0~15 값을 가짐
	static Timer timer;
	static int tryCount = 0;  // 몇번 시도해서 성공했는지
	static int successCount = 0;  // 빙고 카운트 0에서부터 8까지 8개가 되면 성공
	
	
	static class MyFrame extends JFrame implements ActionListener{
		public MyFrame(String title) {
			super(title);
			this.setLayout(new BorderLayout());
			this.setSize(400,500);  // 상단에 100픽셀은 타이틀 400 x 400 화면임
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			initUI(this);	// 화면 UI 세팅 // this는 JFrame에 넣는과정
			
			mixCard();	// 카드를 섞어주는과정
			
			this.pack(); // 비어있는 공간을 자리잡아줄때
			
		}
		static void playSound(String filename) {
			File file = new File("./WAV/"+filename);
			if(file.exists()) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("File Not Found!");
			}
		}
		
		@Override      // 버튼이 눌려지는 상황이 발생되면 여기로 옴
		public void actionPerformed(ActionEvent e) {
			
			if(openCount==2) {  // 2개가 오픈되면 더이상 열리지않도록 리턴
				return;
			}
			
			JButton btn = (JButton)e.getSource();
			int index = getButtonIndex(btn);     // 몇번 인덱스인지 받음
			btn.setIcon(changeImage(images[index]));
			
			openCount++;
			if(openCount==1) {	// 첫번째 오픈 카드인지?
				buttonIndexSave1 = index; //판정을위해 저장해둠
			}
			else if (openCount==2) {	// 두번쨰 카드
				buttonIndexSave2 = index;
				tryCount++;
				LabelMessage.setText("같은 카드를 찾아라!   " + "횟수 :"+tryCount);
				
				// 판정 로직
				boolean	isBingo = checkCard(buttonIndexSave1, buttonIndexSave2);
				if (isBingo==true) {
					playSound("bingo.wav");
					openCount=0;
					successCount++;
					if (successCount==8) {
						LabelMessage.setText("Game Clear!"+tryCount+" 번 시도");
					}
				}else {
					playSound("fail.wav");
					backToQuestion();	// 카드를 다시 뒤집어줘야함
				}
			}
		}
		public void backToQuestion() {
			// 바로 뒤집어 지지말게 시간을 주자
			timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Timer.");
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
			if (images[index1].equals(images[index2])) {	// 사진의 이름이 같은지 스트링 비교
				return true;
			}else {
				return false;
			}
		}
		
		public int getButtonIndex(JButton btn) {
			int index = 0;
			for (int i=0; i<16; i++) {
				if(buttons[i] == btn) { // 같은 인스턴스인지 판단
					index = i;
				}
			}
			return index;
		}
	}
	static void mixCard() {
		Random rand = new Random();
		for(int i=0; i<1000; i++) {
			int random = rand.nextInt(15) + 1; // 1~15 까지 섞임
			// swap
			String temp = images[0];
			images[0] = images[random];
			images[random] = temp;
		}
	}
	
	static void initUI(MyFrame myFrame) {
		panelNorth = new JPanel();
		panelNorth.setPreferredSize(new Dimension(400, 100));
		panelNorth.setBackground(Color.BLACK);
		LabelMessage = new JLabel("같은 카드를 찾아라!   " + "횟수 :"+tryCount);
		LabelMessage.setPreferredSize(new Dimension(400, 100));
		LabelMessage.setForeground(Color.WHITE);   // 글씨 색상
		LabelMessage.setFont(new Font("Monaco", Font.BOLD, 20));
		LabelMessage.setHorizontalAlignment(JLabel.CENTER);  // 글자 가운데로
		panelNorth.add(LabelMessage);
		myFrame.add("North", panelNorth);  //  JFrame에 넣는과정
		
		panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(4,4));
		panelCenter.setPreferredSize(new Dimension(400,400));
		for(int i=0; i<16; i++){
			buttons[i] = new JButton();
			buttons[i].setPreferredSize(new Dimension(100,100));
			buttons[i].setIcon(changeImage("question.png"));
			buttons[i].addActionListener(myFrame);
			panelCenter.add(buttons[i]);
		};
		myFrame.add("Center", panelCenter);
		
	}
	
	static ImageIcon changeImage(String filename) {
		ImageIcon icon = new ImageIcon("./img/"+filename);
		Image originImage = icon.getImage();
		Image changedImage = originImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
		ImageIcon icon_new = new ImageIcon(changedImage);
		return icon_new;
	}

	public static void main(String[] args) {
		new MyFrame("Bingo Game");

	}

}
