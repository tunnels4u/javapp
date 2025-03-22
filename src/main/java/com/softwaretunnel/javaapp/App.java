package com.softwaretunnel.javaapp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import com.softwaretunnel.javaapp.persistance.entity.Employee;
import com.softwaretunnel.javaapp.service.EmployeeService;

/**
 * Data Tunnel App
 *
 */
public class App {

	EmployeeService employeeService;

	JFrame frame = new JFrame("Welcome to Java H2 Tunnel.");
	JButton dropSchemaButton;
	JButton createSchemaButton;
	JButton createDBButton;
	JButton dropDBButton;
	JButton addEmployeeButton;
	JTable jtable;
	JLabel jl;
	ResourceBundle resourceBundle = ResourceBundle.getBundle("system");
	boolean schemaExists = false;

	ArrayList<Employee> employees = new ArrayList<Employee>();

	public App(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public static void main(String[] args) {
		new App(new EmployeeService()).createGUI();
	}

	public void createGUI() {

		this.jl = new JLabel();
		this.jl.setBounds(50, 50, 1000, 20);
		this.jl.setForeground(Color.WHITE);

		this.addEmployeeButton = getAddButton();

		this.jtable = createTable(jl);
		JScrollPane jScrollPane = new JScrollPane(jtable);
		jScrollPane.setBounds(50, 300, 700, 200);
		jScrollPane.setOpaque(false);
		this.frame.add(jScrollPane);

		this.frame.add(addEmployeeButton);
		this.frame.add(jl);
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.addWindowListener(getWindowListener());
		this.frame.add(createBackground());
		this.frame.setSize(800, 800);

		renderFrameAndData();
		this.frame.setVisible(true);
	}

	public void renderFrameAndData() {
		try {
			addEmployeeButton.setVisible(true);
			schemaExists = true;
			refreshModel();
		} catch (Exception e) {
			jl.setText(ErrorMessage.FRAME_RENDERING_FAILED.message);
		}
	}

	public void refreshFrame() {
		frame.repaint();
	}

	public JButton getRemoveButton() {
		JButton deleteRowButton = new JButton("Delete");
		deleteRowButton.setOpaque(true);
		deleteRowButton.setContentAreaFilled(true);
		deleteRowButton.setBorderPainted(false);
		deleteRowButton.setFocusPainted(false);
		deleteRowButton.setForeground(Color.RED);
		deleteRowButton.setBackground(Color.GRAY);
		/*
		 * actionListener here won't work since this button is used inside a table with
		 * a cell renderer
		 */
		return deleteRowButton;

	}

	public JButton getSaveButton() {
		JButton deleteRowButton = new JButton("Save");
		deleteRowButton.setOpaque(true);
		deleteRowButton.setContentAreaFilled(true);
		deleteRowButton.setBorderPainted(false);
		deleteRowButton.setFocusPainted(false);
		deleteRowButton.setForeground(Color.GREEN);
		deleteRowButton.setBackground(Color.GRAY);
		/*
		 * actionListener here won't work since this button is used inside a table with
		 * a cell renderer
		 */
		return deleteRowButton;

	}

	public JButton getAddButton() {
		JButton addRowButton = new JButton("Add Employee");
		addRowButton.setForeground(Color.GREEN);
		addRowButton.setBackground(Color.GRAY);
		addRowButton.setBounds(50, 250, 200, 30);
		addRowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] row = { null, "", "", getSaveButton(), getRemoveButton() };
				((DefaultTableModel) jtable.getModel()).addRow(row);
			}
		});
		return addRowButton;

	}

	/**
	 * This code snippet was taken from
	 * https://stackoverflow.com/questions/1466240/how-to-set-an-image-as-a-background-for-frame-in-swing-gui-of-java
	 */
	public JLabel createBackground() {
		ImageIcon background = new ImageIcon(resourceBundle.getString("IMAGES_PATH") + "tunnel.jpg");
		Image img = background.getImage();
		Image temp = img.getScaledInstance(800, 1000, Image.SCALE_SMOOTH);
		background = new ImageIcon(temp);
		JLabel back = new JLabel(background);
		back.setLayout(null);
		back.setBounds(0, 0, 800, 800);
		back.setOpaque(false);
		return back;
	}

	public JTable createTable(JLabel jl) {
		String[] columnNames = { "Employee ID", "First Name", "Last Name", "Perform Save Action",
				"Perform Delete Action" };
		Object[][] data = null;

		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		JTable table = new JTable(model) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 3 || column == 4) {
					return false;
				} else {
					return true;
				}
			}
		};

		TableCellRenderer buttonRenderer = new JTableButtonRenderer();
		table.getColumn("Perform Delete Action").setCellRenderer(buttonRenderer);
		table.getColumn("Perform Save Action").setCellRenderer(buttonRenderer);
		table.setBackground(Color.GREEN);
		table.setOpaque(false);

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (col == 4) {
					if (table.getValueAt(row, 0) != null) {
						try {
							employeeService.deleteEmployeeRecord(employees.get(row));
							refreshModel();
							jl.setText(ErrorMessage.DELETE_SUCCESS.message);
						} catch (Exception e) {
							jl.setText(ErrorMessage.DELETE_FAILED.message);

						}
					} else {
						((DefaultTableModel) table.getModel()).removeRow(row);
					}
				}
				if (col == 3) {
					Employee employee = new Employee();
					employee.setId((Long) table.getValueAt(row, 0));
					employee.setFirstName((String) table.getValueAt(row, 1));
					employee.setLastName((String) table.getValueAt(row, 2));
					if (table.getValueAt(row, 0) == null) {
						try {
							employeeService.insertEmployeeRecord(employee);
							refreshModel();
							jl.setText(ErrorMessage.INSERT_SUCCESS.message);
						} catch (Exception e) {
							jl.setText(ErrorMessage.INSERT_FAILED.message);

						}
					} else {
						try {
							employeeService.updateEmployeeRecord(employee);
							refreshModel();
							jl.setText(ErrorMessage.UPDATE_SUCCESS.message);
						} catch (Exception e) {
							jl.setText(ErrorMessage.UPDATE_FAILED.message);

						}
					}
				}
			}
		});

		return table;

	}

	public Object[][] getFreshModelData() {

		Object[][] data = null;
		try {
			employees.removeAll(employees);
			employees.addAll(employeeService.getEmployeeRecords());
			if (!employees.isEmpty()) {
				data = new Object[employees.size()][5];
				for (int i = 0; i < employees.size(); i++) {
					data[i][0] = employees.get(i).getId();
					data[i][1] = employees.get(i).getFirstName();
					data[i][2] = employees.get(i).getLastName();
					data[i][3] = getSaveButton();
					data[i][4] = getRemoveButton();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			jl.setText(ErrorMessage.FETCH_FAILED.message);

		}
		return data;
	}

	public void refreshModel() {
		DefaultTableModel model = (DefaultTableModel) jtable.getModel();
		model.setRowCount(0);
		if (schemaExists) {
			Object[][] data = getFreshModelData();
			if (data != null) {
				for (int i = 0; i < data.length; i++) {
					model.addRow(data[i]);
				}
			}
		}
	}

	private static class JTableButtonRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			JButton jbutton = (JButton) value;
			return jbutton;
		}

	}

	public WindowListener getWindowListener() {

		return new WindowListener() {

			@Override
			public void windowClosed(WindowEvent e) {
				try {
					System.exit(0);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

		};
	}
}
