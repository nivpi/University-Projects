import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;


public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField guards;
	private JTextField endTime;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
			        frame.getContentPane().setBackground( Color.PINK );
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 525, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(6, 6, 6, 6));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel2 = new JLabel("~Welcome To Beer-Sheva's Election System~");
		lblNewLabel2.setBounds(15, 15, 480, 35);
		lblNewLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel2.setFont(new Font("Assistant", Font.BOLD, 16));
		contentPane.add(lblNewLabel2);

		JLabel lblNewLabel = new JLabel("Number of Guards: ");
		lblNewLabel.setBounds(10, 70, 175, 25);
		contentPane.add(lblNewLabel);

		JLabel labelPlwDC = new JLabel("Closing Kalpi at: ");
		labelPlwDC.setBounds(10, 105, 200, 25);
		contentPane.add(labelPlwDC);
		
				guards = new JTextField();
				guards.setText(null);
				guards.setBounds(305, 70, 115, 25);
				contentPane.add(guards);
				guards.setColumns(10);
				endTime = new JTextField();
				endTime.setText(null);
				endTime.setBounds(305, 110, 115, 25);
				contentPane.add(endTime);
				endTime.setColumns(10);
		JButton startButton = new JButton("Press to Start");
		startButton.setBounds(130, 250, 115, 30);
		contentPane.add(startButton);
		startButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					String votersData = "C:\\Users\\Eden Azran\\eclipse-workspace\\Threads_Elections\\src\\voters data.txt";
					String idlist = "C:\\Users\\Eden Azran\\eclipse-workspace\\Threads_Elections\\src\\id list.txt";
					Election election = new Election(votersData,idlist,20,2 );
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
			});

		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		exitButton.setBounds(260, 250, 115, 30);
		contentPane.add(exitButton);
	}
}