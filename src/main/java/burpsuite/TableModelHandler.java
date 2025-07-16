package burpsuite;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.persistence.PersistedList;
import burp.api.montoya.persistence.PersistedObject;
import static burp.api.montoya.core.ToolType.PROXY;
import static burp.api.montoya.core.ToolType.REPEATER;
import static burp.api.montoya.http.message.params.HttpParameter.parameter;
import static burpsuite.GraphQLTracker.*;

public class TableModelHandler implements HttpHandler {

    private final MyTableModel table;
    private final PersistedList<String> UniqueOperationName;
    PersistedObject persistence;
    HttpParameterType httpParameterType;

    public TableModelHandler(MyTableModel table, PersistedObject persist, PersistedList<String> UniqueOperationName) {
        this.table = table;
        this.UniqueOperationName = UniqueOperationName;
        this.persistence = persist;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {

        if (requestToBeSent.isInScope() && requestToBeSent.toolSource().isFromTool(REPEATER)) {
            if (requestToBeSent.pathWithoutQuery().contains(persistence.getString(graphQLEndpoint))) {
                if (persistence.getString(parameterType).equals("BODY")) {
                    httpParameterType = HttpParameterType.BODY;
                } else if (persistence.getString(parameterType).equals("URL")) {
                    httpParameterType = HttpParameterType.URL;
                } else if (persistence.getString(parameterType).equals("JSON")) {
                    httpParameterType = HttpParameterType.JSON;
                }
                if (requestToBeSent.hasParameter("query", httpParameterType)) {
                    HttpRequest modifiedRequest = requestToBeSent.withAddedParameters(parameter(persistence.getString(variablesParameter), requestToBeSent.parameterValue("query", httpParameterType), httpParameterType));
                    HttpRequest remove = modifiedRequest.withRemovedParameters(parameter("query", requestToBeSent.parameterValue("query", httpParameterType), httpParameterType));
                    return RequestToBeSentAction.continueWith(remove);
                }
            }
        }
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {

        Annotations annotations = responseReceived.annotations();
        if (responseReceived.initiatingRequest().isInScope() && responseReceived.toolSource().isFromTool(PROXY)) {

            if (responseReceived.initiatingRequest().pathWithoutQuery().contains(persistence.getString(graphQLEndpoint))) {

                if (persistence.getString(parameterType).equals("BODY")) {
                    httpParameterType = HttpParameterType.BODY;
                } else if (persistence.getString(parameterType).equals("URL")) {
                    httpParameterType = HttpParameterType.URL;
                } else if (persistence.getString(parameterType).equals("JSON")) {
                    httpParameterType = HttpParameterType.JSON;
                }

                if (responseReceived.initiatingRequest().hasParameter(persistence.getString(operationNamesParameter), httpParameterType)) {
                    if (!UniqueOperationName.contains(responseReceived.initiatingRequest().parameterValue(persistence.getString(operationNamesParameter), httpParameterType))) {
                        UniqueOperationName.add(responseReceived.initiatingRequest().parameterValue(persistence.getString(operationNamesParameter), httpParameterType));
                        annotations = annotations.withNotes(responseReceived.initiatingRequest().parameterValue(persistence.getString(operationNamesParameter), httpParameterType));
                        table.add(responseReceived);
                        return ResponseReceivedAction.continueWith(responseReceived, annotations);
                    }
                } else {
                    return ResponseReceivedAction.continueWith(responseReceived);
                }
            }
            return ResponseReceivedAction.continueWith(responseReceived);
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
