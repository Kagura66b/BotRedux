package GoogleHandlers;

import Handlers.CraftHandler;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetInformationBuffer {
    public static final String ID = "1U4ilMqpE7rINHN3P5EEUmXr_f0hZY-geWLuPReTRWNg";
    public static final String itemsID = "12Y06S_7WHCj6gYXVZLcph0PSpe2sdiudErWamr7z0Ck";
    private static Sheets sheets;

    public static List<List<String>> retrieveItemSheetData() throws IOException, GeneralSecurityException {
        sheets = SheetsBuilder.getSheets();
        String range = "Bot Friendly";
        Sheets.Spreadsheets.Values.Get request =
                sheets.spreadsheets().values().get(itemsID, range);

        List<List<Object>> response = request.execute().getValues();
        List<List<String>> sheetData = new ArrayList<>(new ArrayList<>());
        for(List<Object> row:response){
            List<String> cellText = new ArrayList<>();
            for(Object cell:row){
                cellText.add(cell.toString());
            }
            sheetData.add(cellText);
        }
        return sheetData;
    }

    public static void writeToCraft(List<Object> entry) throws IOException {
        List<List<Object>> outputToBody = new ArrayList<>();
        outputToBody.add(entry);

        ValueRange body = new ValueRange().setValues(outputToBody);

        sheets.spreadsheets().values()
                .append(ID, "A1", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public static void cancelCraft(List<Object> entry) throws IOException {
        Sheets.Spreadsheets.Values.Get request =
                sheets.spreadsheets().values().get(ID, "Sheet1");
        List<List<Object>> response = request.execute().getValues();
        response.remove(entry);

        ValueRange body = new ValueRange().setValues(response);

        sheets.spreadsheets().values().clear(ID, "Sheet1", new ClearValuesRequest()).execute();
        sheets.spreadsheets().values()
                .update(ID, "A1", body)
                .setValueInputOption("RAW")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public static List<List<String>> retrieveCraftSheetData() throws GeneralSecurityException, IOException {
        sheets = SheetsBuilder.getSheets();
        String range = "Sheet1";
        Sheets.Spreadsheets.Values.Get request =
                sheets.spreadsheets().values().get(ID, range);

        List<List<Object>> response = request.execute().getValues();
        List<List<String>> sheetData = new ArrayList<>(new ArrayList<>());
        for(List<Object> row:response){
            List<String> cellText = new ArrayList<>();
            for(Object cell:row){
                cellText.add(cell.toString());
            }
            sheetData.add(cellText);
        }
        return sheetData;
    }

}
