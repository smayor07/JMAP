package preview;

import org.eclipse.swt.widgets. *;
import org.eclipse.ui.part.ViewPart;

public class LableView extends ViewPart{
	
	private Label label;
	
	public LableView() {
		super();
	}
	public void setFocus() {
		label.setFocus();
	}
	public void createPartControl (Composite parent) {
		label = new Label(parent,0);
		label.setText("HelloWorld");
	}
}
