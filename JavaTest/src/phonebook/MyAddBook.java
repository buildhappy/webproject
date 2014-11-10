package phonebook;

import java.awt.Choice;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class MyAddBook {
	public static JFrame frame;

	public MyAddBook() {
		frame = new JFrame("个人通讯录");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		JMenuBar menubar = new JMenuBar();
		JMenu edit = new JMenu("编辑");
		JMenuItem edit1 = new JMenuItem("录入");
		JMenuItem edit2 = new JMenuItem("查询");
		JMenuItem edit3 = new JMenuItem("删除");
		JMenuItem edit4 = new JMenuItem("修改");
		JMenuItem edit5 = new JMenuItem("排序");
		edit1.addActionListener(new Typein());
		JMenu show = new JMenu("显示信息");
		JMenuItem show1 = new JMenuItem("同学");
		JMenuItem show2 = new JMenuItem("同事");
		JMenuItem show3 = new JMenuItem("朋友");
		JMenuItem show4 = new JMenuItem("亲戚");
		JMenuItem show5 = new JMenuItem("全体人员");
		Container c = frame.getContentPane();
		JPanel pane = new JPanel();
		c.add(pane);
		pane.add(menubar);
		menubar.add(edit);
		edit.add(edit1);
		edit.add(edit2);
		edit.add(edit3);
		edit.add(edit4);
		edit.add(edit5);
		menubar.add(show);
		show.add(show1);
		show.add(show2);
		show.add(show3);
		show.add(show4);
		show.add(show5);
		frame.setSize(300, 100);
		// frame.pack();
		// 查询修改删除
		class Search {
			JDialog dialog = new JDialog(frame, "查询对话框", true);

			public Search(String str, int n) {
				dialog.setSize(250, 200);
				Container c = dialog.getContentPane();
				dialog.setLayout(new GridLayout(2, 1, 5, 5));
				JLabel Lsearch = new JLabel("请输入要" + str + "人员的名字：");
				final JTextField Tname = new JTextField(10);
				JButton certain = new JButton("确定");
				JButton cancel = new JButton("取消");
				// final String in=Tname.getText();
				JPanel pane1 = new JPanel();
				JPanel pane2 = new JPanel();
				c.add(pane1);
				c.add(pane2);
				pane1.add(Lsearch);
				pane1.add(Tname);
				pane2.add(certain);
				pane2.add(cancel);
				dialog.setDefaultCloseOperation(dialog.DISPOSE_ON_CLOSE);
				// dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				if (n == 2) {
					certain.addActionListener(new ActionListener() // 查询
					{
						public void actionPerformed(ActionEvent e) {
							try {
								FileReader file = new FileReader(
										"D:\\AddressBook.txt");
								Scanner find = new Scanner(file);
								while (find.hasNext()) {
									if (find.next().equals(Tname.getText())) {
										dialog.dispose();
										Typein fd = new Typein();
										fd.frame = new JFrame("查询结果如下");
										fd.Tname.setText(Tname.getText());
										fd.Cgroup.select(find.next());
										fd.Cbirthyear.select(find.next());
										fd.Cbirthmonth.select(find.next());
										fd.Cbirthday.select(find.next());
										fd.Tphone.setText(find.next());
										fd.Temail.setText(find.next());
										fd.Tother.setText(find.next());
										fd.typein();

									}
								}
								file.close();
								find.close();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								// e1.printStackTrace();
								System.out.print("未找到文件");
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								// e2.printStackTrace();
								System.out.print("未找到该人员");
							}
						}
					});
				}
				if (n == 3) {
					certain.addActionListener(new ActionListener() // 删除
					{
						public void actionPerformed(ActionEvent e) {
							try {
								File file = new File("D:\\AddressBook.txt");
								Scanner find = new Scanner(file);
								FileWriter file1 = new FileWriter(
										"D:\\AddressBook1.txt", true);
								PrintWriter write = new PrintWriter(file1);
								while (find.hasNext()) {

									String s = find.next();
									if (!(s.equals(Tname.getText()))) {
										write.print(s + '\t');
										write.print(find.next() + '\t');
										write.print(find.next() + ' ');
										write.print(find.next() + ' ');
										write.print(find.next() + '\t');
										write.print(find.next() + '\t');
										write.print(find.next() + '\t');
										write.println(find.next());
									} else {
										find.next();
										find.next();
										find.next();
										find.next();
										find.next();
										find.next();
										find.next();
									}
								}

								// file.close();
								find.close();
								file.delete();
								write.close();
								file1.close();
								File file2 = new File("D:\\AddressBook1.txt");
								file2.renameTo(new File("D:\\AddressBook.txt"));
								JOptionPane
										.showMessageDialog(null, "删除成功",
												"删除结果",
												JOptionPane.INFORMATION_MESSAGE);
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								// e1.printStackTrace();
								JOptionPane.showMessageDialog(null, "未找到文件");
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								// e2.printStackTrace();
								System.out.print("未找到该人员");
							}
						}
					});
				}
				if (n == 4) {
					certain.addActionListener(new ActionListener() // 修改
					{
						public void actionPerformed(ActionEvent e) {
							try {
								File file = new File("D:\\AddressBook.txt");
								Scanner find = new Scanner(file);
								FileWriter file1 = new FileWriter(
										"D:\\AddressBook1.txt", true);
								PrintWriter write = new PrintWriter(file1);
								while (find.hasNext()) {
									String s = find.next();
									if (!(s.equals(Tname.getText()))) {
										write.print(s + '\t');
										write.print(find.next() + '\t');
										write.print(find.next() + ' ');
										write.print(find.next() + ' ');
										write.print(find.next() + '\t');
										write.print(find.next() + '\t');
										write.print(find.next() + '\t');
										write.println(find.next());
									} else {
										dialog.dispose();
										Typein fd = new Typein();
										Typein.y = 1;
										fd.frame = new JFrame("查询结果如下");
										fd.Tname.setText(Tname.getText());
										String s1 = find.next();
										fd.Cgroup.select(s1);
										String s2 = find.next();
										fd.Cbirthyear.select(s2);
										String s3 = find.next();
										fd.Cbirthmonth.select(s3);
										String s4 = find.next();
										fd.Cbirthday.select(s4);
										String s5 = find.next();
										fd.Tphone.setText(s5);
										String s6 = find.next();
										fd.Temail.setText(s6);
										String s7 = find.next();
										fd.Tother.setText(s7);
										fd.typein();
										if (Typein.z == 1) {
											write.print(Tname.getText() + '\t');
											write.print(s1 + '\t');
											write.print(s2 + ' ');
											write.print(s3 + ' ');
											write.print(s4 + '\t');
											write.print(s5 + '\t');
											write.print(s6 + '\t');
											write.println(s7);
											Typein.z = 2;

										}
									}
								}
								// file.close();
								find.close();
								file.delete();
								write.close();
								file1.close();
								File file2 = new File("D:\\AddressBook1.txt");
								file2.renameTo(new File("D:\\AddressBook.txt"));

							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								// e1.printStackTrace();
								System.out.print("未找到文件");
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								// e2.printStackTrace();
								System.out.print("未找到该人员");
							}
						}
					});
				}
				cancel.addActionListener(new ActionListener() // 取消
				{
					public void actionPerformed(ActionEvent e) {
						System.out.println("fdas");
						// dialog.dispose();
					}
				});
			}
		}
		edit2.addActionListener(new ActionListener() // 监听查询
		{
			public void actionPerformed(ActionEvent e) {
				new Search("查询", 2).dialog.setVisible(true);
			}
		});
		edit3.addActionListener(new ActionListener() // 监听删除
		{
			public void actionPerformed(ActionEvent e) {
				new Search("删除", 3).dialog.setVisible(true);
			}
		});
		edit4.addActionListener(new ActionListener() // 监听修改
		{
			public void actionPerformed(ActionEvent e) {
				new Search("修改", 4).dialog.setVisible(true);
			}
		});
		// 输出类
		class Print {
			public JTextArea area;

			public Print(String st, int n) {
				JFrame frame = new JFrame(st + "信息如下");
				frame.setSize(800, 400);
				// frame.setLocation(350, 150);
				area = new JTextArea();
				frame.add(area);
				frame.setVisible(true);
				if (n == 2)
					try // 排序
					{
						int i, j, k;
						String[] all;
						all = new String[1000];
						BufferedReader read = new BufferedReader(
								new FileReader("D:\\AddressBook.txt"));
						area.append("姓名" + '\t' + "组别" + '\t' + "生日" + '\t'
								+ "电话" + '\t' + "Email" + '\t' + "其他" + '\n');
						int z = 1, count = 0;
						while (z == 1) {
							for (i = 0; i < 1000; i++) {
								String str = read.readLine();
								if (str != null) {
									all[i] = str;
									count++;
								} else
									z = 0;
							}
						}
						String[] bll;
						bll = new String[count];
						for (i = 0; i < count; i++)
							bll[i] = all[i];
						getSortOfChinese(bll);
						for (i = 0; i < count; i++)
							area.append(bll[i] + '\n');
						read.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				if (n == 1)
					try // 各类人员
					{
						FileReader file = new FileReader("D:\\AddressBook.txt");
						Scanner find = new Scanner(file);
						area.append("姓名" + '\t' + "组别" + '\t' + "生日" + '\t'
								+ "电话" + '\t' + "Email" + '\t' + "其他" + '\n');
						while (find.hasNext()) {
							String str1 = find.next();
							String str2 = find.next();
							if (str2.equals(st)) {
								area.append(str1 + '\t');
								area.append(str2 + '\t');
								area.append(find.next() + ' ');
								area.append(find.next() + ' ');
								area.append(find.next() + '\t');
								area.append(find.next() + '\t');
								area.append(find.next() + '\t');
								area.append(find.next() + '\n');
							} else {
								find.next();
								find.next();
								find.next();
								find.next();
								find.next();
								find.next();
							}

						}
						file.close();
						find.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}

				if (n == 0)
					try // 全体人员信息
					{
						BufferedReader read = new BufferedReader(
								new FileReader("D:\\AddressBook.txt"));
						area.append("姓名" + '\t' + "组别" + '\t' + "生日" + '\t'
								+ "电话" + '\t' + "Email" + '\t' + "其他" + '\n');
						int z = 1;
						while (z == 1) {
							String str = read.readLine();
							if (str != null)
								area.append(str + '\n');
							else
								z = 0;
						}
						read.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
			}
		}
		edit5.addActionListener(new ActionListener() // 监听排序
		{
			public void actionPerformed(ActionEvent e) {
				new Print("按姓名排序后", 2);
			}
		});
		show1.addActionListener(new ActionListener() // 监听同学
		{
			public void actionPerformed(ActionEvent e) {
				new Print("同学", 1);
			}
		});
		show2.addActionListener(new ActionListener() // 监听同事
		{
			public void actionPerformed(ActionEvent e) {
				new Print("同事", 1);
			}
		});
		show3.addActionListener(new ActionListener() // 监听朋友
		{
			public void actionPerformed(ActionEvent e) {
				new Print("朋友", 1);
			}
		});
		show4.addActionListener(new ActionListener() // 监听亲戚
		{
			public void actionPerformed(ActionEvent e) {
				new Print("亲戚", 1);
			}
		});
		show5.addActionListener(new ActionListener() // 监听全体人员
		{
			public void actionPerformed(ActionEvent e) {
				new Print("全体人员", 0);
			}
		});
	}

	public static void main(String[] args) {
		new MyAddBook();
	}

	public static String[] getSortOfChinese(String[] a) {
		// Collator 类是用来执行区分语言环境这里使用CHINA
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);

		// JDKz自带对数组进行排序。
		Arrays.sort(a, cmp);
		return a;
	}
}

