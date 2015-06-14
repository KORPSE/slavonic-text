package ru.slavonictext.util;

import java.util.List;
import java.util.Map;

public class ConfBean {
    private List<String> aboveSymbols;
    private Map<String, Object> altSymbols;
    private List<String> addSymbols;

    public List<String> getAboveSymbols() {
        return aboveSymbols;
    }

    public void setAboveSymbols(List<String> aboveSymbols) {
        this.aboveSymbols = aboveSymbols;
    }

    public Map<String, Object> getAltSymbols() {
        return altSymbols;
    }

    public void setAltSymbols(Map<String, Object> altSymbols) {
        this.altSymbols = altSymbols;
    }

    public List<String> getAddSymbols() {
        return addSymbols;
    }

    public void setAddSymbols(List<String> addSymbols) {
        this.addSymbols = addSymbols;
    }
}
