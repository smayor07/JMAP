package screens;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import map.handlers.MetricsAnalizer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.SwingConstants;

public class JMAP extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JMAP frame = new JMAP();
					frame.setTitle("JMAP - Java Analyzer Metrics Plugin");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	MetricsAnalizer analyzer = new MetricsAnalizer();
	IProject[] root = analyzer.listarTodosProjetos();
	IPackageFragment[] pack = null;
	ArrayList<JCheckBox> listCheckBoxes = new ArrayList<JCheckBox>();
	ArrayList<IPackageFragment> listPackages = new ArrayList<IPackageFragment>();
	
	ICompilationUnit[] compUnit;
	ArrayList<IMethod> method = new ArrayList<IMethod>();
	HashSet<String> fieldsInClass = new HashSet<String>();
	HashSet<String> namesClass = new HashSet<String>();
	HashSet<String> typesParameters = new HashSet<String>();
	HashSet<String> returnOfMethods = new HashSet<String>();
	HashSet<String> allClassProjects = new HashSet<String>();
	HashMap<String, Integer> acopHeranca = new HashMap<String, Integer>();
	HashMap<String, Integer> acopAbstracao = new HashMap<String, Integer>();
	HashMap<String, Integer> resultTable = new HashMap<String, Integer>();
	
	JButton btnCalcular = new JButton("Calcular");
	JButton btnMostrarTabela = new JButton("Exibir Tabela");
	JButton btnMostrarGrafico = new JButton("Exibir Grafico");
	ChartPanel painelGraficoFilter = null;
	DefaultCategoryDataset barra = new DefaultCategoryDataset();
	JFreeChart graficoFilter = null;
	int somaTotalHeranca = 0;
	int somaTotalAbstracao = 0;
	long tempo = 0;
	int herancaBOM = 0;
	int herancaREGULAR = 0;
	int herancaRUIM = 0;
	int abstracaoBOM = 0;
	int abstracaoREGULAR = 0;
	int abstracaoRUIM = 0;
	
	
	public JMAP() throws JavaModelException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1187, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnCalcular.setEnabled(false);
		btnMostrarTabela.setEnabled(false);
		btnMostrarGrafico.setEnabled(false);
		
		JComboBox<IProject> comboBox = new JComboBox<IProject>();
		comboBox.setBounds(30, 88, 263, 52);
		int i = 0;
		for (IProject iProject : root) {
			if (i == 0) {
				i = 1;
				continue;
			}
			comboBox.addItem(iProject);
			comboBox.setName(iProject.getName());
		}
		contentPane.add(comboBox);
		
		JLabel lblSelecioneProjeto = new JLabel("Selecione o projeto");
		lblSelecioneProjeto.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		lblSelecioneProjeto.setBounds(30, 26, 263, 49);
		contentPane.add(lblSelecioneProjeto);
		
		JLabel lblSelecionePacote = new JLabel("Selecione o pacote");
		lblSelecionePacote.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		lblSelecionePacote.setBounds(30, 153, 263, 59);
		contentPane.add(lblSelecionePacote);
		
		/*btnCalcular = new JButton("Calcular");*/
		btnCalcular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tempo = System.currentTimeMillis();
				ArrayList<IPackageFragment> listCheckSelected = new ArrayList<>();
				for (int i = 0; i < listPackages.size(); i++) {
					if (listPackages.get(i) != null) {
						if (listCheckBoxes.get(i).isSelected()) {
							listCheckSelected.add(listPackages.get(i));
						}
					}
				}
				
				//Captura os nomes de todas as classes do projeto
				for (IPackageFragment iPackFrag : pack) {
					try {
						if (iPackFrag.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackFrag.getElementName().trim().equals("")) {
							compUnit = analyzer.getClassesPorPacote(iPackFrag);
							for (ICompilationUnit iComp : compUnit) {
								String nomeClassePrin = iComp.getElementName();
								nomeClassePrin = nomeClassePrin.toString().replaceAll(".java", "");
								allClassProjects.add(nomeClassePrin);
							}
						}
					} catch (JavaModelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				if (listCheckSelected.size() > 0) {
					//Captura os nomes de todas as classes selecionadas
					for (IPackageFragment iPackFrag : listCheckSelected) {
						try {
							if (iPackFrag.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackFrag.getElementName().trim().equals("")) {
								compUnit = analyzer.getClassesPorPacote(iPackFrag);
								for (ICompilationUnit iComp : compUnit) {
									String nomeClassePrin = iComp.getElementName();
									nomeClassePrin = nomeClassePrin.toString().replaceAll(".java", "");
									namesClass.add(nomeClassePrin);
									acopHeranca.put(nomeClassePrin, 0);
									acopAbstracao.put(nomeClassePrin, 0);
								}
							}
						} catch (JavaModelException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					for (IPackageFragment iPackageFragment : listCheckSelected) {
						try {
							if (iPackageFragment.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackageFragment.getElementName().trim().equals("")) {
								compUnit = analyzer.getClassesPorPacote(iPackageFragment);
								for (ICompilationUnit iCompilationUnit : compUnit) {
									//Obtem o nome da classe corrente
									String nomeClasseCorrente = iCompilationUnit.getElementName();
									nomeClasseCorrente = nomeClasseCorrente.toString().replaceAll(".java", "");
									
									//Obtem os acoplamentos por herança da classe corrente
									IType[] typesComp = iCompilationUnit.getTypes();
									for (IType typeSuper : typesComp) {
										ITypeHierarchy typeHierarchy = typeSuper.newTypeHierarchy(null);
										IType typeClassPrin = typeHierarchy.getType();
										IType typeSuperClass = typeHierarchy.getSuperclass(typeClassPrin);
										if (typeSuperClass != null) {
											String superClassName = typeSuperClass.getElementName();
											if (superClassName != null) {
												for (Map.Entry<String, Integer> element : acopHeranca.entrySet()) {
													String aux = element.getKey();
													if (superClassName.contains(aux)) {
														int valAux = element.getValue();
														valAux++;
														somaTotalHeranca++;
														element.setValue(valAux);
													}
												}
											}
										}
									}
									
									//Retornar o tipo dos atributos utilizado nas classe, salvo os RESERVADOS
									System.out.println("Atributos da classe: " + iCompilationUnit.getAllTypes());
									for (IType type : iCompilationUnit.getAllTypes()) {
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
									
									
									//Obtem os acoplamentos por abstração da classe corrente
									
									for (String nc : allClassProjects) {
										for (String nf : fieldsInClass) {
											if (nf.contains(nc)) {
												for (Map.Entry<String, Integer> element : acopAbstracao.entrySet()) {
													String aux = element.getKey();
													if (nf.contains(aux)) {
														int valAux = element.getValue();
														valAux++;
														somaTotalAbstracao++;
														element.setValue(valAux);
													}
												}
												/*acopAbstracao.put(nomeClasseCorrente, acopAbstracao.containsKey(nomeClasseCorrente) ? acopAbstracao.get(nomeClasseCorrente) + 1 : 1);*/
											}
										}
									}
									
									for (String nc : allClassProjects) {
										for (String tp : typesParameters) {
											if (tp.contains(nc)) {
												for (Map.Entry<String, Integer> element : acopAbstracao.entrySet()) {
													String aux = element.getKey();
													if (tp.contains(aux)) {
														int valAux = element.getValue();
														valAux++;
														somaTotalAbstracao++;
														element.setValue(valAux);
													}
												}
												/*acopAbstracao.put(nomeClasseCorrente, acopAbstracao.containsKey(nomeClasseCorrente) ? acopAbstracao.get(nomeClasseCorrente) + 1 : 1);*/
											}
										}
									}
									
									for (String nc : allClassProjects) {
										for (String rm : returnOfMethods) {
											if (rm.contains(nc)) {
												for (Map.Entry<String, Integer> element : acopAbstracao.entrySet()) {
													String aux = element.getKey();
													if (rm.contains(aux)) {
														int valAux = element.getValue();
														valAux++;
														somaTotalAbstracao++;
														element.setValue(valAux);
													}
												}
												/*acopAbstracao.put(nomeClasseCorrente, acopAbstracao.containsKey(nomeClasseCorrente) ? acopAbstracao.get(nomeClasseCorrente) + 1 : 1);*/
											}
										}
									}
									fieldsInClass.clear();
									typesParameters.clear();
									returnOfMethods.clear();
								}
							}
						} catch (JavaModelException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					btnMostrarTabela.setEnabled(true);
					btnMostrarGrafico.setEnabled(true);
					System.out.println("O método foi executado em " + ((System.currentTimeMillis() - tempo)/ 1000) + "." + ((System.currentTimeMillis() - tempo) % 1000));
					System.out.println("Quantidade de classes selecionadas: " + namesClass.size());
					System.out.println("Total Herança:" + somaTotalHeranca);
					System.out.println("Total Abstração:" + somaTotalAbstracao);
				}
				else {
					JOptionPane.showMessageDialog(null, "Selecione pelo menos um pacote!");
				}
			}
		});
		btnCalcular.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		btnCalcular.setBounds(534, 399, 623, 66);
		contentPane.add(btnCalcular);
		
		/*JButton btnMostrarTabela = new JButton("Exibir Tabela");*/
		btnMostrarTabela.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Criando a tabela de resultados
				String[] columns = {"Classe","Acop.Heranca","Aceitacao","Acop.Abstracao","Aceitacao"};
				ImageIcon BOM = new ImageIcon("C:\\Users\\smayo\\eclipse-workspace\\Map\\icons\\LOW.png");
				ImageIcon REGULAR = new ImageIcon("C:\\Users\\smayo\\eclipse-workspace\\Map\\icons\\MEDIUM.png");
				ImageIcon RUIM = new ImageIcon("C:\\Users\\smayo\\eclipse-workspace\\Map\\icons\\HIGH.png");
				String aceitacaoHeranca = null;
				String aceitacaoAbstracao = null;
				DefaultTableModel model = new DefaultTableModel();
				int c = 0;
				model.setColumnIdentifiers(columns);
				
				Iterator<Entry<String, Integer>> iter1 = acopHeranca.entrySet().iterator();
				Iterator<Entry<String, Integer>> iter2 = acopAbstracao.entrySet().iterator();
				
				while (iter1.hasNext() || iter2.hasNext()) { 
					Entry<String, Integer> e1 = iter1.next();
					Entry<String, Integer> e2 = iter2.next();
					model.setRowCount(c);
					//Seta a aceitação do Acoplamento de Herança
					if (e1.getValue() <= 1) {
						aceitacaoHeranca = "BOM";
						herancaBOM++;
					} else if (e1.getValue() <= 3) {
						aceitacaoHeranca = "REGULAR";
						herancaREGULAR++;
					} else if (e1.getValue() > 3) {
						aceitacaoHeranca = "RUIM";
						herancaRUIM++;
					}
					
					//Seta a aceitação do Acomplamento de Abstração
					if (e2.getValue() <= 6) {
						aceitacaoAbstracao = "BOM";
						abstracaoBOM++;
					} else if (e2.getValue() <= 16) {
						aceitacaoAbstracao = "REGULAR";
						abstracaoREGULAR++;
					} else if (e2.getValue() > 16) {
						aceitacaoAbstracao = "RUIM";
						abstracaoRUIM++;
					}
					
					model.addRow(new Object[] {e1.getKey(),e1.getValue(),aceitacaoHeranca,e2.getValue(),aceitacaoAbstracao});
					int sumResult = e1.getValue() + e2.getValue();
					resultTable.put(e1.getKey(), sumResult);
					sumResult = 0;
					c++;
				}
				System.out.println("Heranca BOM: " + herancaBOM);
				System.out.println("Heranca REGULAR: " + herancaREGULAR);
				System.out.println("Heranca RUIM: " + herancaRUIM);
				System.out.println("Abstracao BOM: " + abstracaoBOM);
				System.out.println("Abstracao REGULAR: " + abstracaoREGULAR);
				System.out.println("Abstracao RUIM: " + abstracaoRUIM);
				JTable table = new JTable(model);
				JScrollPane spTable = new JScrollPane(table);
				JFrame frameTable = new JFrame();
				frameTable.setTitle("Resultado Acoplamentos");
				frameTable.setSize(900, 600);
				frameTable.getContentPane().add(spTable);
				frameTable.setVisible(true);
			}
		});
		btnMostrarTabela.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		btnMostrarTabela.setBounds(534, 490, 623, 69);
		contentPane.add(btnMostrarTabela);
		
		/*JButton btnMostrarGrafico = new JButton("Exibir Grafico");*/
		btnMostrarGrafico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultCategoryDataset barra = new DefaultCategoryDataset();
				/*for (Map.Entry<String, Integer> element : resultTable.entrySet()) {
					barra.setValue(element.getValue(), element.getKey(), "");
				}*/
				JFreeChart grafico = ChartFactory.createBarChart("Resultados", "Classes", "Quantidade Acoplamentos", barra, PlotOrientation.VERTICAL, true, true, false);
				ChartPanel painelGrafico = new ChartPanel(grafico);
				JFrame frameGraphic = new JFrame();
				frameGraphic.setTitle("Gráfico Acoplamentos");
				frameGraphic.setSize(1050, 900);
				JPanel buttonFilter = new JPanel();
				buttonFilter.setLayout(new FlowLayout(FlowLayout.RIGHT));
				{
					frameGraphic.getContentPane().setLayout(new FlowLayout(FlowLayout.RIGHT));
					JRadioButton rbAbstracao = new JRadioButton("Acop.Abstração", true);
					JRadioButton rbHeranca = new JRadioButton("Acop.Herança", false);
					JComboBox<String> comboBox = new JComboBox<String>();
					comboBox.setBounds(10, 36, 414, 20);
					for (String classFilter : namesClass) {
						comboBox.addItem(classFilter);
						comboBox.setName(classFilter);
					}
					frameGraphic.getContentPane().add(comboBox);
					ButtonGroup bg = new ButtonGroup();
					bg.add(rbHeranca);
					bg.add(rbAbstracao);
					frameGraphic.getContentPane().add(rbAbstracao);
					frameGraphic.getContentPane().add(rbHeranca);
					
					JButton filter = new JButton("Filtrar");
					filter.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String classSelected = (String)comboBox.getSelectedItem();
							graficoFilter = null;
							/*barra.clear();*/
							
							if (rbAbstracao.isSelected()) {
								//Filtra acoplamento de Abstração
								for (Map.Entry<String, Integer> elementAbs : acopAbstracao.entrySet()) {
									if (elementAbs.getKey().equals(classSelected)) {
										barra.setValue(elementAbs.getValue(), elementAbs.getKey(), "");
										graficoFilter = ChartFactory.createBarChart("Resultados", "Classes", "Quantidade Acoplamentos", barra, PlotOrientation.VERTICAL, true, true, false);
										if (painelGraficoFilter != null) {
											frameGraphic.getContentPane().remove(painelGraficoFilter);
											frameGraphic.repaint();
											frameGraphic.revalidate();
										}
										painelGraficoFilter = new ChartPanel(graficoFilter);
										frameGraphic.getContentPane().add(painelGraficoFilter);
										frameGraphic.repaint();
										frameGraphic.revalidate();
										break;
									}
								}
							} else if (rbHeranca.isSelected()) {
								//Filtra acoplamento de Herança
								for (Map.Entry<String, Integer> elementHer : acopHeranca.entrySet()) {
									if (elementHer.getKey().equals(classSelected)) {
										barra.setValue(elementHer.getValue(), elementHer.getKey(), "");
										graficoFilter = ChartFactory.createBarChart("Resultados", "Classes", "Quantidade Acoplamentos", barra, PlotOrientation.VERTICAL, true, true, false);
										if (painelGraficoFilter != null) {
											frameGraphic.getContentPane().remove(painelGraficoFilter);
											frameGraphic.repaint();
											frameGraphic.revalidate();
										}
										painelGraficoFilter = new ChartPanel(graficoFilter);
										frameGraphic.getContentPane().add(painelGraficoFilter);
										frameGraphic.repaint();
										frameGraphic.revalidate();
										break;
									}
								}
							}
						}
					});
					filter.setActionCommand("Filtrar");
					JButton clearFilter = new JButton("Limpar graficos");
					clearFilter.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							barra.clear();
						}
					});
					clearFilter.setActionCommand("Limpar graficos");
					buttonFilter.add(filter);
					buttonFilter.add(clearFilter);
				}
				frameGraphic.getContentPane().add(buttonFilter);
				/*frameGraphic.getContentPane().add(painelGrafico);*/
				frameGraphic.setVisible(true);
			}
		});
		btnMostrarGrafico.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		btnMostrarGrafico.setBounds(534, 584, 623, 66);
		contentPane.add(btnMostrarGrafico);
		
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JMAP.this.dispose();
			}
		});
		btnSair.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		btnSair.setBounds(534, 674, 623, 66);
		contentPane.add(btnSair);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\smayo\\eclipse-workspace\\Map\\icons\\JMAP - Reduce.jpg"));
		lblNewLabel.setBounds(534, 26, 623, 360);
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 210, 492, 530);
		contentPane.add(scrollPane);
		
		JPanel panelPacotes = new JPanel();
		panelPacotes.setLayout(new BoxLayout(panelPacotes, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(panelPacotes);
		
		JButton btnFiltrarPacotes = new JButton("Filtrar");
		btnFiltrarPacotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelPacotes.removeAll();
				IProject projeto = (IProject) comboBox.getSelectedItem();
				pack = analyzer.getPacotesPorProjeto(projeto);
				
				if (pack != null) {
					for (IPackageFragment iPackageFragment : pack) {
						try {
							if (iPackageFragment.getKind() == IPackageFragmentRoot.K_SOURCE && !iPackageFragment.getElementName().trim().equals("")) {
								JCheckBox checkBox = new JCheckBox(iPackageFragment.getElementName());
								checkBox.setName(iPackageFragment.getElementName());
								panelPacotes.add(checkBox);
								checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
								panelPacotes.repaint();
								panelPacotes.revalidate();
								listCheckBoxes.add(checkBox);
								listPackages.add(iPackageFragment);
							}
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					btnCalcular.setEnabled(true);
				} else {
					System.out.println("ERRO");
				}
			}
		});
		btnFiltrarPacotes.setFont(new Font("Times New Roman", Font.PLAIN, 32));
		btnFiltrarPacotes.setBounds(305, 88, 217, 52);
		contentPane.add(btnFiltrarPacotes);
	}
}