// 输入类
class Typein implements ActionListener {
	public static int z = 2;
	public static int y = 0;
	public JLabel Lname, Lgroup, Lbirthday, Lphone, Lemail, Lother, Lnote;
	public JTextField Tname = new JTextField(10), Tphone = new JTextField(15),
			Temail = new JTextField(15), Tother = new JTextField(15);
	public Choice Cgroup = new Choice(), Cbirthyear = new Choice(),
			Cbirthmonth = new Choice(), Cbirthday = new Choice();
	public JButton certain, cancel;
	public JFrame frame = new JFrame("录入联系人信息");

	public Typein() {
		Cgroup.addItem("无");
		Cgroup.addItem("同学");
		Cgroup.addItem("同事");
		Cgroup.addItem("朋友");
		Cgroup.addItem("亲戚");
		Cbirthyear.addItem("1985");
		Cbirthyear.addItem("1986");
		Cbirthyear.addItem("1987");
		Cbirthyear.addItem("1988");
		Cbirthyear.addItem("1989");
		Cbirthyear.addItem("1990");
		Cbirthyear.addItem("1991");
		Cbirthyear.addItem("1992");
		Cbirthyear.addItem("1993");
		Cbirthyear.addItem("1994");
		Cbirthyear.addItem("1995");
		Cbirthyear.addItem("1996");
		Cbirthyear.addItem("1997");
		Cbirthyear.addItem("1998");
		Cbirthyear.addItem("1999");
		Cbirthyear.addItem("2000");
		Cbirthmonth.addItem("01");
		Cbirthmonth.addItem("02");
		Cbirthmonth.addItem("03");
		Cbirthmonth.addItem("04");
		Cbirthmonth.addItem("05");
		Cbirthmonth.addItem("06");
		Cbirthmonth.addItem("07");
		Cbirthmonth.addItem("08");
		Cbirthmonth.addItem("09");
		Cbirthmonth.addItem("10");
		Cbirthmonth.addItem("11");
		Cbirthmonth.addItem("12");
		Cbirthday.addItem("01");
		Cbirthday.addItem("02");
		Cbirthday.addItem("03");
		Cbirthday.addItem("04");
		Cbirthday.addItem("05");
		Cbirthday.addItem("06");
		Cbirthday.addItem("07");
		Cbirthday.addItem("08");
		Cbirthday.addItem("09");
		Cbirthday.addItem("10");
		Cbirthday.addItem("11");
		Cbirthday.addItem("12");
		Cbirthday.addItem("13");
		Cbirthday.addItem("14");
		Cbirthday.addItem("15");
		Cbirthday.addItem("16");
		Cbirthday.addItem("17");
		Cbirthday.addItem("18");
		Cbirthday.addItem("19");
		Cbirthday.addItem("20");
		Cbirthday.addItem("21");
		Cbirthday.addItem("22");
		Cbirthday.addItem("23");
		Cbirthday.addItem("24");
		Cbirthday.addItem("25");
		Cbirthday.addItem("26");
		Cbirthday.addItem("27");
		Cbirthday.addItem("28");
		Cbirthday.addItem("29");
		Cbirthday.addItem("30");
		Cbirthday.addItem("31");
	}

