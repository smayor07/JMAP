package screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CBanner;

import map.handlers.MetricsAnalizer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.Console;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class Home extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Home dialog = new Home();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.setModal(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @throws JavaModelException 
	 */
	public Home() throws JavaModelException {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setLocationRelativeTo(null);
		
		MetricsAnalizer analyzer = new MetricsAnalizer();
		
		IProject[] root = analyzer.listarTodosProjetos();
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBounds(10, 67, 414, 150);
		contentPanel.add(panel);
		
		JComboBox<IProject> comboBox = new JComboBox<IProject>();
		comboBox.setBounds(10, 36, 414, 20);
		
		int i = 0;
		for (IProject iProject : root) {
			if (i == 0) {
				i = 1;
				continue;
			}
			comboBox.addItem(iProject);
			comboBox.setName(iProject.getName());
		}
		contentPanel.add(comboBox);
		
		JLabel lblSelecioneUmProjeto = new JLabel("Selecione um Projeto:");
		lblSelecioneUmProjeto.setBounds(10, 11, 165, 14);
		contentPanel.add(lblSelecioneUmProjeto);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Avançar");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						IProject projeto = (IProject) comboBox.getSelectedItem();
						SelectPackages.main(projeto);
					}
				});
				okButton.setActionCommand("Avançar");
				buttonPane.add(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Home.this.dispose();
						/*JMAP j = new JMAP();
						j.setVisible(true);*/
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
