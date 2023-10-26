package pkg.home.notepad;

public class Favourites {
    private String text;
    private String time;
    private int serialNumber;
    private  String imagePath;
    private  boolean isChecked;
    public static final String TABLE_NAME="Favourites";
    public static final String COL_SERIAL_NUMBER="fSerialNumber";
    public static final String COL_TEXT="fText";
    public static final String COL_TIME="fTime";
    public static final String COL_IMAGE="fImage";
    public static final String COL_IS_CHECKED="fISChecked";
    public static final String CREATE_TABLE=String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT,%s TEXT,%s TEXT)",TABLE_NAME,COL_SERIAL_NUMBER,COL_TEXT,COL_TIME,COL_IMAGE,COL_IS_CHECKED);
    public static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;
    public static final String SELECT_ALL_FAVOURITES_NOTES="SELECT * FROM "+TABLE_NAME;
    public static final String SELECT__A_NOTE_FORM_FAVOURITE="SELECT * FROM "+TABLE_NAME +" WHERE "+COL_SERIAL_NUMBER+" = ? ";

    public Favourites(){

    }
    public Favourites(int serialNumber) {
        this.serialNumber=serialNumber;
    }

    public Favourites(String text, String time, int serialNumber) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
    }
    public Favourites(int serialNumber,String text, String time) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
    }
    public Favourites(int serialNumber,String text, String time,String imagePath) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath=imagePath;
    }

    public Favourites(String text, String time, int serialNumber, String imagePath, boolean isChecked) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath = imagePath;
        this.isChecked = isChecked;
    }
    public Favourites( int serialNumber,String text, String time, boolean isChecked,String imagePath) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.isChecked = isChecked;
        this.imagePath=imagePath;
    }
    public Favourites( int serialNumber,String text, String time, boolean isChecked) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.isChecked = isChecked;
    }

    public Favourites(String text, String time, int serialNumber, String imagePath) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
