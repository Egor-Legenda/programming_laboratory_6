package org.example.common.request;

import org.example.common.data.City;
import org.example.common.data.Climate;

public class CommandsRequest extends Request {
    public long id;
    public City city;
    public String line;
    public Climate climate;
    public CommandsRequest(String name) {
        super(name);
    }
    public CommandsRequest(String name, long id){
        super(name);
        this.id=id;
    }
    public CommandsRequest(String name, long id, City city){
        super(name);
        this.id=id;
        this.city=city;
    }
    public CommandsRequest(String name, Climate climate){
        super(name);
        this.climate=climate;
    }
    public CommandsRequest(String name, String line){
        super(name);
        this.line=line;
    }

    public long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public String getLine() {
        return line;
    }

    public Climate getClimate() {
        return climate;
    }
}
