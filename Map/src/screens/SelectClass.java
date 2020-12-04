package screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.HashMap;

import map.handlers.MetricsAnalizer;

@SuppressWarnings("serial")
public class SelectClass extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(ArrayList<IPackageFragment> pack) {
		try {
			SelectClass dialog = new SelectClass(pack);
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
	public SelectClass(ArrayList<IPackageFragment> pack) throws JavaModelException {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setLocationRelativeTo(null);
		
		MetricsAnalizer analyzer = new MetricsAnalizer();
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBounds(10, 67, 414, 150);
		contentPanel.add(panel);
		
		ICompilationUnit[] compUnit;
		ArrayList<IMethod> method = new ArrayList<IMethod>();
		HashSet<String> fieldsInClass = new HashSet<String>();
		HashSet<String> namesClass = new HashSet<String>();
		HashSet<String> typesParameters = new HashSet<String>();
		HashSet<String> returnOfMethods = new HashSet<String>();
		HashMap<String, Integer> acopHeranca = new HashMap<String, Integer>();
		HashMap<String, Integer> acopAbstracao = new HashMap<String, Integer>();
		HashMap<String, Integer> resultTable = new HashMap<String, Integer>();
		
		if (pack != null) {
			//Captura os nomes de todas as classes
			for (IPackageFragment iPackFrag : pack) {
				if (iPackFrag.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackFrag.getElementName().trim().equals("")) {
					compUnit = analyzer.getClassesPorPacote(iPackFrag);
					for (ICompilationUnit iComp : compUnit) {
						String nomeClassePrin = iComp.getElementName();
						nomeClassePrin = nomeClassePrin.toString().replaceAll(".java", "");
						namesClass.add(nomeClassePrin);
					}
				}
			}
			
			
			for (IPackageFragment iPackageFragment : pack) {
				if (iPackageFragment.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackageFragment.getElementName().trim().equals("")) {
					compUnit = analyzer.getClassesPorPacote(iPackageFragment);
					for (ICompilationUnit iCompilationUnit : compUnit) {
						//Obtem o nome da classe corrente
						String nomeClasseCorrente = iCompilationUnit.getElementName();
						nomeClasseCorrente = nomeClasseCorrente.toString().replaceAll(".java", "");
						acopHeranca.put(nomeClasseCorrente, 0);
						acopAbstracao.put(nomeClasseCorrente, 0);
						
						//Retornar o tipo dos atributos utilizado nas classe, salvo os RESERVADOS
						System.out.println("Atributos da classe: " + iCompilationUnit.getAllTypes());
						for (IType type : iCompilationUnit.getTypes()) {
							for (IField fields : type.getFields()) {
								String atributos = org.eclipse.jdt.core.Signature.getSignatureSimpleName(fields.getTypeSignature());
								fieldsInClass.add(atributos);
							}
						}
						
						/*System.out.println("Nome da classe: " + iCompilationUnit.getElementName());*/ 
						method = analyzer.getMetodosPorClasse(iCompilationUnit);
						/*System.out.println("Total de metodos:" + method.size());*/
						for (IMethod iMethod : method) {
							//Obtem os nomes dos metodos da classe
							System.out.println("	Nome do metodo:  " + iMethod.getElementName());
							//**********************
							//Obter os tipos dos atributos declarados nos metodos
							//**********************
							System.out.println("		Tipo retorno do método: " + iMethod);
							String typeMethod = iMethod.getReturnType();
							if (!nomeClasseCorrente.equals(typeMethod)) {
								returnOfMethods.add(typeMethod);
							}
							
							for (String type : iMethod.getParameterTypes()) {
								typesParameters.add(type);
							}
						}
						
						//Obtem os acoplamentos por herança da classe corrente
						for (String nc : namesClass) {
							for (String nf : fieldsInClass) {
								if (nc.equals(nf)) {
									acopHeranca.put(nomeClasseCorrente, acopHeranca.containsKey(nomeClasseCorrente) ? acopHeranca.get(nomeClasseCorrente) + 1 : 1);
								}
							}
						}
						
						//Obtem os acoplamentos por abstração da classe corrente
						for (String nc : namesClass) {
							for (String tp : typesParameters) {
								if (tp.contains(nc)) {
									acopAbstracao.put(nomeClasseCorrente, acopAbstracao.containsKey(nomeClasseCorrente) ? acopAbstracao.get(nomeClasseCorrente) + 1 : 1);
								}
							}
						}
						
						for (String nc : namesClass) {
							for (String rm : returnOfMethods) {
								if (rm.contains(nc)) {
									acopAbstracao.put(nomeClasseCorrente, acopAbstracao.containsKey(nomeClasseCorrente) ? acopAbstracao.get(nomeClasseCorrente) + 1 : 1);
								}
							}
						}
						fieldsInClass.clear();
						typesParameters.clear();
						returnOfMethods.clear();
					}
				}
			}
		} else {
			System.out.println("ERRO");
		}
		
		//Criando a tabela de resultados
		String[] columns = {"Classe","Acop.Heranca","Acop.Abstracao","Aceitacao"};
		DefaultTableModel model = new DefaultTableModel();
		int c = 0;
		model.setColumnIdentifiers(columns);
		
		Iterator<Entry<String, Integer>> iter1 = acopHeranca.entrySet().iterator();
		Iterator<Entry<String, Integer>> iter2 = acopAbstracao.entrySet().iterator();
		
		while (iter1.hasNext() || iter2.hasNext()) { 
			Entry<String, Integer> e1 = iter1.next();
			Entry<String, Integer> e2 = iter2.next();
			model.setRowCount(c);
			model.addRow(new Object[] {e1.getKey(),e1.getValue(),e2.getValue(),"OK"});
			int sumResult = e1.getValue() + e2.getValue();
			resultTable.put(e1.getKey(), sumResult);
			sumResult = 0;
			c++;
		}
		
		JPanel buttonTableGraphic = new JPanel();
		buttonTableGraphic.setLayout(new FlowLayout(FlowLayout.LEADING));
		getContentPane().add(buttonTableGraphic, BorderLayout.CENTER);
		{
			JButton showTableButton = new JButton("Mostrar Tabela");
			showTableButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JTable table = new JTable(model);
					JScrollPane spTable = new JScrollPane(table);
					JFrame frameTable = new JFrame();
					frameTable.setTitle("Resultado Acoplamentos");
					frameTable.setSize(600, 300);
					frameTable.add(spTable);
					frameTable.setVisible(true);
				}
			});
			showTableButton.setActionCommand("Mostrar Tabela");
			buttonTableGraphic.add(showTableButton);
			
