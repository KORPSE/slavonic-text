package ru.slavonictext.util;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfBean {
    private AboveCharacters aboveSymbols;
    private Map<String, Object> altSymbols;

    public AboveCharacters getAboveSymbols() {
        return aboveSymbols;
    }

    public void setAboveSymbols(AboveCharacters aboveSymbols) {
        this.aboveSymbols = aboveSymbols;
    }

    public Map<String, Object> getAltSymbols() {
        return altSymbols;
    }

    public void setAltSymbols(Map<String, Object> altSymbols) {
        this.altSymbols = altSymbols;
    }

    public static class AboveCharacters {

        private List<String> accents;
        private List<String> aspirations;
        private List<String> titlo;

        public List<String> getAccents() {
            return accents;
        }

        public void setAccents(List<String> accents) {
            this.accents = accents;
        }

        public List<String> getAspirations() {
            return aspirations;
        }

        public void setAspirations(List<String> aspirations) {
            this.aspirations = aspirations;
        }

        public List<String> getTitlo() {
            return titlo;
        }

        public void setTitlo(List<String> titlo) {
            this.titlo = titlo;
        }

        public AboveCharacters() {
            super();
        }

        public Set<String> all() {
            Set<String> result = Sets.newHashSet(accents);
            result.addAll(aspirations);
            result.addAll(titlo);
            return result;
        }
    }
}
