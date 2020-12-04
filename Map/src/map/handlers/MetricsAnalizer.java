package map.handlers;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.util.IAttributeNamesConstants;


public class MetricsAnalizer {
	
	public IProject[] listarTodosProjetos() {

		// Retorna o Workspace Principal
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		// Retorna todos os projetos abertos no Workspace
		return root.getProjects();

	}

	public IPackageFragment[] getPacotesPorProjeto(IProject project) {
		// Verificar se é um projeto Java
		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				IJavaProject projectInf = JavaCore.create(project);
				return projectInf.getPackageFragments();
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ICompilationUnit[] getClassesPorPacote(IPackageFragment pack) {
		try {
			return pack.getCompilationUnits();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<IMethod> getMetodosPorClasse(ICompilationUnit c) {
		ArrayList<IMethod> retorno = new ArrayList<IMethod>();
		try {
			IType[] allTypes = c.getAllTypes();
			for (IType type : allTypes) {
				IMethod[] methods = type.getMethods();
				for (IMethod method : methods) {
					retorno.add(method);
				}
			}

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retorno;
	}
	
	public boolean visit(MethodDeclaration md) {
		
		if (md != null) {
			md.accept(new ASTVisitor() {
				public boolean visit (VariableDeclarationFragment fd) {
					System.out.println("Variable: " + fd);
					return false;
				}
			});
		}
		return false;
	}
	
	public static void parse(char[] string)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setSource(string);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {
			
			public boolean visit(VariableDeclarationFragment var) {
				System.out.println("variable: " + var.getName());
				return false;
			}
			
			public boolean visit(MethodDeclaration md) {
				
				if (md.getName().toString().equals("method_test2")) {
					md.accept(new ASTVisitor() {
						public boolean visit (VariableDeclarationFragment fd) {
							System.out.println("in method: " + fd);
							return false;
						}
					});
				}
				return false;
			}
		});
	}
	
	public void iniciar() {
		
		//IProject[] projetosWorkspace = listarTodosProjetos();
	}
	
	
}

