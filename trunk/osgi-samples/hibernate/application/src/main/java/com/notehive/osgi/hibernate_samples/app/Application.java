package com.notehive.osgi.hibernate_samples.app;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.SessionFactory;


public class Application extends JFrame {
	
	private SessionFactory sessionFactory;
	private JTextArea resultTextArea;
	private JTextArea hqlTextArea;
	private JButton executeQueryButton;
	
	// input box to type hslq
	// output box to show results
	// button to show  hibernateSessoin.getAllClassMetadata()
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Application() {
		super("Hibernate in OSGi - HQL command line interface");
		Box content = Box.createVerticalBox();
		this.setContentPane(content);
		
		Box box; 
		JLabel label;
		
		box = Box.createHorizontalBox();
		label = new JLabel("HQL");
		box.add(label);
		box.add(Box.createGlue());
		this.getContentPane().add(box);
		
		hqlTextArea = new JTextArea();
		this.getContentPane().add(hqlTextArea);
		this.getContentPane().add(Box.createVerticalStrut(6));

		box = Box.createHorizontalBox();
		executeQueryButton = new JButton("Execute Query");
		executeQueryButton.setMnemonic(KeyEvent.VK_E);
		executeQueryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executeQuery();
			}});
		box.add(executeQueryButton);
		box.add(Box.createHorizontalStrut(14));
		JButton showHibernateConfigButton = new JButton("Show Hibernate Config");
		showHibernateConfigButton.setMnemonic(KeyEvent.VK_S);
		box.add(showHibernateConfigButton);
		this.getContentPane().add(box);
		
		box = Box.createHorizontalBox();
		box.add(new JLabel(" Result:"));
		label.setDisplayedMnemonic(KeyEvent.VK_R);
		box.add(Box.createGlue());
		this.getContentPane().add(box);
		resultTextArea = new JTextArea();
		this.getContentPane().add(resultTextArea);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(d.width / 2 - 200, d.height / 2 - 250);
		this.setSize(500,400);
	}
	
	protected void executeQuery() {
		String text = hqlTextArea.getText();
		try {
			Session session = sessionFactory.openSession();
			Query query = session.createQuery(text);
			if (text.toLowerCase().contains("update")) {
				int count = query.executeUpdate();
				resultTextArea.setText("Rows updated: " + count);
			} else if (text.toLowerCase().contains("delete")) {
				int count = query.executeUpdate();
				resultTextArea.setText("Rows deleted: " + count);
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				List results = query.list();
				pw.println("Rows returned: " + results.size());
				for(Object o : results) {
					pw.println(o.toString());
				}
				resultTextArea.setText(sw.getBuffer().toString());
			}
		} catch (Exception e) {
			resultTextArea.setText(e.toString());
		}
	}

	public static void main(String[] args) {
		new Application().run();
	}
	
	public void run() {
		this.setVisible(true);
	}

	/** 
	 * Package level method accessed by test class.
	 */
	JTextArea getResultTextArea() {
		return resultTextArea;
	}

	/** 
	 * Package level method accessed by test class.
	 */
	JTextArea getHqlTextArea() {
		return hqlTextArea;
	}
	
	/** 
	 * Package level method accessed by test class.
	 */
	JButton getExecuteQueryButton() {
		return executeQueryButton;
	}

	public void stop() {
		this.setVisible(false);
	}
	
	

}