			JButton showGraphicButton = new JButton("Mostrar Gráfico");
			showGraphicButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultCategoryDataset barra = new DefaultCategoryDataset();
					for (Map.Entry<String, Integer> element : resultTable.entrySet()) {
						barra.setValue(element.getValue(), element.getKey(), "");
					}
					JFreeChart grafico = ChartFactory.createBarChart("Resultados", "Classes", "Quantidade Acoplamentos", barra, PlotOrientation.VERTICAL, true, true, false);
					ChartPanel painelGrafico = new ChartPanel(grafico);
					JFrame frameGraphic = new JFrame();
					frameGraphic.setTitle("Gráfico Acoplamentos");
					frameGraphic.setSize(1050, 900);
					JPanel buttonFilter = new JPanel();
					buttonFilter.setLayout(new FlowLayout(FlowLayout.RIGHT));
					{
						frameGraphic.getContentPane().setLayout(new FlowLayout(FlowLayout.RIGHT));
						JComboBox<String> comboBox = new JComboBox<String>();
						comboBox.setBounds(10, 36, 414, 20);
						for (String classFilter : namesClass) {
							comboBox.addItem(classFilter);
							comboBox.setName(classFilter);
						}
						frameGraphic.getContentPane().add(comboBox);
						
						JButton filter = new JButton("Filtrar");
						filter.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								
							}
						});
						filter.setActionCommand("Filtrar");
						buttonFilter.add(filter);
					}
					frameGraphic.getContentPane().add(buttonFilter);
					frameGraphic.add(painelGrafico);
					frameGraphic.setVisible(true);
				}
			});
			showGraphicButton.setActionCommand("Mostrar Gráfico");
			buttonTableGraphic.add(showGraphicButton);
			
			JButton goBack = new JButton("Voltar");
			goBack.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SelectClass.this.dispose();
				}
			});
			goBack.setActionCommand("Voltar");
			buttonTableGraphic.add(goBack);
		}
	}
}