package burpsuite;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.utilities.URLUtils;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

import static burpsuite.GraphQLTracker.*;

public class MyTableModel extends AbstractTableModel
{
    private final List<HttpResponseReceived> log;
    private final URLUtils urlUtils;
    private final MontoyaApi api;
    PersistedObject persistence;
    HttpParameterType httpParameterType;

    public MyTableModel(PersistedObject persist, MontoyaApi api)
    {
        this.api = api;
        this.log = new ArrayList<>();
        this.urlUtils = api.utilities().urlUtils();
        this.persistence = persist;
    }

    @Override
    public synchronized int getRowCount()
    {
        return log.size();
    }

    @Override
    public int getColumnCount()
    {
        return 6;
    }

    @Override
    public String getColumnName(int column)
    {
        return switch (column)
        {
            case 0 -> "#";
            case 1 -> "Host";
            case 2 -> "Path";
            case 3 -> "Operation Name";
            case 4 -> "Unique Identifier";
            case 5 -> "Query Value";
            default -> "";
        };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        HttpResponseReceived responseReceived = log.get(rowIndex);

        if(persistence.getString(parameterType).equals("BODY")){
            httpParameterType = HttpParameterType.BODY;
        } else if (persistence.getString(parameterType).equals("URL")) {
            httpParameterType = HttpParameterType.URL;
        } else if (persistence.getString(parameterType).equals("JSON")){
            httpParameterType = HttpParameterType.JSON;
        }

        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return responseReceived.initiatingRequest().headerValue("Host");
            case 2:
                return responseReceived.initiatingRequest().pathWithoutQuery();
            case 3:
                return responseReceived.initiatingRequest().parameterValue(persistence.getString(operationNamesParameter), httpParameterType);
            case 4:
                if(responseReceived.initiatingRequest().hasParameter(persistence.getString(docIDParameter), httpParameterType)) {
                    return responseReceived.initiatingRequest().parameterValue(persistence.getString(docIDParameter), httpParameterType);
                } else if (responseReceived.initiatingRequest().hasParameter(persistence.getString(docIDParameterTwo), httpParameterType)) {
                    return responseReceived.initiatingRequest().parameterValue(persistence.getString(docIDParameterTwo), httpParameterType);
                }else {
                    return "";
                }
            case 5:
                if(responseReceived.initiatingRequest().hasParameter(persistence.getString(variablesParameter), httpParameterType)){
                    return urlUtils.decode(responseReceived.initiatingRequest().parameterValue(persistence.getString(variablesParameter), httpParameterType));
                }else {
                    return "";
                }
            default:
                return "";
        }
    }

    public synchronized void add(HttpResponseReceived responseReceived)
    {
        int index = log.size();
        log.add(responseReceived);
        fireTableRowsInserted(index, index);
    }

    public synchronized HttpResponseReceived get(int rowIndex)
    {
        return log.get(rowIndex);
    }
}
