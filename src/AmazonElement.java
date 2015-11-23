/**
 * Created with IntelliJ IDEA.
 * User: rbs
 * Date: 27.11.13
 * Time: 09:53
 * To change this template use File | Settings | File Templates.
 */
public class AmazonElement {

    String name;
    String time;
    String href;
    String img_href;
    String price;
    int percent_used = -1;
    private String startingTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        if(href.contains("amazon.de"))
            this.href = href;
        else
            this.href = "http://www.amazon.de/" + href;
    }

    public String getImg_href() {
        return img_href;
    }

    public void setImg_href(String img_href) {
        this.img_href = img_href;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPercent_used() {
        return percent_used;
    }

    public void setPercent_used(int percent_used) {
        this.percent_used = percent_used;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public String generateHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='ama_elem'>");
        sb.append("<img src='" + img_href + "'class='name'></img>");
        sb.append("<div class='name'><a href='" + href + "'>" + escapeHTML(name) + "</a></div>");

        if(percent_used > -1) {
            sb.append("<div class='perc_used'><div class='bar' style='width: " + percent_used + "%'>" + percent_used + "%</div></div>");
            sb.append("<div class='price'>" + escapeHTML(price) + "</div>");
        } else {
            sb.append("<div class='time'>Startet: " + startingTime + "</div>");
        }




        sb.append("</div>");

        return sb.toString();
    }

    private static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
