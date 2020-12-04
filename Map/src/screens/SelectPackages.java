package screens;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.sound.sampled.Line;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ComboBoxCellEditor;

import map.handlers.MetricsAnalizer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.Console;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SelectPackages extends JDialog {
	
	int numberOfCheckedBoxes = 0;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(IProject project) {
		try {
			SelectPackages dialog = new SelectPackages(project);
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
	public SelectPackages(IProject project) throws JavaModelException {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setLocationRelativeTo(null);
		
		MetricsAnalizer analyzer = new MetricsAnalizer();
		
		IPackageFragment[] pack;
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBounds(10, 67, 414, 150);
		contentPanel.add(panel);
		JLabel lblSelecioneUmPacote = new JLabel("Selecione um Pacote:");
		lblSelecioneUmPacote.setBounds(10, 11, 165, 14);
		contentPanel.add(lblSelecioneUmPacote);
		
		pack = analyzer.getPacotesPorProjeto(project);
		
		ArrayList<JCheckBox> listCheckBoxes = new ArrayList<JCheckBox>();
		ArrayList<IPackageFragment> listPackages = new ArrayList<IPackageFragment>();
		
		if (pack != null) {
			for (IPackageFragment iPackageFragment : pack) {
				if (iPackageFragment.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackageFragment.getElementName().trim().equals("")) {
					JCheckBox checkBox = new JCheckBox(iPackageFragment.getElementName());
					checkBox.setName(iPackageFragment.getElementName());
					panel.add(checkBox);
					listCheckBoxes.add(checkBox);
					listPackages.add(iPackageFragment);
				}
			}
		} else {
			System.out.println("ERRO");
		}
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("Avançar");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ArrayList<IPackageFragment> listCheckSelected = new ArrayList<>();
					for (int i = 0; i < listPackages.size(); i++) {
						if (listPackages.get(i) != null) {
							if (listCheckBoxes.get(i).isSelected()) {
								listCheckSelected.add(listPackages.get(i));
							}
						}
					}
					if (listCheckSelected.size() > 0) {
						SelectClass.main(listCheckSelected);
					}
					else {
						JOptionPane.showMessageDialog(null, "Selecione pelo menos um pacote!");
					}
				}
			});
			
			okButton.setActionCommand("Avançar");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Voltar");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SelectPackages.this.dispose();
				}
			});
			cancelButton.setActionCommand("Voltar");
			buttonPane.add(cancelButton);
		}
	}
}
