package map.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.JavaModelException;

import screens.Home;
import screens.JMAP;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MetricsAnalyzerPlugin extends AbstractHandler {

	@SuppressWarnings("deprecation")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//MetricsAnalizer analyzer = new MetricsAnalizer();
		//int i = analyzer.listarTodosProjetos().length;
		
		//IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		/*Home home = null;*/
		JMAP jmap = null;
		try {
			/*home = new Home();*/
			jmap = new JMAP();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*home.show(true);*/
		jmap.show(true);
//		MessageDialog.openInformation(
//				window.getShell(),
//				"Map",
//				"Total de projetos: " + i);
		return null;
	}
}
