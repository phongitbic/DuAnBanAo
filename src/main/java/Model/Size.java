package Model;


/**
 *
 * @author HoaNguyenVan
 */
public class Size {

    private String size_id;
    private String size_name;
    private String size_description;

    public Size() {
    }

    public Size(String size_id, String size_name, String size_description) {
        this.size_id = size_id;
        this.size_name = size_name;
        this.size_description = size_description;
    }

    public String getSize_id() {
        return size_id;
    }

    public void setSize_id(String size_id) {
        this.size_id = size_id;
    }

    public String getSize_name() {
        return size_name;
    }

    public void setSize_name(String size_name) {
        this.size_name = size_name;
    }

    public String getSize_description() {
        return size_description;
    }

    public void setSize_description(String size_description) {
        this.size_description = size_description;
    }

}
