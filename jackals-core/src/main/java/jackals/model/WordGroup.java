package jackals.model;

import org.springframework.util.CollectionUtils;

import java.util.List;

public class WordGroup {
    List<String> words;
    Long times;

    public WordGroup() {
    }

    public WordGroup(List<String> words, Long times) {
        this.words = words;
        this.times = times;
    }

    public String fmt() {
        String out = "";
        if (CollectionUtils.isEmpty(words))
            return out;
        for (String s : words) {
            out += s;
            out += " ";
        }
        return out;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    @Override
    public boolean equals(Object obj) {
        return this.words.equals(((WordGroup) obj).words);
    }
}