	public void typein() {
		Container c = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 300);
		frame.setLayout(new GridLayout(5, 1, 5, 5));
		frame.setVisible(true);
		Lname = new JLabel("姓名：");
		Lgroup = new JLabel("组别：");
		Lbirthday = new JLabel("生日：");
		Lphone = new JLabel("电话：");
		Lemail = new JLabel("Email：");
		Lother = new JLabel("其他：");
		Lnote = new JLabel("注释：”其他“中输入所在学校 /共事单位/认识地方/亲戚称呼");
		certain = new JButton("确定");
		cancel = new JButton("取消");
		JPanel pane1 = new JPanel();
		JPanel pane2 = new JPanel();
		JPanel pane3 = new JPanel();
		JPanel pane4 = new JPanel();
		JPanel pane5 = new JPanel();
		c.add(pane1);
		c.add(pane2);
		c.add(pane3);
		c.add(pane4);
		c.add(pane5);
		pane1.add(Lname);
		pane1.add(Tname);
		pane1.add(Lgroup);
		pane1.add(Cgroup);
		pane2.add(Lbirthday);
		pane2.add(Cbirthyear);
		pane2.add(Cbirthmonth);
		pane2.add(Cbirthday);
		pane2.add(Lphone);
		pane2.add(Tphone);
		pane3.add(Lemail);
		pane3.add(Temail);
		pane3.add(Lother);
		pane3.add(Tother);
		pane4.add(Lnote);
		pane5.add(certain);
		pane5.add(cancel);
		certain.addActionListener(new ActionListener() // 设置监听器
		{
			public void actionPerformed(ActionEvent e) // 用匿名内部类实现监听器
			{
				if (Tname.getText().equals(""))
					JOptionPane.showMessageDialog(null, "录入失败，姓名必须填写！", "录入结果",
							JOptionPane.INFORMATION_MESSAGE);
				else {
					try {
						FileWriter AddressBook = new FileWriter(
								"D:\\AddressBook.txt", true);
						PrintWriter add = new PrintWriter(AddressBook);
						String s1, s2, s3, s4;
						if (Tname.getText().equals(""))
							s1 = "无";
						else
							s1 = Tname.getText();
						if (Tphone.getText().equals(""))
							s2 = "无";
						else
							s2 = Tphone.getText();
						if (Temail.getText().equals(""))
							s3 = "无";
						else
							s3 = Temail.getText();
						if (Tother.getText().equals(""))
							s4 = "无";
						else
							s4 = Tother.getText();
						/*
						 * add.println(Tname.getText()+'\t'+Cgroup.getSelectedItem
						 * ()+'\t'+Cbirthyear.getSelectedItem()+
						 * ' '+Cbirthmonth.
						 * getSelectedItem()+' '+Cbirthday.getSelectedItem()+
						 * '\t'
						 * +Tphone.getText()+'\t'+Temail.getText()+'\t'+Tother
						 * .getText());
						 */
						add.println(s1 + '\t' + Cgroup.getSelectedItem() + '\t'
								+ Cbirthyear.getSelectedItem() + ' '
								+ Cbirthmonth.getSelectedItem() + ' '
								+ Cbirthday.getSelectedItem() + '\t' + s2
								+ '\t' + s3 + '\t' + s4);
						add.close();
						AddressBook.close();
						z = 1;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (y == 0) {
						JOptionPane.showMessageDialog(null, "录入成功", "录入结果",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "修改成功", "修改结果",
								JOptionPane.INFORMATION_MESSAGE);
					}
					Tname.setText("");
					Tphone.setText("");
					Temail.setText("");
					Tother.setText("");
					// Cgroup.setName("无");
					// Cbirthyear.setName("1985");
					// Cbirthmonth.setName("1");
					// Cbirthday.setName("1");
				}
			}
		});
		cancel.addActionListener(new ActionListener() // 设置监听器
		{
			public void actionPerformed(ActionEvent e) // 用匿名内部类实现监听器
			{
				frame.dispose();
				z = 0;
			}
		});

	}

	public void actionPerformed(ActionEvent e) {
		new Typein().typein();
	}
}