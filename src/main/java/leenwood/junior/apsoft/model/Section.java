package leenwood.junior.apsoft.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.*;

public class Section {

    private String text;

    @JsonManagedReference
    private List<Section> subSections;

    @JsonBackReference
    private Section parent;

    // Конструктор, геттеры и сеттеры

    public Section() {
        this.text = "";
        this.subSections = new ArrayList<>();
    }

    public Section(String title) {
        this.text = title;
        this.subSections = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Section> getSubSections() {
        return subSections;
    }

    public void setSubSections(List<Section> subSections) {
        this.subSections = subSections;
    }

    public Section getParent() {
        return parent;
    }

    public void setParent(Section parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Section{")
                .append("title='").append(text).append('\'')
                .append(", subSections=").append(subSections)
                .append(", parent=").append(parent)
                .append('}');
        return sb.toString();
    }
}
