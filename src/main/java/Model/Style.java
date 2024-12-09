package Model;


/**
 *
 * @author HoaNguyenVan
 */
public class Style {

    private String style_id;
    private String style_name;
    private String style_description;

    public Style() {
    }

    public Style(String style_id, String style_name, String style_description) {
        this.style_id = style_id;
        this.style_name = style_name;
        this.style_description = style_description;
    }

    public String getStyle_id() {
        return style_id;
    }

    public void setStyle_id(String style_id) {
        this.style_id = style_id;
    }

    public String getStyle_name() {
        return style_name;
    }

    public void setStyle_name(String style_name) {
        this.style_name = style_name;
    }

    public String getStyle_description() {
        return style_description;
    }

    public void setStyle_description(String style_description) {
        this.style_description = style_description;
    }

}
