package ke.co.infiware.uaa;

/**
 * @author Denis Gitonga
 */
public class CurrencySymbol {
    private Long id;
    private String symbol;
    private String country;

    public CurrencySymbol() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
