package cn.com.magnity.coresdksample.ddnwebserver.database;

import org.litepal.crud.LitePalSupport;

/**
 * 照片记录数据
 * */
public class PhotoRecordDb  extends LitePalSupport{

    private String personPath;

    private String temperPath;

    private Long date;

    private Integer temp;

    public String getPersonPath() {
        return personPath;
    }

    public void setPersonPath(String personPath) {
        this.personPath = personPath;
    }

    public String getTemperPath() {
        return temperPath;
    }

    public void setTemperPath(String temperPath) {
        this.temperPath = temperPath;
    }



    public Integer getTemp() {
        return temp;
    }

    public void setTemp(Integer temp) {
        this.temp = temp;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PhotoRecordDb{" +
                "personPath='" + personPath + '\'' +
                ", temperPath='" + temperPath + '\'' +
                ", date=" + date +
                ", temp=" + temp +
                '}';
    }
}
