package net.codesamples.crowsnest;

import java.util.List;

public class Environment {
    private String name;

    private List<App> apps;

    public Environment() {}

    public Environment(String name, List<App> apps) {
        this.name = name;
        this.apps = apps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    @Override
    public String toString() {
        return "Environment{" +
                "name='" + name + '\'' +
                ", apps=" + apps +
                '}';
    }
}
