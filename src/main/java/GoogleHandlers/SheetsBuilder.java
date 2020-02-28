package GoogleHandlers;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SheetsBuilder {
    private static final String APP_NAME = "Tsundere";

    public static Sheets getSheets() throws GeneralSecurityException, IOException {
        HttpRequestInitializer credential = Authorizer.authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APP_NAME)
                .build();
    }
}
