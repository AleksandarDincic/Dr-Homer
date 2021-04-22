package imageEditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import imageEditor.BasicOperation;
import imageEditor.CompositeOperation;
import imageEditor.EditorException;
import imageEditor.MainWindow;
import imageEditor.Operation;

@SuppressWarnings("serial")
public class AddOperationDialog extends EditorDialog {
	private List operationsList;
	private ArrayList<TextField> paramFields = new ArrayList<TextField>();
	private Panel paramsPanel;
	private Button addButton;

	public AddOperationDialog(MainWindow owner) {
		super(owner, "Add operation", 480, 120);
	}

	@Override
	protected void addComponents() {
		operationsList = new List();
		operationsList.setMultipleMode(false);

		paramsPanel = new Panel();

		Panel eastPanel = new Panel(new BorderLayout());
		Label paramsLabel = new Label("Parameters:");
		paramsLabel.setAlignment(Label.CENTER);
		eastPanel.add(paramsLabel, BorderLayout.NORTH);
		eastPanel.add(paramsPanel, BorderLayout.CENTER);

		Panel buttonPanel = new Panel();
		addButton = new Button("Add");
		buttonPanel.add(addButton);

		add(operationsList, BorderLayout.WEST);
		add(eastPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});

		operationsList.addItemListener(e -> {
			if (operationsList.getSelectedItem() != null) {
				Operation operation = null;
				try {
					operation = image.getOperation(operationsList.getSelectedItem());
				} catch (EditorException e1) {
					// nece se desiti
				}
				paramFields.clear();
				paramsPanel.removeAll();
				paramsPanel.revalidate();

				if (operation instanceof BasicOperation) {
					BasicOperation basicOperation = (BasicOperation) operation;
					for (int i = 0; i < basicOperation.getNumOfParams(); i++) {
						TextField newField = new TextField(3);
						paramFields.add(newField);
						paramsPanel.add(newField);
					}

					paramsPanel.revalidate();
				}
			}
		});

		addButton.addActionListener(e -> {
			if (operationsList.getSelectedItem() != null) {
				try {
					Operation operation = null;

					operation = image.getOperation(operationsList.getSelectedItem());

					if (operation instanceof BasicOperation) {
						BasicOperation basicOperation = ((BasicOperation) operation).clone();
						for (int i = 0; i < basicOperation.getNumOfParams(); i++) {
							basicOperation.setParam(i, Double.parseDouble(paramFields.get(i).getText()));
						}
						operation = basicOperation;
					} else {
						CompositeOperation compositeOperation = ((CompositeOperation) operation).clone();
						operation = compositeOperation;
					}

					image.addOperation(operation);
					mwOwner.updateOperationsBuffer();
					setVisible(false);
				} catch (EditorException e1) {
					// nece se desiti
				} catch (NumberFormatException e1) {
					mwOwner.showException(new EditorException("Entered numbers not valid"));
				}
			}
		});
	}

	@Override
	public void showDialog() {
		java.util.List<String> operations = image.getAvailableOperations();
		operationsList.removeAll();
		for (String s : operations)
			operationsList.add(s);

		paramFields.clear();
		paramsPanel.removeAll();
		paramsPanel.revalidate();

		setVisible(true);
	}

}