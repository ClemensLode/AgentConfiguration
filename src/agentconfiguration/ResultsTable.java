package agentconfiguration;

/**
 *
 * @author Clemens Lode, 1151459, University Karlsruhe (TH), clemens@lode.de
 */


import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import java.math.BigInteger;

public class ResultsTable extends AbstractTableModel
{
    /**
     * einen String, für restliche Unterschiede
     */
    public final static String[] columnNames = {
            "Configuration name",
            "Configuration ID",
            "Spread points", 
            "Average points",
            "Spread agent distance",
            "Average agent distance",
            "Spread goal distance",
            "Average goal distance",
            "Average prediction error",
            "Covered area",
            "Wasted coverage",
            "Wasted movements",
            "Half goal percentage",
            "Goal percentage"
        }; 
    public final static int COLUMN_COUNT = columnNames.length;
            
   /** Vector of Object[], this are the datas of the table */
    Vector datas = new Vector();    
    
    public int getColumnCount () {
        return columnNames.length;
    }
    
    public int getRowCount () {
        return datas.size();
    }
    
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public Object getValueAt (int row, int col) {
        Object[] array;
        do {
         array = (Object[])(datas.elementAt(row));
        } while(array.length < columnNames.length);
        return array[col];
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void addRow(Object[] row) {
        datas.add(row);
    }
    
    public Object[] getRow(int row) {
        return (Object[])datas.get(row);
    }
    
    /**
     * Searchs through the database if it contains the object array
     * @param r object array
     * @return -1 if the entry was not found, otherwise the index of the entry
     */
    public int contains(Object[] r) {
        if(datas.size() == 0) {
            return -1;
        }
        
        for(int row = 0; row < datas.size(); row++) {
            BigInteger t0 = (BigInteger)getValueAt(row, 0);
            if(!t0.equals((BigInteger)r[0])) {
                continue;
            }
            return row;
        }
        return -1;
    }
}