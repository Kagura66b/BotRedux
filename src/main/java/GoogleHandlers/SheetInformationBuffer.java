package GoogleHandlers;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.mortbay.util.IO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class SheetInformationBuffer {
    public static final String craftID = "1U4ilMqpE7rINHN3P5EEUmXr_f0hZY-geWLuPReTRWNg";
    public static final String itemsID = "12Y06S_7WHCj6gYXVZLcph0PSpe2sdiudErWamr7z0Ck";
    public static final String messageHistory = "1P9mlBeGpmW16Dv2wfArBfflJ3Osdw5RG_M44lbFqeyE";
    private static Sheets sheets;

    public static void initialize() throws GeneralSecurityException, IOException {
        sheets = SheetsBuilder.getSheets();
    }

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
                .append(craftID, "A1", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public static void appendToSheet(String ID, String range, String sheetName, List<List<String>> body) throws IOException {
        List<List<Object>> finalBody = new ArrayList<>();
        for(List<String> row:body){
            List<Object> rowBuilder = new ArrayList<>();
            for(String cell:row){
                rowBuilder.add(cell);
            }
            finalBody.add(rowBuilder);
        }

        sheets.spreadsheets().values()
                .append(ID, range, new ValueRange().setValues(finalBody))
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public static void writeToSheet(String ID, String range, List<List<Object>> body) throws IOException{
        ValueRange output = new ValueRange().setValues(body);
        sheets.spreadsheets().values()
                .append(ID, range, output)
                .setValueInputOption("RAW")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public static List<List<Object>> readFromSheet(String ID, String range) throws IOException, GeneralSecurityException {
        sheets = SheetsBuilder.getSheets();
        Sheets.Spreadsheets.Values.Get requestCurrent =
                sheets.spreadsheets().values().get(ID, range);
        List<List<Object>> result = requestCurrent.execute().getValues();

        return result;
    }


}
