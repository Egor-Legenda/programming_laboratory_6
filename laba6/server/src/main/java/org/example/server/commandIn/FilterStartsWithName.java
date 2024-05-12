package org.example.server.commandIn;

import org.example.server.data.City;

public class FilterStartsWithName extends Commands implements Command{
    @Override
    public String execution(String arg, City city){
        return man.filterStartsWithNameManager(arg);
    }

}
