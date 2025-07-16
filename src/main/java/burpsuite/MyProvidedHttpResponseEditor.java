package burpsuite;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import java.awt.*;

import static burp.api.montoya.core.ByteArray.byteArray;
import burp.api.montoya.utilities.json.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MyProvidedHttpResponseEditor implements ExtensionProvidedHttpResponseEditor
{
    private final RawEditor responseEditor;
    HttpRequestResponse requestResponse;
    private final JsonUtils jsonUtils;
    public MyProvidedHttpResponseEditor(MontoyaApi api)
    {
        responseEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
        this.jsonUtils = api.utilities().jsonUtils();
    }

    @Override
    public HttpResponse getResponse()
    {
        return requestResponse.response();
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.requestResponse = requestResponse;
        HttpResponse response = requestResponse.response();
        String jsonInput = response.bodyToString();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object jsonObject = gson.fromJson(jsonInput, Object.class);
        String prettyJson = gson.toJson(jsonObject);
        this.responseEditor.setContents(byteArray(prettyJson));
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse)
    {
        this.requestResponse = requestResponse;
        HttpResponse response = requestResponse.response();
        if ( response != null && jsonUtils.isValidJson(response.bodyToString()) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String caption()
    {
        return "JSON body processor";
    }

    @Override
    public Component uiComponent()
    {
        return responseEditor.uiComponent();
    }

    @Override
    public Selection selectedData()
    {
        return responseEditor.selection().isPresent() ? responseEditor.selection().get() : null;
    }

    @Override
    public boolean isModified()
    {
        return responseEditor.isModified();
    }
}