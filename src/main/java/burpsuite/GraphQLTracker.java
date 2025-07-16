package burpsuite;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.persistence.PersistedList;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

import static burp.api.montoya.http.message.HttpRequestResponse.httpRequestResponse;
import static burp.api.montoya.http.message.params.HttpParameter.parameter;
import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class GraphQLTracker implements BurpExtension {

    private MontoyaApi api;
    DateTimeFormatter dateTimeFormatter;
    static final String graphQLEndpoint = "graphQLEndpoint";
    static final String variablesParameter = "variablesParameter";
    static final String docIDParameter = "docIDParameter";
    static final String docIDParameterTwo = "docIDParameterTwo";
    static final String operationNamesParameter = "operationNamesParameter";
    static final String parameterType = "parameterType";
    HttpParameterType httpParameterType;

    @Override
    public void initialize(MontoyaApi api) {

        this.dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
        this.api = api;

        api.extension().setName("GraphQL Tracker");
        PersistedObject persist =  api.persistence().extensionData();
        PersistedList<String> UniquedocID = persist.getStringList("UniquedocID") != null
                ? persist.getStringList("UniquedocID")
                : PersistedList.persistedStringList();

        if (UniquedocID.isEmpty())
        {
            persist.setStringList("UniquedocID", UniquedocID);
        }

        String graphql = persist.getString(graphQLEndpoint);

        if (graphql == null) {
            graphql = "graphql";
        }

        persist.setString(graphQLEndpoint, graphql);
        String variables = persist.getString(variablesParameter);

        if (variables == null) {
            variables = "variables";
        }

        persist.setString(variablesParameter, variables);
        String docID = persist.getString(docIDParameter);

        if (docID == null) {
            docID = "doc_id";
        }

        persist.setString(docIDParameter, docID);
        String docIDTwo = persist.getString(docIDParameterTwo);

        if (docIDTwo == null) {
            docIDTwo = "client_doc_id";
        }
        persist.setString(docIDParameterTwo, docIDTwo);
        String operationNames = persist.getString(operationNamesParameter);
        if (operationNames == null) {
            operationNames = "fb_api_req_friendly_name";
        }
        persist.setString(operationNamesParameter, operationNames);
        String requestData = persist.getString(parameterType);

        if (requestData == null) {
            requestData = "BODY";
        }
        persist.setString(parameterType, requestData);
        MyTableModel table = new MyTableModel(persist,api);
        api.userInterface().registerSuiteTab("GraphQL Tracker", LoggerTab(table,persist));
        api.http().registerHttpHandler(new TableModelHandler(table,persist,UniquedocID));
        api.userInterface().registerHttpResponseEditorProvider(creationContext -> new MyProvidedHttpResponseEditor(api));
    }

    private Component LoggerTab(MyTableModel table, PersistedObject persist) {

        JTabbedPane graphQLGUI = new JTabbedPane();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        UserInterface userInterface = api.userInterface();
        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);
        JTabbedPane requestTab = new JTabbedPane();
        requestTab.addTab("Request", requestViewer.uiComponent());

        JTabbedPane responseTab = new JTabbedPane();
        responseTab.addTab("Response", responseViewer.uiComponent());
        JSplitPane splitTabs = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitTabs.setLeftComponent(requestTab);
        splitTabs.setRightComponent(responseTab);
        splitPane.setRightComponent(splitTabs);
        JTable jTable = new JTable(table) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
                // Get the selected row data
                HttpResponseReceived responseReceived = table.get(convertRowIndexToModel(rowIndex));
                if (responseReceived.initiatingRequest().pathWithoutQuery().contains(persist.getString(graphQLEndpoint))) {

                    if(persist.getString(parameterType).equals("BODY")){
                        httpParameterType = HttpParameterType.BODY;
                    } else if (persist.getString(parameterType).equals("URL")) {
                        httpParameterType = HttpParameterType.URL;
                    } else if (persist.getString(parameterType).equals("JSON")){
                        httpParameterType = HttpParameterType.JSON;
                    }

                    if (responseReceived.initiatingRequest().hasParameter(persist.getString(variablesParameter), httpParameterType)){
                        HttpRequest modifiedRequest = responseReceived.initiatingRequest().withAddedParameters(parameter("query", responseReceived.initiatingRequest().parameterValue(persist.getString(variablesParameter), httpParameterType),httpParameterType));
                        HttpRequest remove = modifiedRequest.withRemovedParameters(parameter(persist.getString(variablesParameter),responseReceived.initiatingRequest().parameterValue(persist.getString(variablesParameter), httpParameterType),httpParameterType));
                        requestViewer.setRequest(remove);
                        responseViewer.setResponse(responseReceived);

                    } else {
                        requestViewer.setRequest(responseReceived.initiatingRequest());
                        responseViewer.setResponse(responseReceived);
                    }
                } else {
                    requestViewer.setRequest(responseReceived.initiatingRequest());
                    responseViewer.setResponse(responseReceived);
                }
            }
        };

        jTable.setRowHeight(30);
        jTable.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table);
        jTable.setRowSorter(sorter);
        sorter.setComparator(0, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2); // Compare as integers
            }
        });

        jTable.getRowSorter().toggleSortOrder(0);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        TableColumn ID = jTable.getColumnModel().getColumn(0);
        ID.setMinWidth(50);
        ID.setMaxWidth(100);
        TableColumn Host = jTable.getColumnModel().getColumn(1);
        Host.setMinWidth(250);
        Host.setMaxWidth(300);
        TableColumn Path = jTable.getColumnModel().getColumn(2);
        Path.setMinWidth(200);
        Path.setMaxWidth(250);
        TableColumn docID = jTable.getColumnModel().getColumn(4);
        docID.setMinWidth(350);
        docID.setMaxWidth(400);

        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Create the context menu
        JPopupMenu popupMenu = new JPopupMenu();

        // Second menu item: Send to Repeater
        JMenuItem sendToRepeaterMenuItem = new JMenuItem("Send to Repeater");
        popupMenu.add(sendToRepeaterMenuItem);

        // First menu item: Send to Intruder
        JMenuItem sendToIntruderMenuItem = new JMenuItem("Send to Intruder");
        popupMenu.add(sendToIntruderMenuItem);

        // Third menu item: Send to Organizer
        JMenuItem sendToOrganizerMenuItem = new JMenuItem("Send to Organizer");
        popupMenu.add(sendToOrganizerMenuItem);

        // Add action listener for "Send to Repeater"
        Action sendToRepeaterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = jTable.getSelectedRows();
                for (int selectedRow : selectedRows) {
                    HttpResponseReceived responseReceived = table.get(jTable.convertRowIndexToModel(selectedRow));
                    if (responseReceived.initiatingRequest().pathWithoutQuery().contains(persist.getString(graphQLEndpoint))) {
                        if(persist.getString(parameterType).equals("BODY")){
                            httpParameterType = HttpParameterType.BODY;
                        } else if (persist.getString(parameterType).equals("URL")) {
                            httpParameterType = HttpParameterType.URL;
                        } else if (persist.getString(parameterType).equals("JSON")){
                            httpParameterType = HttpParameterType.JSON;
                        }
                        if (responseReceived.initiatingRequest().hasParameter(persist.getString(variablesParameter), httpParameterType)){
                            HttpRequest modifiedRequest = responseReceived.initiatingRequest().withAddedParameters(parameter("query", responseReceived.initiatingRequest().parameterValue(persist.getString(variablesParameter), httpParameterType),httpParameterType));
                            HttpRequest remove = modifiedRequest.withRemovedParameters(parameter(persist.getString(variablesParameter),responseReceived.initiatingRequest().parameterValue(persist.getString(variablesParameter), httpParameterType),httpParameterType));

                            api.repeater().sendToRepeater(remove, String.valueOf(jTable.convertRowIndexToModel(selectedRow) + 1));

                        }
                    } else {
                        api.repeater().sendToRepeater(responseReceived.initiatingRequest(),dateTimeFormatter.format(LocalDateTime.now()));
                    }
                }
            }
        };

        // Add action listener for "Send to Intruder"
        Action sendToIntruderAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = jTable.getSelectedRows();
                for (int selectedRow : selectedRows) {
                    HttpResponseReceived responseReceived = table.get(jTable.convertRowIndexToModel(selectedRow));
                    api.intruder().sendToIntruder(responseReceived.initiatingRequest());
                }
            }
        };

        Action sendToOrganizerAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = jTable.getSelectedRows();
                for (int selectedRow : selectedRows) {
                    int modelRow = jTable.convertRowIndexToModel(selectedRow);
                    // Send the request/response pair to the Organize
                    HttpResponseReceived responseReceived = table.get(jTable.convertRowIndexToModel(selectedRow));
                    if (responseReceived.initiatingRequest().pathWithoutQuery().contains(persist.getString(graphQLEndpoint))) {
                        if(persist.getString(parameterType).equals("BODY")){
                            httpParameterType = HttpParameterType.BODY;
                        } else if (persist.getString(parameterType).equals("URL")) {
                            httpParameterType = HttpParameterType.URL;
                        } else if (persist.getString(parameterType).equals("JSON")){
                            httpParameterType = HttpParameterType.JSON;
                        }
                        if (responseReceived.initiatingRequest().hasParameter(persist.getString(variablesParameter), httpParameterType)){
                            HttpRequest modifiedRequest = responseReceived.initiatingRequest().withAddedParameters(parameter("query", responseReceived.initiatingRequest().parameterValue(persist.getString(variablesParameter), httpParameterType),httpParameterType));
                            HttpRequest remove = modifiedRequest.withRemovedParameters(parameter(persist.getString(variablesParameter),responseReceived.initiatingRequest().parameterValue(persist.getString(variablesParameter), httpParameterType),httpParameterType));
                            api.organizer().sendToOrganizer(httpRequestResponse(remove, responseReceived));
                        }
                    } else {
                        api.organizer().sendToOrganizer(httpRequestResponse(responseReceived.initiatingRequest(), responseReceived));
                    }
                }
            }
        };

        sendToRepeaterMenuItem.addActionListener(sendToRepeaterAction);
        sendToIntruderMenuItem.addActionListener(sendToIntruderAction);
        sendToOrganizerMenuItem.addActionListener(sendToOrganizerAction);
        // Attach the context menu to the table
        jTable.setComponentPopupMenu(popupMenu);

        // Bind "Ctrl + R" to sendToRepeaterAction (if not already done)
        KeyStroke repeaterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(repeaterKeyStroke, "sendToRepeater");
        jTable.getActionMap().put("sendToRepeater", sendToRepeaterAction);
        // Bind "Ctrl + I" to sendToIntruderAction
        KeyStroke intruderKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(intruderKeyStroke, "sendToIntruder");
        jTable.getActionMap().put("sendToIntruder", sendToIntruderAction);

        KeyStroke organizerKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(organizerKeyStroke, "sendToOrganizer");
        jTable.getActionMap().put("sendToOrganizer", sendToOrganizerAction);

        JScrollPane scrollPane = new JScrollPane(jTable);
        splitPane.setLeftComponent(scrollPane);
        graphQLGUI.addTab("Logger",splitPane);
        graphQLGUI.addTab("Settings",new Settings(persist));
        return graphQLGUI;
    }


}
