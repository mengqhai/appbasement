package urlclassloader;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class PromptBalloon extends JDialog {

	int xx = 0;
	int yy = 0;
	int locx = 0;
	int locy = 0;
	/**
     * 
     */
	private static final long serialVersionUID = 4126088839621171364L;

	public PromptBalloon() {

		super();
		setTitle("Login");
		// setBounds(550, 250, 250, 140);
		this.setLayout(new BorderLayout());
		// Container container = getContentPane();
		// container.setLayout(new BorderLayout());
		// container.setBackground(new Color(255, 255, 173));
		setUndecorated(true);
		setVisible(true);
		this.setResizable(true);

		JarClassInfoLoader loader = new JarClassInfoLoader(
				"E:/Tools/apache-tomcat-7.0.57/lib/annotations-api.jar");
		JTable table = new JTable();
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		// dtm.setColumnIdentifiers(new String[] { "Classes" });
		dtm.addColumn("", loader.getClassList().toArray());
		table.getTableHeader().setVisible(false);
		JScrollPane scrollPane = new JScrollPane(table);
		// container.add(scrollPane);
		this.add(scrollPane, BorderLayout.CENTER);
		// container.add(new JButton("Hello"));
		// this.fitTableColumns(table);
		this.setBounds(550, 250, this.getFitTableWidth(table) + 20, 140);

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				int x0 = e.getX();
				xx = x0;
				int y0 = e.getY();
				yy = y0;
				String s = "mouse location:" + x0 + ',' + y0;
				System.out.println(s);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int adjust_x = x - xx;
				int adjust_y = y - yy;
				PromptBalloon.this
						.setLocation(locx + adjust_x, locy + adjust_y);
				Point loc = PromptBalloon.this.getLocation();
				locx = loc.x;
				locy = loc.y;
				System.out.println("moved:" + adjust_x + "," + adjust_y);
				System.out.println("dialog location:<" + locx + "> , <" + locy
						+ ">");
			}

		});
	}

	public int getFitTableWidth(JTable table) {
		JTableHeader header = table.getTableHeader();
		int rowCount = table.getRowCount();
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		int maxWidth = 0;
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(
					column.getIdentifier());
			int width = (int) table
					.getTableHeader()
					.getDefaultRenderer()
					.getTableCellRendererComponent(table,
							column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) table
						.getCellRenderer(row, col)
						.getTableCellRendererComponent(table,
								table.getValueAt(row, col), false, false, row,
								col).getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			maxWidth = Math.max(maxWidth, width
					+ table.getIntercellSpacing().width);
		}
		return maxWidth;
	}

	public void fitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();
		Enumeration<TableColumn> columns = myTable.getColumnModel()
				.getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(
					column.getIdentifier());
			int width = (int) myTable
					.getTableHeader()
					.getDefaultRenderer()
					.getTableCellRendererComponent(myTable,
							column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable
						.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable,
								myTable.getValueAt(row, col), false, false,
								row, col).getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column);
			column.setWidth(width + myTable.getIntercellSpacing().width);
		}
	}

	public static void main(String[] args) throws IOException {
		new PromptBalloon().setVisible(true);
	}
}
