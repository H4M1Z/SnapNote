package pkg.home.notepad;

public class ListViewModelClass {
    private int serialNumber;
    private String text;
    private String time;
    private boolean isChecked;
    private String imagePath;
    private boolean isFavourite;
    public static final String TABLE_NAME="Notes";
    public static final String COL_SERIAL_NUMBER="nSerialNumber";
    public static final String COL_TEXT="nText";
    public static final String COL_TIME="nTime";
    public static final String COL_IMAGE="nImage";
    public static final String COL_IS_CHECKED="nIsChecked";
    public static final String COL_IS_FAVOURITE="nIsFavourite";
    public static final String CREATE_TABLE=String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT,%s TEXT,%s TEXT,%s TEXT)",TABLE_NAME,COL_SERIAL_NUMBER,COL_TEXT,COL_TIME,COL_IMAGE,COL_IS_CHECKED,COL_IS_FAVOURITE);
    public static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;
    public static final String SELECT_ALL_NOTES="SELECT * FROM "+TABLE_NAME;
    public static final String SELECT_A_NOTE="SELECT * FROM "+ TABLE_NAME +" WHERE "+ COL_TEXT +" = ? ";


    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ListViewModelClass() {
    }

    public ListViewModelClass(int serialNumber, String text, String time, boolean isChecked, String imagePath, boolean isFavourite) {
        this.serialNumber = serialNumber;
        this.text = text;
        this.time = time;
        this.isChecked = isChecked;
        this.imagePath = imagePath;
        this.isFavourite = isFavourite;
    }

    public ListViewModelClass(String text, String time) {
        this.text = text;
        this.time = time;
    }

    public ListViewModelClass(String text, String time, int serialNumber) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
    }
    public ListViewModelClass( int serialNumber,String text, String time) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
    }
    public ListViewModelClass( int serialNumber,String text, String time,String imagePath) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath=imagePath;
    }

    public ListViewModelClass(String text, String time, int serialNumber, String imagePath) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath = imagePath;
    }

    public ListViewModelClass(String text, String time, int serialNumber, String imagePath, boolean isChecked) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath = imagePath;
        this.isChecked = isChecked;
    }
    public ListViewModelClass(int serialNumber,String text, String time, boolean isChecked) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.isChecked = isChecked;
    }
    public ListViewModelClass(int serialNumber,String text, String time, boolean isChecked,String imagePath) {
        this.text = text;
        this.time = time;
        this.serialNumber = serialNumber;
        this.imagePath = imagePath;
        this.isChecked = isChecked;
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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
