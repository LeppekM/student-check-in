package Database;

import java.sql.*;

public class LoadRealDatabase {

    private Connection connection;

    private String circuitDesigners = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Circuit Designers', ?, 'MSOE', 9800, 1, 'S350 A1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//1-103, 1-103
    private String wireKits = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Wire Kits', ?, 'RSR', 1195, 2, 'S350 B1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//104-207, 1-103
    private String partsBoxes = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Parts Boxes', ?, 'MSOE', 5000, 1, 'S350 C1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//208-297, 1-89
    private String cypressPSO = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Cypress PSOC5LP Dev. Bds.', ?, 'Cypress', 9343, 3, 'S350 D1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//298-378, 1-80
    private String cypressFM = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Cypress FM4 Kit', ?, 'Cypress', 6945, 3, 'S350 E1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//379-439, 1-60
    private String stampKit = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Basic Stamp Kit', ?, 'MSOE', 16700, 1, 'S350 F1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//440-466, 1-26
    private String olimex = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Olimex ECG/EMG', ?, 'MSOE', 2453, 1, 'S350 G1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//467-479, 1-12
    private String tens = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'TENS Unit', ?, 'TENS', 4999, 4, 'S350 H1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//480-493, 1-13
    private String analog = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Analog Discovery 2', ?, 'Digilent', 27900, 5, 'S350 I1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//494-579, 1-85
    private String fluke123 = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Fluke 123 Scopemeter', ?, 'Fluke', 189900, 6, 'S350 J1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//580-591, 1-11
    private String fluke43 = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Fluke 43 & 43B Power Quality Analyzer', ?, 'Fluke', 346900, 6, 'S350 K1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//592-610, 1-18
    private String nexus = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'SE1021 Nexus Kits', ?, 'Nexus', 8200, 7, 'S350 L1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//611-631, 1-20
    private String mke = "insert into parts (partID, partName, serialNumber, manufacturer, price, vendorID," +
            " location, barcode, totalQuantity, faultQuantity, availableQuantity, createdAt, createdBy)" +
            " values (?, 'Milwaukee Tool DMM', ?, 'Milwaukee Tool', 15900, 8, 'S350 M1', NULL, 1, 0, 1, date('" + new Date(System.currentTimeMillis()) + "')," +
            "NULL);";//632-682, 1-50

    public LoadRealDatabase(Connection c){
        connection = c;
    }

    public void load() {
        String[] tables = {"parts", "fault", "checkout_parts", "checkouts", "students", "vendors"};
        for (String s : tables) {
            clearDatabase(s);
        }
        int j = 0;
        for (int i = 1; i < 683; i++) {
            if (i < 104) {
                j = i;
                loadDatabase(circuitDesigners, i, j);
            } else if (i < 208) {
                j = i - 103;
                loadDatabase(wireKits, i, j);
            } else if (i < 298) {
                j = i - 207;
                loadDatabase(partsBoxes, i, j);
            } else if (i < 379) {
                j = i - 297;
                loadDatabase(cypressPSO, i, j);
            } else if (i < 440) {
                j = i - 378;
                loadDatabase(cypressFM, i, j);
            } else if (i < 467) {
                j = i - 439;
                loadDatabase(stampKit, i, j);
            } else if (i < 480) {
                j = i - 466;
                loadDatabase(olimex, i, j);
            } else if (i < 494) {
                j = i - 479;
                loadDatabase(tens, i, j);
            } else if (i < 580) {
                j = i - 493;
                loadDatabase(analog, i, j);
            } else if (i < 592) {
                j = i - 579;
                loadDatabase(fluke123, i, j);
            } else if (i < 611) {
                j = i - 591;
                loadDatabase(fluke43, i, j);
            } else if (i < 632) {
                j = i - 610;
                loadDatabase(nexus, i, j);
            } else {
                j = i - 631;
                loadDatabase(mke, i, j);
            }
        }
    }

    public void loadDatabase(String partQuery, int partIDValue, int serialValue){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(partQuery);
            preparedStatement.setInt(1, partIDValue);
            preparedStatement.setInt(2, serialValue);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void clearDatabase(String table){
        String clearTable =  "delete from " + table + ";";
        try{
            Statement statement = connection.createStatement();
            statement.execute(clearTable);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
